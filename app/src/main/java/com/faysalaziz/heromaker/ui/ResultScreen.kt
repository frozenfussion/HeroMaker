package com.faysalaziz.heromaker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.faysalaziz.heromaker.Generation
import com.faysalaziz.heromaker.HeroViewModel
import com.faysalaziz.heromaker.ui.theme.Bone
import com.faysalaziz.heromaker.ui.theme.Danger
import com.faysalaziz.heromaker.ui.theme.Gold
import com.faysalaziz.heromaker.ui.theme.GoldDeep
import com.faysalaziz.heromaker.ui.theme.GoldLight
import com.faysalaziz.heromaker.ui.theme.Muted
import androidx.compose.foundation.layout.Row

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultScreen(vm: HeroViewModel) {
    val state = vm.generation
    val context = LocalContext.current

    // Android 9 (API 28) needs a permission to write to the gallery; newer versions do not.
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) vm.saveToGallery() else vm.message("Allow storage access to save the image.")
    }
    fun save() {
        val needsPermission = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        if (needsPermission) permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE) else vm.saveToGallery()
    }

    Column(Modifier.fillMaxSize()) {
        AppTopBar(onSurprise = null, onSettings = vm::openSettings)
        StepProgress(total = vm.steps.size, current = vm.steps.size)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (state) {
                Generation.Idle, Generation.Loading -> Column(
                    Modifier.fillMaxWidth().padding(vertical = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    CircularProgressIndicator(color = GoldLight, trackColor = GoldDeep.copy(alpha = 0.3f), modifier = Modifier.size(54.dp))
                    Text("Forging your hero…", style = TextStyle(fontSize = 16.sp, color = GoldLight))
                    Text("This can take up to a minute.", style = HintStyle)
                }

                is Generation.Success -> {
                    Box(Modifier.fillMaxWidth().border(1.dp, GoldDeep)) {
                        Image(
                            bitmap = state.image,
                            contentDescription = "Your generated hero",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    val spec = vm.spec
                    FlowRow(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        listOf(spec.gender, spec.kind, spec.style, spec.setting, vm.settings.model).forEach { tag ->
                            Text(
                                tag,
                                modifier = Modifier.border(1.dp, GoldDeep.copy(alpha = 0.6f)).padding(horizontal = 8.dp, vertical = 4.dp),
                                style = TextStyle(fontSize = 12.sp, color = Muted),
                            )
                        }
                    }
                }

                is Generation.Failed -> Column(
                    Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("Could not make the hero", style = DisplayStyle.copy(fontSize = 22.sp, color = Danger), textAlign = TextAlign.Center)
                    Text(state.message, style = TextStyle(fontSize = 15.sp, color = Bone), textAlign = TextAlign.Center)
                    Text("Check Settings, then try again.", style = TextStyle(fontSize = 13.sp, color = Gold))
                }
            }
        }
        BottomBar {
            GhostButton("Edit", onClick = vm::goToReview, modifier = Modifier.weight(1f))
            when (state) {
                is Generation.Success -> {
                    GhostButton("Retry", onClick = vm::generate, modifier = Modifier.weight(1f))
                    GoldButton("Save", onClick = { save() }, modifier = Modifier.weight(1.3f))
                }
                is Generation.Failed -> GoldButton("Try again", onClick = vm::generate, modifier = Modifier.weight(1.6f))
                else -> GoldButton("Save", onClick = {}, modifier = Modifier.weight(1.6f), enabled = false)
            }
        }
    }
}
