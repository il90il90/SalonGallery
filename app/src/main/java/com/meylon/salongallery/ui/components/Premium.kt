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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.meylon.salongallery.ui.theme.ElecBg
import com.meylon.salongallery.ui.theme.ElecBorder
import com.meylon.salongallery.ui.theme.ElecSurface
import com.meylon.salongallery.ui.theme.NeonBlue
import com.meylon.salongallery.ui.theme.NeonCyan
import com.meylon.salongallery.ui.theme.NeonViolet
import com.meylon.salongallery.ui.theme.OnAccent
import com.meylon.salongallery.ui.theme.TextPrimary
import com.meylon.salongallery.ui.theme.TextTertiary

/** Cyan → violet accent used for logo & selected borders. */
val AccentGradient = Brush.linearGradient(listOf(NeonCyan, NeonViolet))

/** Cyan → blue → violet CTA gradient. */
val CtaGradient = Brush.horizontalGradient(listOf(NeonCyan, NeonBlue, NeonViolet))

/** Deep-space background with cyan (top) and violet (bottom) neon glows. */
@Composable
fun SalonBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(ElecBg)) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.radialGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.14f), Color.Transparent),
                    center = Offset(150f, 120f), radius = 720f,
                )
            )
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.radialGradient(
                    colors = listOf(NeonViolet.copy(alpha = 0.16f), Color.Transparent),
                    center = Offset(950f, 2000f), radius = 900f,
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
                if (enabled) Modifier.shadow(20.dp, shape, spotColor = NeonBlue, ambientColor = NeonViolet)
                else Modifier
            )
            .clip(shape)
            .background(if (enabled) CtaGradient else Brush.horizontalGradient(listOf(ElecSurface, ElecSurface)))
            .then(if (!enabled) Modifier.border(1.dp, ElecBorder, shape) else Modifier)
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
