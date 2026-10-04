package com.faysalaziz.heromaker.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.faysalaziz.heromaker.HeroViewModel
import com.faysalaziz.heromaker.Screen
import com.faysalaziz.heromaker.ui.theme.Bone
import com.faysalaziz.heromaker.ui.theme.HeroMakerTheme
import com.faysalaziz.heromaker.ui.theme.Ink

@Composable
fun HeroMakerApp(vm: HeroViewModel) {
    HeroMakerTheme {
        Surface(Modifier.fillMaxSize(), color = Ink, contentColor = Bone) {
            val context = LocalContext.current
            LaunchedEffect(vm) {
                vm.messages.collect { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            }

            val atStart = vm.screen == Screen.Step(0) && !vm.showSettings
            BackHandler(enabled = !atStart) { vm.onBack() }

            Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                when (val screen = vm.screen) {
                    // key() gives each step its own scroll position
                    is Screen.Step -> key(screen.index) { WizardScreen(vm, screen.index) }
                    Screen.Review -> ReviewScreen(vm)
                    Screen.Result -> ResultScreen(vm)
                }
                if (vm.showSettings) SettingsScreen(vm)
            }
        }
    }
}
