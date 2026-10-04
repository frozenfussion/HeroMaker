package com.faysalaziz.heromaker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faysalaziz.heromaker.R
import com.faysalaziz.heromaker.ui.theme.Bone
import com.faysalaziz.heromaker.ui.theme.Gold
import com.faysalaziz.heromaker.ui.theme.GoldBrush
import com.faysalaziz.heromaker.ui.theme.GoldDeep
import com.faysalaziz.heromaker.ui.theme.GoldLight
import com.faysalaziz.heromaker.ui.theme.Ink
import com.faysalaziz.heromaker.ui.theme.Line
import com.faysalaziz.heromaker.ui.theme.Muted
import com.faysalaziz.heromaker.ui.theme.Panel
import com.faysalaziz.heromaker.ui.theme.PanelRaised
import com.faysalaziz.heromaker.ui.theme.PanelSelected

/** A rectangle with two opposite corners cut off, like a circuit-board tile. */
class ChamferShape(private val cut: Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val c = with(density) { cut.toPx() }
        val path = Path().apply {
            moveTo(c, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - c)
            lineTo(size.width - c, size.height)
            lineTo(0f, size.height)
            lineTo(0f, c)
            close()
        }
        return Outline.Generic(path)
    }
}

val DisplayStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, letterSpacing = 0.3.sp, color = Bone)
val CardTitleStyle = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp, letterSpacing = 0.2.sp)
val LabelStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 2.sp, color = Gold)
val HintStyle = TextStyle(fontSize = 12.sp, color = Muted)

/** A selectable option card with a thin gold edge. Gold and tinted when selected. */
@Composable
fun HexCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cut: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = remember(cut) { ChamferShape(cut) }
    Box(
        modifier
            .clip(shape)
            .background(if (selected) GoldLight else GoldDeep)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(1.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(if (selected) PanelSelected else Panel)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            content = content,
        )
    }
}

/** Small pill used for multi-select options such as powers and outfit pieces. */
@Composable
fun OptionChip(label: String, selected: Boolean, onToggle: () -> Unit) {
    val shape = RoundedCornerShape(2.dp)
    Box(
        Modifier
            .clip(shape)
            .background(if (selected) GoldLight else Panel)
            .border(1.dp, if (selected) GoldLight else GoldDeep, shape)
            .toggleable(value = selected, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(horizontal = 12.dp, vertical = 9.dp),
    ) {
        Text(
            label,
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) Ink else Bone,
            ),
        )
    }
}

/** The main action button: solid gold gradient. */
@Composable
fun GoldButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val shape = remember { ChamferShape(10.dp) }
    Box(
        modifier
            .height(48.dp)
            .alpha(if (enabled) 1f else 0.35f)
            .clip(shape)
            .background(GoldBrush)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text.uppercase(),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.4.sp, color = Ink),
        )
    }
}

/** The secondary button: dark with a gold edge. */
@Composable
fun GhostButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val shape = remember { ChamferShape(10.dp) }
    Box(
        modifier
            .height(48.dp)
            .alpha(if (enabled) 1f else 0.35f)
            .clip(shape)
            .background(GoldDeep)
            .padding(1.dp)
            .clip(shape)
            .background(Ink)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text.uppercase(),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.4.sp, color = GoldLight),
        )
    }
}

/** Small outlined button for the top bar. */
@Composable
fun BarButton(text: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(2.dp)
    Box(
        Modifier
            .height(36.dp)
            .clip(shape)
            .border(1.dp, GoldDeep, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text.uppercase(),
            style = TextStyle(fontSize = 11.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Medium, color = GoldLight),
        )
    }
}

@Composable
fun AppTopBar(onSurprise: (() -> Unit)?, onSettings: (() -> Unit)?) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(painterResource(R.drawable.ic_hero_mark), contentDescription = null, modifier = Modifier.size(34.dp))
        Text(
            "HERO MAKER",
            modifier = Modifier.weight(1f),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 2.sp, color = GoldLight),
        )
        if (onSurprise != null) BarButton("Surprise", onSurprise)
        if (onSettings != null) BarButton("Settings", onSettings)
    }
}

/** Thin segmented progress bar: one segment per wizard step. */
@Composable
fun StepProgress(total: Int, current: Int, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 0 until total) {
            Box(
                Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        when {
                            i < current -> GoldDeep
                            i == current -> GoldLight
                            else -> Line
                        },
                    ),
            )
        }
    }
}

@Composable
fun BottomBar(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().background(PanelRaised.copy(alpha = 0.4f))) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) { content() }
    }
}
