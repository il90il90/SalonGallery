package com.meylon.salongallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** A frame/mat style rendered around the displayed media. */
data class FrameStyle(
    val id: Int,
    val name: String,
    val matColor: Color,
    val matFraction: Float,   // border thickness as a fraction of the shorter side
    val lineColor: Color?,
    val lineWidth: Dp,
)

val FRAMES = listOf(
    FrameStyle(0, "None", Color.Black, 0f, null, 0.dp),
    FrameStyle(1, "White", Color(0xFFF7F7F4), 0.05f, Color(0xFFE1E1DC), 1.dp),
    FrameStyle(2, "Black", Color(0xFF0A0A0A), 0.05f, Color(0xFF2A2A2A), 1.dp),
    FrameStyle(3, "Gold", Color.Black, 0.035f, Color(0xFFD9BE8B), 3.dp),
    FrameStyle(4, "Wood", Color(0xFF5A3A22), 0.045f, Color(0xFF33200F), 2.dp),
)

fun frameById(id: Int): FrameStyle = FRAMES.getOrElse(id) { FRAMES[0] }

/** Wraps [content] with the given frame/mat. */
@Composable
fun FramedContent(
    frameId: Int,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val f = frameById(frameId)
    BoxWithConstraints(modifier.background(f.matColor)) {
        val pad = minOf(maxWidth, maxHeight) * f.matFraction
        Box(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .then(if (f.lineColor != null) Modifier.border(f.lineWidth, f.lineColor) else Modifier),
            content = content,
        )
    }
}
