@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.meylon.salongallery.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.meylon.salongallery.R

private fun grotesk(weight: Int) = Font(
    R.font.space_grotesk,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

private fun inter(weight: Int) = Font(
    R.font.inter,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

// Space Grotesk — bold, techy geometric display face.
val Display = FontFamily(grotesk(500), grotesk(600), grotesk(700))

// Inter — crisp modern body sans.
val Body = FontFamily(inter(400), inter(500), inter(600), inter(700))

val SalonTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Display, fontWeight = FontWeight(700),
        fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-0.5).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Display, fontWeight = FontWeight(700),
        fontSize = 28.sp, lineHeight = 33.sp, letterSpacing = (-0.5).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = Display, fontWeight = FontWeight(700),
        fontSize = 23.sp, lineHeight = 29.sp, letterSpacing = (-0.3).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Display, fontWeight = FontWeight(600),
        fontSize = 18.sp, lineHeight = 23.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Body, fontWeight = FontWeight(600),
        fontSize = 16.sp, lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Body, fontWeight = FontWeight(400),
        fontSize = 16.sp, lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Body, fontWeight = FontWeight(400),
        fontSize = 13.5.sp, lineHeight = 19.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Body, fontWeight = FontWeight(700),
        fontSize = 15.sp, lineHeight = 20.sp, letterSpacing = 0.2.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Body, fontWeight = FontWeight(600),
        fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = 0.3.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Body, fontWeight = FontWeight(700),
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 2.sp,
    ),
)
