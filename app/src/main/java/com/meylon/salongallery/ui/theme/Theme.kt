package com.meylon.salongallery.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ElectricColors = darkColorScheme(
    primary = NeonCyan,
    onPrimary = OnAccent,
    secondary = NeonViolet,
    onSecondary = TextPrimary,
    background = ElecBg,
    onBackground = TextPrimary,
    surface = ElecSurface,
    onSurface = TextPrimary,
    surfaceVariant = ElecSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = ElecBorder,
)

@Composable
fun SalonGalleryTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ElectricColors,
        typography = SalonTypography,
        content = content
    )
}
