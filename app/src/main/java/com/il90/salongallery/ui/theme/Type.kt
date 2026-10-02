@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.il90.salongallery.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.il90.salongallery.R

private fun fraunces(weight: Int) = Font(
    R.font.fraunces,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(
        FontVariation.weight(weight),
        FontVariation.Setting("opsz", 40f),
        FontVariation.Setting("SOFT", 0f),
        FontVariation.Setting("WONK", 0f),
    ),
)

private fun dmsans(weight: Int) = Font(
    R.font.dm_sans,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

private fun rubik(weight: Int) = Font(
    R.font.rubik,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

// Fraunces — warm editorial serif for headings (gallery / museum feel).
val Display = FontFamily(fraunces(400), fraunces(500), fraunces(600), fraunces(700))

// DM Sans — clean humanist sans for body & UI.
val Body = FontFamily(dmsans(400), dmsans(500), dmsans(600), dmsans(700))

// Rubik — covers Hebrew + Latin; used for user-entered content (overlay text, RSS,
// device names) so Hebrew renders beautifully and bidi/RTL resolves correctly.
val ContentFont = FontFamily(rubik(400), rubik(500), rubik(600), rubik(700))

val SalonTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Display, fontWeight = FontWeight(600),
        fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-0.4).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Display, fontWeight = FontWeight(600),
        fontSize = 28.sp, lineHeight = 33.sp, letterSpacing = (-0.3).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = Display, fontWeight = FontWeight(600),
        fontSize = 23.sp, lineHeight = 28.sp, letterSpacing = (-0.2).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Display, fontWeight = FontWeight(600),
        fontSize = 19.sp, lineHeight = 24.sp, letterSpacing = (-0.1).sp,
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
    bodySmall = TextStyle(
        fontFamily = Body, fontWeight = FontWeight(400),
        fontSize = 12.5.sp, lineHeight = 17.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Body, fontWeight = FontWeight(600),
        fontSize = 15.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Body, fontWeight = FontWeight(500),
        fontSize = 13.sp, lineHeight = 16.sp, letterSpacing = 0.2.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Body, fontWeight = FontWeight(600),
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 1.6.sp,
    ),
)
