package com.faysalaziz.heromaker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysalaziz.heromaker.HeroViewModel
import com.faysalaziz.heromaker.data.Choice
import com.faysalaziz.heromaker.data.HeroSpec
import com.faysalaziz.heromaker.data.Options
import com.faysalaziz.heromaker.data.WizardStep
import com.faysalaziz.heromaker.ui.theme.Bone
import com.faysalaziz.heromaker.ui.theme.Gold
import com.faysalaziz.heromaker.ui.theme.GoldDeep
import com.faysalaziz.heromaker.ui.theme.GoldLight
import com.faysalaziz.heromaker.ui.theme.Muted
import com.faysalaziz.heromaker.ui.theme.Panel
import androidx.compose.foundation.shape.RoundedCornerShape

/** One wizard step: title, the options for that step, and the Back / Next bar. */
@Composable
fun WizardScreen(vm: HeroViewModel, stepIndex: Int) {
    val step = vm.steps[stepIndex]
    val spec = vm.spec
    val isLast = stepIndex == vm.steps.lastIndex

    Column(Modifier.fillMaxSize()) {
        AppTopBar(onSurprise = vm::surprise, onSettings = vm::openSettings)
        StepProgress(total = vm.steps.size, current = stepIndex)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp),
        ) {
            Text("STEP ${stepIndex + 1} OF ${vm.steps.size}", style = LabelStyle)
            Spacer(Modifier.height(4.dp))
            Text(stepTitle(step, spec), style = DisplayStyle)
            Spacer(Modifier.height(4.dp))
            Text(step.subtitle, style = TextStyle(fontSize = 14.sp, color = Muted))
            Spacer(Modifier.height(16.dp))
            StepContent(step, spec, vm)
        }
        BottomBar {
            GhostButton("Back", onClick = { vm.onBack() }, modifier = Modifier.width(104.dp), enabled = stepIndex > 0)
            GoldButton(
                if (isLast) "Review hero" else "Next",
                onClick = vm::next,
                modifier = Modifier.weight(1f),
                enabled = vm.canContinue(step),
            )
        }
    }
}

private fun stepTitle(step: WizardStep, spec: HeroSpec): String =
    if (step == WizardStep.Kind) spec.kindLabel.ifEmpty { "Species" } else step.title

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepContent(step: WizardStep, spec: HeroSpec, vm: HeroViewModel) {
    when (step) {
        WizardStep.Nature -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Options.natures.forEach { choice ->
                HexCard(
                    selected = spec.nature == choice.label,
                    onClick = { vm.pickNature(choice.label) },
                    modifier = Modifier.fillMaxWidth(),
                    cut = 18.dp,
                ) {
                    Text(choice.label, style = CardTitleStyle.copy(fontSize = 20.sp))
                    choice.hint?.let { Text(it, style = HintStyle) }
                    Spacer(Modifier.height(36.dp))
                }
            }
        }

        WizardStep.Gender -> OptionGrid(Options.genders, spec.gender, vm::pickGender)

        WizardStep.Kind -> {
            // The first step already decided biological or not; show a hint if the user skipped ahead.
            val options = if (spec.isBiological) Options.species else Options.machineTypes
            OptionGrid(options, spec.kind, vm::pickKind)
        }

        WizardStep.Powers -> {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Options.powers.forEach { OptionChip(it, it in spec.powers) { vm.togglePower(it) } }
            }
            Spacer(Modifier.height(10.dp))
            Text("${spec.powers.size} of ${Options.MAX_POWERS} selected", style = TextStyle(fontSize = 12.sp, color = Gold))
        }

        WizardStep.Background -> {
            OptionGrid(Options.backgrounds, spec.background, vm::pickBackground)
            Spacer(Modifier.height(16.dp))
            GoldTextField(
                value = spec.customBackground,
                onValueChange = vm::setCustomBackground,
                label = "Or write your own",
                placeholder = "e.g. Raised by robots on a lunar mining station",
                singleLine = true,
            )
        }

        WizardStep.Personality -> OptionGrid(Options.personalities, spec.personality, vm::pickPersonality)

        WizardStep.Outfit -> {
            Options.outfitGroups.forEachIndexed { index, (group, items) ->
                if (index > 0) Spacer(Modifier.height(16.dp))
                Text(group.uppercase(), style = LabelStyle)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items.forEach { OptionChip(it, it in spec.outfit) { vm.toggleOutfit(it) } }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text("${spec.outfit.size} of ${Options.MAX_OUTFIT} selected", style = TextStyle(fontSize = 12.sp, color = Gold))
        }

        WizardStep.Style -> StyleGrid(spec.style, vm::pickStyle)

        WizardStep.Setting -> OptionGrid(Options.settings, spec.setting, vm::pickSetting)

        WizardStep.Pose -> OptionGrid(Options.poses, spec.pose, vm::pickPose)

        WizardStep.Remarks -> {
            GoldTextField(
                value = spec.remarks,
                onValueChange = vm::setRemarks,
                label = "Extra notes",
                placeholder = "Name, age, build, hair and skin colour, costume colours, catchphrase, anything else.",
                singleLine = false,
                minLines = 6,
            )
            Spacer(Modifier.height(8.dp))
            Text("Tip: add a name here if you want one on the image.", style = HintStyle)
        }
    }
}

/** Two cards per row. */
@Composable
private fun OptionGrid(options: List<Choice>, selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        options.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { choice ->
                    HexCard(
                        selected = selected == choice.label,
                        onClick = { onSelect(choice.label) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            choice.label,
                            style = CardTitleStyle.copy(color = if (selected == choice.label) GoldLight else Bone),
                        )
                        choice.hint?.let { Text(it, style = HintStyle) }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

/** Art style cards with a sample picture on each. */
@Composable
private fun StyleGrid(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Options.styles.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { style ->
                    HexCard(
                        selected = selected == style.label,
                        onClick = { onSelect(style.label) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Image(
                            painterResource(style.sample),
                            contentDescription = "${style.label} sample",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(2.dp)),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            style.label,
                            style = CardTitleStyle.copy(color = if (selected == style.label) GoldLight else Bone),
                        )
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun GoldTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it, color = Muted) } },
        singleLine = singleLine,
        minLines = minLines,
        visualTransformation = visualTransformation,
        colors = goldTextFieldColors(),
    )
}

@Composable
fun goldTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Bone,
    unfocusedTextColor = Bone,
    focusedBorderColor = Gold,
    unfocusedBorderColor = GoldDeep,
    cursorColor = GoldLight,
    focusedLabelColor = Gold,
    unfocusedLabelColor = Muted,
    focusedContainerColor = Panel,
    unfocusedContainerColor = Panel,
)
