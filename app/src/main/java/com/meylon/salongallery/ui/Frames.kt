package com.meylon.salongallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A realistic frame: an outer molding drawn with a diagonal gradient (to fake a
 * bevel catching light), bevel edge-lines for depth, and an inner mat/passe-partout.
 */
data class FrameStyle(
    val id: Int,
    val name: String,
    val moldingColors: List<Color> = emptyList(), // gradient across the molding; empty = no molding
    val moldingFrac: Float = 0f,                  // molding thickness / shorter side
    val bevelOuter: Color? = null,                // light line on the outer edge
    val bevelInner: Color? = null,                // dark line where molding meets the mat
    val matColor: Color? = null,
    val matFrac: Float = 0f,
    val lipColor: Color? = null,                  // thin line where the mat meets the photo
)

val FRAMES = listOf(
    FrameStyle(0, "None"),
    FrameStyle(
        1, "Gallery",
        matColor = Color(0xFFF6F5F1), matFrac = 0.055f, lipColor = Color(0xFFCFC9BE),
    ),
    FrameStyle(
        2, "Noir",
        moldingColors = listOf(Color(0xFF3A3A3A), Color(0xFF141414), Color(0xFF060606)),
        moldingFrac = 0.03f, bevelOuter = Color(0xFF4A4A4A), bevelInner = Color(0xFF000000),
        matColor = Color(0xFF0C0C0C), matFrac = 0.02f, lipColor = Color(0xFF2A2A2A),
    ),
    FrameStyle(
        3, "Gold",
        moldingColors = listOf(Color(0xFFF6E7B8), Color(0xFFD9BE8B), Color(0xFF9A824F), Color(0xFFCBAE73)),
        moldingFrac = 0.045f, bevelOuter = Color(0xFFF8EFCF), bevelInner = Color(0xFF5A4A28),
        matColor = Color(0xFFF3ECE1), matFrac = 0.03f, lipColor = Color(0xFF8A754E),
    ),
    FrameStyle(
        4, "Walnut",
        moldingColors = listOf(Color(0xFF8A5C36), Color(0xFF5A3A22), Color(0xFF34200F)),
        moldingFrac = 0.05f, bevelOuter = Color(0xFF9E6E44), bevelInner = Color(0xFF241206),
        matColor = Color(0xFFEDE6DA), matFrac = 0.022f, lipColor = Color(0xFF3A2414),
    ),
    FrameStyle(
        5, "Oak",
        moldingColors = listOf(Color(0xFFD8BC8C), Color(0xFFB79462), Color(0xFF8C6D42)),
        moldingFrac = 0.045f, bevelOuter = Color(0xFFEBD6AE), bevelInner = Color(0xFF6A5230),
        matColor = Color(0xFFF2EDE2), matFrac = 0.025f, lipColor = Color(0xFF8C6D42),
    ),
)

fun frameById(id: Int): FrameStyle = FRAMES.getOrElse(id) { FRAMES[0] }

/** Wraps [content] (the photo/video, which should fill) with the given frame. */
@Composable
fun FramedContent(
    frameId: Int,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val f = frameById(frameId)
    BoxWithConstraints(modifier.background(Color.Black)) {
        val minSide = minOf(maxWidth, maxHeight)
        val molding = minSide * f.moldingFrac
        val mat = minSide * f.matFrac

        if (f.moldingColors.isNotEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(f.moldingColors))
                    .then(if (f.bevelOuter != null) Modifier.border(1.5.dp, f.bevelOuter) else Modifier),
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(molding)
                        .then(if (f.bevelInner != null) Modifier.border(2.dp, f.bevelInner) else Modifier),
                ) { MatAndPhoto(f, mat, content) }
            }
        } else {
            MatAndPhoto(f, mat, content)
        }
    }
}

@Composable
private fun MatAndPhoto(f: FrameStyle, mat: androidx.compose.ui.unit.Dp, content: @Composable BoxScope.() -> Unit) {
    if (f.matColor != null) {
        Box(Modifier.fillMaxSize().background(f.matColor)) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(mat)
                    .then(if (f.lipColor != null) Modifier.border(1.dp, f.lipColor) else Modifier),
                content = content,
            )
        }
    } else {
        Box(
            Modifier.fillMaxSize().then(if (f.lipColor != null) Modifier.border(1.dp, f.lipColor) else Modifier),
            content = content,
        )
    }
}
