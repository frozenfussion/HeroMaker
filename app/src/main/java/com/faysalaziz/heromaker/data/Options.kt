package com.faysalaziz.heromaker.data

import androidx.annotation.DrawableRes
import com.faysalaziz.heromaker.R

/** One pickable card in the wizard. [hint] is the small grey line under the label. */
data class Choice(val label: String, val hint: String? = null)

/** An art style card: [sample] is the picture shown on the card, [prompt] is the description sent to the model. */
data class ArtStyle(val label: String, @DrawableRes val sample: Int, val prompt: String)

const val NATURE_BIOLOGICAL = "Biological"
const val NATURE_NON_BIOLOGICAL = "Non-biological"

/** Every option the wizard offers. Edit these lists to add or remove choices. */
object Options {
    val natures = listOf(
        Choice(NATURE_BIOLOGICAL, "Born, grown or evolved"),
        Choice(NATURE_NON_BIOLOGICAL, "Built, coded or forged"),
    )

    val genders = listOf("Male", "Female", "Non-binary", "Indeterminate", "None").map { Choice(it) }

    val species = listOf("Human", "Alien", "Mutant", "Demigod", "Beast-person", "Spirit / Undead").map { Choice(it) }

    val machineTypes = listOf("Robot", "Android", "Cyborg", "AI hologram", "Living armor", "Golem").map { Choice(it) }

    val powers = listOf(
        "Super strength", "Flight", "Telekinesis", "Fire control", "Ice control", "Lightning control",
        "Shadow", "Time manipulation", "Shapeshifting", "Healing", "Invisibility", "Tech & gadgets",
        "Mind control", "Energy blasts",
    )
    const val MAX_POWERS = 4

    val backgrounds = listOf(
        "Lab accident", "Ancient prophecy", "Alien crash-lander", "Street orphan",
        "Military experiment", "Royal bloodline", "Cosmic event",
    ).map { Choice(it) }

    val personalities = listOf("Noble", "Anti-hero", "Reluctant", "Hot-headed", "Cunning", "Playful").map { Choice(it) }

    val outfitGroups: List<Pair<String, List<String>>> = listOf(
        "Headwear" to listOf(
            "Hood", "Cowl", "Mask", "Helmet", "Hijab", "Turban", "Shemagh scarf", "Crown", "Visor", "Goggles",
        ),
        "Outerwear" to listOf("Cape", "Cloak", "Long coat", "Trench coat", "Jacket", "Poncho", "Scarf"),
        "Suit" to listOf("Skin-tight suit", "Armor", "Robes", "Battle dress", "Tactical gear", "Jumpsuit"),
        "Accessories" to listOf("Gauntlets", "Gloves", "Utility belt", "Shoulder pads", "Boots", "Chest emblem", "Wings"),
    )
    const val MAX_OUTFIT = 5

    val styles = listOf(
        ArtStyle(
            "Anime", R.drawable.style_anime,
            "clean cel-shaded anime illustration, bold line art, large expressive eyes, vibrant colours",
        ),
        ArtStyle(
            "Cyberpunk", R.drawable.style_cyberpunk,
            "cyberpunk art, neon cyan and magenta lighting, rain-slick city glow, high-tech implants, moody and glossy",
        ),
        ArtStyle(
            "Arcane painterly", R.drawable.style_arcane,
            "painterly hand-brushed look like a stylised animated series, rich purples and warm gold, visible brush strokes, dramatic lighting",
        ),
        ArtStyle(
            "Comic book", R.drawable.style_comic,
            "classic American comic book art, thick ink outlines, halftone dots, flat bold colours, dynamic shading",
        ),
        ArtStyle(
            "Pixel art", R.drawable.style_pixel,
            "retro 16-bit pixel art, limited colour palette, crisp visible pixels, no smoothing",
        ),
        ArtStyle(
            "Watercolor", R.drawable.style_watercolor,
            "soft watercolor painting, bleeding washes, paper texture, gentle pastel colours, loose edges",
        ),
        ArtStyle(
            "Dark fantasy", R.drawable.style_dark_fantasy,
            "gritty dark fantasy oil painting, muted desaturated tones, deep shadows, ominous atmosphere",
        ),
        ArtStyle(
            "Retro 80s", R.drawable.style_retro80s,
            "1980s airbrushed synthwave poster look, hot pink, yellow and purple, chrome highlights, sunset grid glow",
        ),
        ArtStyle(
            "Realistic", R.drawable.style_realistic,
            "photorealistic cinematic portrait, natural skin texture, shallow depth of field, studio lighting",
        ),
        ArtStyle(
            "Chibi", R.drawable.style_chibi,
            "cute chibi proportions, oversized head, small body, soft pastel colours, kawaii",
        ),
    )

    val settings = listOf(
        "Futuristic city", "Ruins", "Space", "Fantasy kingdom", "Underwater", "Wasteland", "Volcanic rift",
        "Floating islands", "Jungle", "Desert", "Arctic", "Rooftop at night", "Alien planet", "Haunted castle",
    ).map { Choice(it) }

    val poses = listOf(
        "Full body", "Portrait", "Action shot", "Flying", "Power stance", "Battle-ready", "Crouching",
        "Back view", "Close-up face", "Dramatic low angle",
    ).map { Choice(it) }
}
