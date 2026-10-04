package com.faysalaziz.heromaker.data

/** Turns a [HeroSpec] into the text prompt sent to the image model. Pure Kotlin so it is easy to unit test. */
object PromptBuilder {

    fun build(spec: HeroSpec): String {
        val style = Options.styles.firstOrNull { it.label == spec.style }?.prompt ?: spec.style
        val lines = mutableListOf<String>()

        lines += "Create an original superhero character illustration in this art style: $style."

        val being = if (spec.isBiological) "a biological being" else "a non-biological being (built, not born)"
        val kind = if (spec.kind.isNotEmpty()) ", ${spec.kind.lowercase()}" else ""
        lines += "Character: $being$kind. Gender presentation: ${genderText(spec.gender)}."

        if (spec.powers.isNotEmpty()) lines += "Powers, shown visually where possible: ${spec.powers.joinToString(", ")}."
        if (spec.effectiveBackground.isNotEmpty()) lines += "Origin story: ${spec.effectiveBackground}."
        if (spec.personality.isNotEmpty()) lines += "Personality, shown in expression and body language: ${spec.personality.lowercase()}."
        if (spec.outfit.isNotEmpty()) lines += "Costume and gear: ${spec.outfit.joinToString(", ")}."
        if (spec.setting.isNotEmpty()) lines += "Setting: ${spec.setting}."
        if (spec.pose.isNotEmpty()) lines += "Pose and framing: ${spec.pose}."

        val remarks = spec.remarks.trim()
        if (remarks.isNotEmpty()) lines += "Extra notes from the creator (follow these closely): $remarks"

        lines += "One character only. No text, captions, logos or watermarks unless a name is given in the notes above."
        return lines.joinToString("\n")
    }

    private fun genderText(gender: String): String = when (gender) {
        "Male" -> "male"
        "Female" -> "female"
        "Non-binary" -> "non-binary"
        "Indeterminate" -> "indeterminate, ambiguous"
        "None" -> "none, genderless"
        else -> gender.lowercase().ifEmpty { "unspecified" }
    }
}
