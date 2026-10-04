package com.faysalaziz.heromaker.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysalaziz.heromaker.HeroViewModel
import com.faysalaziz.heromaker.data.WizardStep
import com.faysalaziz.heromaker.ui.theme.Bone
import com.faysalaziz.heromaker.ui.theme.Gold
import com.faysalaziz.heromaker.ui.theme.Line
import com.faysalaziz.heromaker.ui.theme.Muted
import com.faysalaziz.heromaker.ui.theme.Panel

private class SummaryRow(val label: String, val value: String, val stepIndex: Int)

@Composable
fun ReviewScreen(vm: HeroViewModel) {
    val spec = vm.spec
    val rows = listOf(
        SummaryRow("Nature", spec.nature, WizardStep.Nature.ordinal),
        SummaryRow("Gender", spec.gender, WizardStep.Gender.ordinal),
        SummaryRow(spec.kindLabel, spec.kind, WizardStep.Kind.ordinal),
        SummaryRow("Powers", spec.powers.joinToString(", "), WizardStep.Powers.ordinal),
        SummaryRow("Background", spec.effectiveBackground, WizardStep.Background.ordinal),
        SummaryRow("Personality", spec.personality, WizardStep.Personality.ordinal),
        SummaryRow("Outfit", spec.outfit.joinToString(", ").ifEmpty { "None" }, WizardStep.Outfit.ordinal),
        SummaryRow("Art style", spec.style, WizardStep.Style.ordinal),
        SummaryRow("Setting", spec.setting, WizardStep.Setting.ordinal),
        SummaryRow("Pose", spec.pose, WizardStep.Pose.ordinal),
        SummaryRow("Remarks", spec.remarks.trim().ifEmpty { "None" }, WizardStep.Remarks.ordinal),
    )
    var showPrompt by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        AppTopBar(onSurprise = vm::surprise, onSettings = vm::openSettings)
        StepProgress(total = vm.steps.size, current = vm.steps.size)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp),
        ) {
            Text("READY", style = LabelStyle)
            Spacer(Modifier.height(4.dp))
            Text("Your hero", style = DisplayStyle)
            Spacer(Modifier.height(4.dp))
            Text("Check the details, then generate.", style = TextStyle(fontSize = 14.sp, color = Muted))
            Spacer(Modifier.height(12.dp))

            rows.forEach { row ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        row.label.uppercase(),
                        modifier = Modifier.width(96.dp),
                        style = TextStyle(fontSize = 11.sp, letterSpacing = 1.5.sp, color = Muted),
                    )
                    Text(
                        row.value.ifEmpty { "Not chosen" },
                        modifier = Modifier.weight(1f),
                        style = TextStyle(fontSize = 15.sp, color = Bone),
                    )
                    Text(
                        "Edit",
                        modifier = Modifier.clickable { vm.goToStep(row.stepIndex) },
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Gold),
                    )
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
            }

            Spacer(Modifier.height(18.dp))
            Text(
                if (showPrompt) "Hide the prompt" else "Show the prompt sent to OpenAI",
                modifier = Modifier.clickable { showPrompt = !showPrompt },
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Gold),
            )
            if (showPrompt) {
                Spacer(Modifier.height(8.dp))
                Text(
                    vm.promptPreview(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Panel)
                        .padding(12.dp),
                    style = TextStyle(fontSize = 13.sp, lineHeight = 19.sp, color = Bone, textAlign = TextAlign.Start),
                )
            }
        }
        BottomBar {
            GhostButton("Back", onClick = { vm.onBack() }, modifier = Modifier.width(104.dp))
            GoldButton("Generate hero", onClick = vm::generate, modifier = Modifier.weight(1f))
        }
    }
}
