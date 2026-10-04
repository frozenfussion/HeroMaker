package com.faysalaziz.heromaker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysalaziz.heromaker.HeroViewModel
import com.faysalaziz.heromaker.data.AppSettings
import com.faysalaziz.heromaker.ui.theme.Bone
import com.faysalaziz.heromaker.ui.theme.Gold
import com.faysalaziz.heromaker.ui.theme.GoldDeep
import com.faysalaziz.heromaker.ui.theme.GoldLight
import com.faysalaziz.heromaker.ui.theme.Ink
import com.faysalaziz.heromaker.ui.theme.Muted
import com.faysalaziz.heromaker.ui.theme.Panel
import com.faysalaziz.heromaker.ui.theme.PanelRaised

@Composable
fun SettingsScreen(vm: HeroViewModel) {
    var apiKey by remember { mutableStateOf(vm.loadApiKey()) }
    var showKey by remember { mutableStateOf(false) }
    var model by remember { mutableStateOf(vm.settings.model) }
    var size by remember { mutableStateOf(vm.settings.size) }
    var quality by remember { mutableStateOf(vm.settings.quality) }

    val modelOptions = (vm.models + model).distinct().map { it to it }

    Column(Modifier.fillMaxSize().background(Ink)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "SETTINGS",
                modifier = Modifier.weight(1f),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 2.sp, color = GoldLight),
            )
            BarButton("Close", vm::closeSettings)
        }
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("OPENAI API KEY", style = LabelStyle)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    GoldTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = "API key",
                        placeholder = "sk-...",
                        modifier = Modifier.weight(1f),
                        visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    )
                    BarButton(if (showKey) "Hide" else "Show") { showKey = !showKey }
                }
                Text(
                    "Stored encrypted on this phone. It is only ever sent to OpenAI.",
                    style = HintStyle,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("IMAGE MODEL", style = LabelStyle)
                SimpleDropdown(modelOptions, model) { model = it }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BarButton(if (vm.modelsLoading) "Loading…" else "Refresh list from OpenAI") {
                        if (!vm.modelsLoading) vm.refreshModels(apiKey)
                    }
                }
                vm.modelsStatus?.let { Text(it, style = TextStyle(fontSize = 12.sp, color = Gold)) }
                Text(
                    "Needs your key. OpenAI retires models from time to time, so refresh if a model stops working.",
                    style = HintStyle,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("IMAGE SIZE", style = LabelStyle)
                SimpleDropdown(AppSettings.SIZES, size) { size = it }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("QUALITY", style = LabelStyle)
                SimpleDropdown(AppSettings.QUALITIES, quality) { quality = it }
                Text("Larger sizes and higher quality cost more per image. Check OpenAI's pricing.", style = HintStyle)
            }
            Spacer(Modifier.height(8.dp))
        }
        BottomBar {
            GoldButton(
                "Save settings",
                onClick = { vm.saveSettings(apiKey, AppSettings(model = model, size = size, quality = quality)) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** A plain dropdown. Each option is (value, label). */
@Composable
private fun SimpleDropdown(options: List<Pair<String, String>>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(2.dp)
    val label = options.firstOrNull { it.first == selected }?.second ?: selected
    Box {
        Row(
            Modifier
                .fillMaxWidth()
                .background(Panel, shape)
                .border(1.dp, GoldDeep, shape)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, modifier = Modifier.weight(1f), style = TextStyle(fontSize = 15.sp, color = Bone))
            Text("▾", style = TextStyle(fontSize = 14.sp, color = Gold))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(PanelRaised),
        ) {
            options.forEach { (value, text) ->
                DropdownMenuItem(
                    text = { Text(text, color = Bone) },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    },
                )
            }
        }
    }
}
