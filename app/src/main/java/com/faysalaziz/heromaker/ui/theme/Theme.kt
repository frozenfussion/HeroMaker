package com.faysalaziz.heromaker.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// "Gold Circuit" palette, taken from the logo.
val Ink = Color(0xFF0A0A0A)
val Panel = Color(0xFF141210)
val PanelRaised = Color(0xFF1C1914)
val PanelSelected = Color(0xFF2B2418)
val Line = Color(0xFF2A251C)
val GoldDeep = Color(0xFF8F6E3A)
val Gold = Color(0xFFB8955A)
val GoldLight = Color(0xFFE3CB98)
val Bone = Color(0xFFEDE9E1)
val Muted = Color(0xFF9D9486)
val Danger = Color(0xFFE08A7A)

val GoldBrush = Brush.linearGradient(listOf(GoldDeep, Gold, GoldLight))

private val GoldCircuitColors = darkColorScheme(
    primary = Gold,
    onPrimary = Ink,
    secondary = GoldLight,
    onSecondary = Ink,
    background = Ink,
    onBackground = Bone,
    surface = Panel,
    onSurface = Bone,
    surfaceVariant = PanelRaised,
    onSurfaceVariant = Muted,
    outline = GoldDeep,
    error = Danger,
)

@Composable
fun HeroMakerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GoldCircuitColors,
        shapes = Shapes(
            extraSmall = RoundedCornerShape(2.dp),
            small = RoundedCornerShape(2.dp),
            medium = RoundedCornerShape(4.dp),
        ),
        content = content,
    )
}
