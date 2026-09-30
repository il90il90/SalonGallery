package com.meylon.salongallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.meylon.salongallery.ui.theme.ElecBg
import com.meylon.salongallery.ui.theme.ElecBorder
import com.meylon.salongallery.ui.theme.ElecSurface
import com.meylon.salongallery.ui.theme.NeonBlue
import com.meylon.salongallery.ui.theme.NeonCyan
import com.meylon.salongallery.ui.theme.NeonViolet
import com.meylon.salongallery.ui.theme.OnAccent
import com.meylon.salongallery.ui.theme.TextPrimary
import com.meylon.salongallery.ui.theme.TextTertiary

/** Warm terracotta accent used for logo & selected borders. */
val AccentGradient = Brush.linearGradient(listOf(Color(0xFFCB7A42), Color(0xFFB04E2C)))

/** Clay CTA gradient (white text stays legible on it). */
val CtaGradient = Brush.horizontalGradient(listOf(Color(0xFFBC5A34), Color(0xFFA5482A)))

/**
 * Draws a neon focus ring when this focusable/clickable element has D-pad focus,
 * so the UI is navigable and legible on Android TV (place before .clickable/.focusable).
 */
fun Modifier.focusRing(shape: Shape = RoundedCornerShape(16.dp)): Modifier = composed {
    var focused by remember { mutableStateOf(false) }
    this
        .onFocusChanged { focused = it.isFocused }
        .then(if (focused) Modifier.border(2.5.dp, NeonCyan, shape) else Modifier)
}

/** Warm paper ground with a whisper of light at the top. */
@Composable
fun SalonBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(ElecBg)) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.radialGradient(
                    colors = listOf(Color(0x1FCB8A52), Color.Transparent),
                    center = Offset(160f, 80f), radius = 1000f,
                )
            )
        )
        content()
    }
}

/** Gradient logo chip holding a framed-picture glyph. */
@Composable
fun LogoChip(size: Int = 40, icon: ImageVector = Icons.Outlined.Image) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size * 0.3f).dp))
            .background(AccentGradient),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size((size * 0.55f).dp))
    }
}

/** Top-left brand row: gradient logo + wordmark. */
@Composable
fun BrandRow(name: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LogoChip(size = 40)
        Text(text = name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
    }
}

/** Small letter-spaced overline label in neon cyan. */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = NeonCyan,
        modifier = modifier,
    )
}

/** Primary gradient CTA with a soft glow. */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leading: ImageVector? = null,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .then(
                if (enabled) Modifier.shadow(14.dp, shape, spotColor = Color(0x66B0502E), ambientColor = Color(0x33B0502E))
                else Modifier
            )
            .clip(shape)
            .background(if (enabled) CtaGradient else Brush.horizontalGradient(listOf(ElecSurface, ElecSurface)))
            .then(if (!enabled) Modifier.border(1.dp, ElecBorder, shape) else Modifier)
            .focusRing(shape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (leading != null) {
                Icon(leading, null, tint = if (enabled) OnAccent else TextTertiary, modifier = Modifier.size(20.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge, color = if (enabled) OnAccent else TextTertiary)
        }
    }
}

/** Secondary outline button. */
@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leading: ImageVector? = null,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .border(1.dp, ElecBorder, shape)
            .focusRing(shape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (leading != null) {
                Icon(leading, null, tint = if (enabled) TextPrimary else TextTertiary, modifier = Modifier.size(20.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge, color = if (enabled) TextPrimary else TextTertiary)
        }
    }
}
