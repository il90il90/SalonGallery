package com.meylon.salongallery.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GalleryColors = darkColorScheme(
    primary = NeonCyan,          // clay
    onPrimary = OnAccent,        // white
    secondary = NeonViolet,      // plum
    onSecondary = OnAccent,
    background = ElecBg,          // espresso ground
    onBackground = TextPrimary,  // warm off-white
    surface = ElecSurface,       // card
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
        colorScheme = GalleryColors,
        typography = SalonTypography,
        content = content
    )
}
