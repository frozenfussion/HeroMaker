package com.faysalaziz.heromaker.data

/** Everything the user picked in the wizard. Empty strings and lists mean "not chosen yet". */
data class HeroSpec(
    val nature: String = "",
    val gender: String = "",
    /** Species for biological heroes, machine type for non-biological ones. */
    val kind: String = "",
    val powers: List<String> = emptyList(),
    val background: String = "",
    val customBackground: String = "",
    val personality: String = "",
    val outfit: List<String> = emptyList(),
    val style: String = "",
    val setting: String = "",
    val pose: String = "",
    val remarks: String = "",
) {
    /** The background text that counts: the typed one wins over a picked card. */
    val effectiveBackground: String
        get() = customBackground.trim().ifEmpty { background }

    val isBiological: Boolean get() = nature == NATURE_BIOLOGICAL
    val kindLabel: String get() = if (isBiological) "Species" else "Type"
}

/** The wizard steps, in order. */
enum class WizardStep(val title: String, val subtitle: String) {
    Nature("Nature", "Flesh and blood, or built?"),
    Gender("Gender", "Applies to any hero, robots included."),
    Kind("Species", "What are they?"),
    Powers("Powers", "Pick up to ${Options.MAX_POWERS}."),
    Background("Background", "Where did they come from?"),
    Personality("Personality", "How do they carry themselves?"),
    Outfit("Outfit & gear", "Optional. Pick up to ${Options.MAX_OUTFIT}."),
    Style("Art style", "The look of the final image."),
    Setting("Setting", "Where is the hero standing?"),
    Pose("Pose & framing", "How should the shot look?"),
    Remarks("Remarks", "Anything else. Optional."),
}

/** Has the user done enough on [step] to move on? */
fun HeroSpec.isStepComplete(step: WizardStep): Boolean = when (step) {
    WizardStep.Nature -> nature.isNotEmpty()
    WizardStep.Gender -> gender.isNotEmpty()
    WizardStep.Kind -> kind.isNotEmpty()
    WizardStep.Powers -> powers.isNotEmpty()
    WizardStep.Background -> effectiveBackground.isNotEmpty()
    WizardStep.Personality -> personality.isNotEmpty()
    WizardStep.Outfit -> true
    WizardStep.Style -> style.isNotEmpty()
    WizardStep.Setting -> setting.isNotEmpty()
    WizardStep.Pose -> pose.isNotEmpty()
    WizardStep.Remarks -> true
}

/** Randomises every choice ("Surprise me"). Remarks are kept. */
fun HeroSpec.randomised(random: kotlin.random.Random = kotlin.random.Random.Default): HeroSpec {
    val biological = random.nextBoolean()
    val kinds = if (biological) Options.species else Options.machineTypes
    return copy(
        nature = if (biological) NATURE_BIOLOGICAL else NATURE_NON_BIOLOGICAL,
        gender = Options.genders.random(random).label,
        kind = kinds.random(random).label,
        powers = Options.powers.shuffled(random).take(random.nextInt(1, Options.MAX_POWERS + 1)),
        background = Options.backgrounds.random(random).label,
        customBackground = "",
        personality = Options.personalities.random(random).label,
        outfit = Options.outfitGroups.flatMap { it.second }.shuffled(random).take(random.nextInt(0, Options.MAX_OUTFIT + 1)),
        style = Options.styles.random(random).label,
        setting = Options.settings.random(random).label,
        pose = Options.poses.random(random).label,
    )
}
