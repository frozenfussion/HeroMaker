package com.faysalaziz.heromaker.data

import android.content.Context

/** Image generation settings chosen in the Settings screen. The API key is stored separately in [SecureStore]. */
data class AppSettings(
    val model: String = DEFAULT_MODEL,
    val size: String = "1024x1024",
    val quality: String = "medium",
) {
    companion object {
        const val DEFAULT_MODEL = "gpt-image-2"

        /**
         * Used until the real list is fetched from OpenAI with your key (Settings > Refresh).
         * OpenAI retires models from time to time, so the fetched list is the source of truth.
         */
        val FALLBACK_MODELS = listOf("gpt-image-2.5-sunburst", "gpt-image-2.5-flare", "gpt-image-2")

        val SIZES = listOf(
            "1024x1024" to "Square (1024 × 1024)",
            "1024x1536" to "Portrait (1024 × 1536)",
            "1536x1024" to "Landscape (1536 × 1024)",
            "auto" to "Auto",
        )

        val QUALITIES = listOf(
            "auto" to "Auto",
            "low" to "Low (cheapest)",
            "medium" to "Medium",
            "high" to "High",
        )
    }
}

class SettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun load(): AppSettings {
        val d = AppSettings()
        return AppSettings(
            model = prefs.getString("model", d.model) ?: d.model,
            size = prefs.getString("size", d.size) ?: d.size,
            quality = prefs.getString("quality", d.quality) ?: d.quality,
        )
    }

    fun save(settings: AppSettings) {
        prefs.edit()
            .putString("model", settings.model)
            .putString("size", settings.size)
            .putString("quality", settings.quality)
            .apply()
    }
}
