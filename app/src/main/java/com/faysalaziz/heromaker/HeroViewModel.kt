package com.faysalaziz.heromaker

import android.app.Application
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.faysalaziz.heromaker.data.AppSettings
import com.faysalaziz.heromaker.data.GallerySaver
import com.faysalaziz.heromaker.data.HeroSpec
import com.faysalaziz.heromaker.data.OpenAiClient
import com.faysalaziz.heromaker.data.OpenAiException
import com.faysalaziz.heromaker.data.Options
import com.faysalaziz.heromaker.data.PromptBuilder
import com.faysalaziz.heromaker.data.SecureStore
import com.faysalaziz.heromaker.data.SettingsStore
import com.faysalaziz.heromaker.data.WizardStep
import com.faysalaziz.heromaker.data.isStepComplete
import com.faysalaziz.heromaker.data.randomised
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Which screen the main flow is on. Settings is shown on top of it. */
sealed interface Screen {
    data class Step(val index: Int) : Screen
    data object Review : Screen
    data object Result : Screen
}

/** The state of the image request. */
sealed interface Generation {
    data object Idle : Generation
    data object Loading : Generation
    class Success(val bytes: ByteArray, val image: ImageBitmap) : Generation
    data class Failed(val message: String) : Generation
}

class HeroViewModel(app: Application) : AndroidViewModel(app) {
    private val secureStore = SecureStore(app)
    private val settingsStore = SettingsStore(app)

    var spec by mutableStateOf(HeroSpec())
        private set
    var screen by mutableStateOf<Screen>(Screen.Step(0))
        private set
    var showSettings by mutableStateOf(false)
        private set
    var settings by mutableStateOf(settingsStore.load())
        private set
    var models by mutableStateOf(AppSettings.FALLBACK_MODELS)
        private set
    var modelsStatus by mutableStateOf<String?>(null)
        private set
    var modelsLoading by mutableStateOf(false)
        private set
    var generation by mutableStateOf<Generation>(Generation.Idle)
        private set

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    /** One-off messages for the screen to show as a toast. */
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    private var generationJob: Job? = null

    // ----- Wizard choices -----

    val steps: List<WizardStep> = WizardStep.entries

    fun currentStep(): WizardStep? = (screen as? Screen.Step)?.let { steps[it.index] }

    fun canContinue(step: WizardStep): Boolean = spec.isStepComplete(step)

    fun pickNature(nature: String) {
        spec = if (spec.nature == nature) spec else spec.copy(nature = nature, kind = "")
    }

    fun pickGender(value: String) { spec = spec.copy(gender = value) }
    fun pickKind(value: String) { spec = spec.copy(kind = value) }
    fun pickPersonality(value: String) { spec = spec.copy(personality = value) }
    fun pickStyle(value: String) { spec = spec.copy(style = value) }
    fun pickSetting(value: String) { spec = spec.copy(setting = value) }
    fun pickPose(value: String) { spec = spec.copy(pose = value) }
    fun setRemarks(value: String) { spec = spec.copy(remarks = value) }

    fun pickBackground(value: String) { spec = spec.copy(background = value, customBackground = "") }
    fun setCustomBackground(value: String) {
        spec = spec.copy(customBackground = value, background = if (value.isNotBlank()) "" else spec.background)
    }

    fun togglePower(value: String) {
        toggle(spec.powers, value, Options.MAX_POWERS)?.let { spec = spec.copy(powers = it) }
    }

    fun toggleOutfit(value: String) {
        toggle(spec.outfit, value, Options.MAX_OUTFIT)?.let { spec = spec.copy(outfit = it) }
    }

    private fun toggle(list: List<String>, value: String, max: Int): List<String>? = when {
        value in list -> list - value
        list.size < max -> list + value
        else -> {
            _messages.tryEmit("You can pick up to $max.")
            null
        }
    }

    fun surprise() {
        spec = spec.randomised()
        screen = Screen.Review
    }

    // ----- Navigation -----

    fun next() {
        val s = screen as? Screen.Step ?: return
        screen = if (s.index == steps.lastIndex) Screen.Review else Screen.Step(s.index + 1)
    }

    fun goToStep(index: Int) { screen = Screen.Step(index) }

    fun goToReview() {
        generationJob?.cancel()
        generation = Generation.Idle
        screen = Screen.Review
    }

    /** Drops the current hero and any image request, and goes back to the first wizard step. */
    fun startOver() {
        generationJob?.cancel()
        generationJob = null
        generation = Generation.Idle
        spec = HeroSpec()
        screen = Screen.Step(0)
    }

    fun openSettings() { showSettings = true }
    fun closeSettings() { showSettings = false }

    /** Handles the system Back button. Returns false when there is nothing left to go back to. */
    fun onBack(): Boolean = when {
        showSettings -> { showSettings = false; true }
        screen is Screen.Result -> { goToReview(); true }
        screen is Screen.Review -> { screen = Screen.Step(steps.lastIndex); true }
        screen is Screen.Step && (screen as Screen.Step).index > 0 -> {
            screen = Screen.Step((screen as Screen.Step).index - 1)
            true
        }
        else -> false
    }

    // ----- Settings -----

    fun loadApiKey(): String = secureStore.loadApiKey()

    fun saveSettings(apiKey: String, newSettings: AppSettings) {
        secureStore.saveApiKey(apiKey)
        settingsStore.save(newSettings)
        settings = newSettings
        showSettings = false
        _messages.tryEmit("Settings saved")
    }

    /** Asks OpenAI which image models this key can use. */
    fun refreshModels(apiKey: String) {
        val key = apiKey.trim()
        if (key.isEmpty()) {
            modelsStatus = "Enter your API key first."
            return
        }
        modelsLoading = true
        modelsStatus = null
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { runCatching { OpenAiClient.listImageModels(key) } }
            modelsLoading = false
            result.onSuccess { list ->
                if (list.isEmpty()) {
                    modelsStatus = "No image models found for this key. Showing the built-in list."
                } else {
                    models = list
                    modelsStatus = "Found ${list.size} image model(s)."
                }
            }.onFailure { modelsStatus = (it as? OpenAiException)?.message ?: "Could not load the model list." }
        }
    }

    // ----- Generation -----

    fun promptPreview(): String = PromptBuilder.build(spec)

    fun generate() {
        val apiKey = secureStore.loadApiKey()
        if (apiKey.isBlank()) {
            showSettings = true
            _messages.tryEmit("Add your OpenAI API key first.")
            return
        }
        screen = Screen.Result
        generation = Generation.Loading
        val prompt = PromptBuilder.build(spec)
        val current = settings
        generationJob?.cancel()
        generationJob = viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching { OpenAiClient.generateImage(apiKey, current.model, prompt, current.size, current.quality) }
            }
            generation = result.fold(
                onSuccess = { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap == null) Generation.Failed("The image came back in a format the app cannot show.")
                    else Generation.Success(bytes, bitmap.asImageBitmap())
                },
                onFailure = { Generation.Failed((it as? OpenAiException)?.message ?: "Something went wrong. Try again.") },
            )
        }
    }

    fun saveToGallery() {
        val success = generation as? Generation.Success ?: return
        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) { GallerySaver.savePng(getApplication(), success.bytes) }
            _messages.tryEmit(if (ok) "Saved to Pictures/HeroMaker" else "Could not save the image.")
        }
    }

    fun message(text: String) { _messages.tryEmit(text) }
}
