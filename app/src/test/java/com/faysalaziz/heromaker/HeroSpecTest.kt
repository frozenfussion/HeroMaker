package com.faysalaziz.heromaker

import com.faysalaziz.heromaker.data.HeroSpec
import com.faysalaziz.heromaker.data.NATURE_BIOLOGICAL
import com.faysalaziz.heromaker.data.NATURE_NON_BIOLOGICAL
import com.faysalaziz.heromaker.data.Options
import com.faysalaziz.heromaker.data.PromptBuilder
import com.faysalaziz.heromaker.data.WizardStep
import com.faysalaziz.heromaker.data.isStepComplete
import com.faysalaziz.heromaker.data.randomised
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class HeroSpecTest {
    private val sample = HeroSpec(
        nature = NATURE_BIOLOGICAL,
        gender = "Female",
        kind = "Alien",
        powers = listOf("Flight", "Lightning control"),
        background = "Cosmic event",
        personality = "Noble",
        outfit = listOf("Cape", "Gauntlets"),
        style = "Cyberpunk",
        setting = "Futuristic city",
        pose = "Action shot",
        remarks = "Name: Nova Vale",
    )

    @Test
    fun promptMentionsEveryChoice() {
        val prompt = PromptBuilder.build(sample)
        listOf("alien", "Flight", "Lightning control", "Cosmic event", "Cape", "Gauntlets", "Futuristic city", "Action shot", "Nova Vale")
            .forEach { assertTrue("missing: $it", prompt.contains(it)) }
        assertTrue(prompt.contains("neon cyan and magenta"))
    }

    @Test
    fun promptSkipsEmptyRemarksAndOutfit() {
        val prompt = PromptBuilder.build(sample.copy(remarks = "  ", outfit = emptyList()))
        assertFalse(prompt.contains("Extra notes"))
        assertFalse(prompt.contains("Costume and gear"))
    }

    @Test
    fun typedBackgroundWinsOverPickedCard() {
        val spec = sample.copy(customBackground = "Raised by robots")
        assertEquals("Raised by robots", spec.effectiveBackground)
        assertTrue(PromptBuilder.build(spec).contains("Raised by robots"))
    }

    @Test
    fun nonBiologicalHeroesUseTypeLabel() {
        val spec = sample.copy(nature = NATURE_NON_BIOLOGICAL, kind = "Android")
        assertEquals("Type", spec.kindLabel)
        assertTrue(PromptBuilder.build(spec).contains("non-biological"))
    }

    @Test
    fun stepsNeedAChoiceExceptOptionalOnes() {
        val empty = HeroSpec()
        assertFalse(empty.isStepComplete(WizardStep.Nature))
        assertFalse(empty.isStepComplete(WizardStep.Powers))
        assertTrue(empty.isStepComplete(WizardStep.Outfit))
        assertTrue(empty.isStepComplete(WizardStep.Remarks))
    }

    @Test
    fun surpriseMeCompletesEveryStepWithinLimits() {
        repeat(50) { seed ->
            val spec = HeroSpec(remarks = "keep me").randomised(Random(seed))
            WizardStep.entries.forEach { assertTrue("$seed ${it.name}", spec.isStepComplete(it)) }
            assertTrue(spec.powers.size in 1..Options.MAX_POWERS)
            assertTrue(spec.outfit.size <= Options.MAX_OUTFIT)
            assertEquals("keep me", spec.remarks)
            val kinds = (if (spec.isBiological) Options.species else Options.machineTypes).map { it.label }
            assertTrue(spec.kind in kinds)
        }
    }
}
