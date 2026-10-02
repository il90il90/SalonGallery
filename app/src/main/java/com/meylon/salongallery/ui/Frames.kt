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
    val category: String = "Minimal",
    val recommended: Boolean = false,
    val adaptive: Boolean = false,  // molding colour is derived from the current photo
    val moldingColors: List<Color> = emptyList(), // gradient across the molding; empty = no molding
    val moldingFrac: Float = 0f,                  // molding thickness / shorter side
    val bevelOuter: Color? = null,                // light line on the outer edge
    val bevelInner: Color? = null,                // dark line where molding meets the mat
    val matColor: Color? = null,
    val matFrac: Float = 0f,
    val lipColor: Color? = null,                  // thin line where the mat meets the photo
)

val FRAMES = listOf(
    // ---- Smart ----
    FrameStyle(50, "Adaptive", "Smart", adaptive = true, matFrac = 0.03f),
    // ---- Minimal ----
    FrameStyle(0, "None", "Minimal"),
    FrameStyle(
        1, "Gallery", "Minimal", recommended = true,
        matColor = Color(0xFFF6F5F1), matFrac = 0.055f, lipColor = Color(0xFFCFC9BE),
    ),
    FrameStyle(
        11, "White Mat", "Minimal",
        matColor = Color(0xFFFFFFFF), matFrac = 0.085f, lipColor = Color(0xFFDAD5CB),
    ),
    FrameStyle(
        12, "Thin Line", "Minimal",
        matColor = Color(0xFF101014), matFrac = 0.012f, lipColor = Color(0xFF3A3A42),
    ),
    // ---- Metal ----
    FrameStyle(
        3, "Gold", "Metal",
        moldingColors = listOf(Color(0xFFF6E7B8), Color(0xFFD9BE8B), Color(0xFF9A824F), Color(0xFFCBAE73)),
        moldingFrac = 0.045f, bevelOuter = Color(0xFFF8EFCF), bevelInner = Color(0xFF5A4A28),
        matColor = Color(0xFFF3ECE1), matFrac = 0.03f, lipColor = Color(0xFF8A754E),
    ),
    FrameStyle(
        6, "Silver", "Metal",
        moldingColors = listOf(Color(0xFFF0F1F3), Color(0xFFBFC3C9), Color(0xFF8A9099), Color(0xFFCED2D8)),
        moldingFrac = 0.04f, bevelOuter = Color(0xFFFFFFFF), bevelInner = Color(0xFF6A7078),
        matColor = Color(0xFFF7F8FA), matFrac = 0.03f, lipColor = Color(0xFF9AA0A8),
    ),
    FrameStyle(
        7, "Rose Gold", "Metal",
        moldingColors = listOf(Color(0xFFF6D7C4), Color(0xFFE0A183), Color(0xFFB4735A), Color(0xFFE7B79E)),
        moldingFrac = 0.042f, bevelOuter = Color(0xFFFBE7DB), bevelInner = Color(0xFF7E4A38),
        matColor = Color(0xFFF6ECE6), matFrac = 0.028f, lipColor = Color(0xFFB4735A),
    ),
    FrameStyle(
        17, "Pewter", "Metal",
        moldingColors = listOf(Color(0xFFB9BCC0), Color(0xFF8E9195), Color(0xFF63666A)),
        moldingFrac = 0.038f, bevelOuter = Color(0xFFD6D8DB), bevelInner = Color(0xFF4A4D50),
        matColor = Color(0xFFEDEEF0), matFrac = 0.028f, lipColor = Color(0xFF7A7D81),
    ),
    // ---- Wood ----
    FrameStyle(
        4, "Walnut", "Wood",
        moldingColors = listOf(Color(0xFF8A5C36), Color(0xFF5A3A22), Color(0xFF34200F)),
        moldingFrac = 0.05f, bevelOuter = Color(0xFF9E6E44), bevelInner = Color(0xFF241206),
        matColor = Color(0xFFEDE6DA), matFrac = 0.022f, lipColor = Color(0xFF3A2414),
    ),
    FrameStyle(
        5, "Oak", "Wood",
        moldingColors = listOf(Color(0xFFD8BC8C), Color(0xFFB79462), Color(0xFF8C6D42)),
        moldingFrac = 0.045f, bevelOuter = Color(0xFFEBD6AE), bevelInner = Color(0xFF6A5230),
        matColor = Color(0xFFF2EDE2), matFrac = 0.025f, lipColor = Color(0xFF8C6D42),
    ),
    FrameStyle(
        10, "Cherry", "Wood",
        moldingColors = listOf(Color(0xFF9B4A34), Color(0xFF6E2E1E), Color(0xFF441810)),
        moldingFrac = 0.048f, bevelOuter = Color(0xFFB86A50), bevelInner = Color(0xFF2E0F08),
        matColor = Color(0xFFF1E7DE), matFrac = 0.03f, lipColor = Color(0xFF6E2E1E),
    ),
    FrameStyle(
        18, "Natural Pine", "Wood",
        moldingColors = listOf(Color(0xFFE7CFA2), Color(0xFFD2B176), Color(0xFFB8955A)),
        moldingFrac = 0.04f, bevelOuter = Color(0xFFF2E3C4), bevelInner = Color(0xFF8A6E40),
        matColor = Color(0xFFF6F1E7), matFrac = 0.026f, lipColor = Color(0xFFB8955A),
    ),
    // ---- Classic ----
    FrameStyle(
        8, "Vintage", "Classic",
        moldingColors = listOf(Color(0xFF9A7B4F), Color(0xFF6E5433), Color(0xFF3E2E18)),
        moldingFrac = 0.055f, bevelOuter = Color(0xFFB89968), bevelInner = Color(0xFF2A1E0E),
        matColor = Color(0xFFEAE0C8), matFrac = 0.035f, lipColor = Color(0xFF6E5433),
    ),
    FrameStyle(
        15, "Ornate Gold", "Classic",
        moldingColors = listOf(Color(0xFFFCEFC2), Color(0xFFE2C178), Color(0xFF9C7A3A), Color(0xFFD8B463)),
        moldingFrac = 0.07f, bevelOuter = Color(0xFFFFF6D6), bevelInner = Color(0xFF4E3A16),
        matColor = Color(0xFFF4EDDD), matFrac = 0.04f, lipColor = Color(0xFF9C7A3A),
    ),
    FrameStyle(
        16, "Mahogany", "Classic",
        moldingColors = listOf(Color(0xFF7A3524), Color(0xFF511E12), Color(0xFF2E0F08)),
        moldingFrac = 0.058f, bevelOuter = Color(0xFF9A4A34), bevelInner = Color(0xFF1E0804),
        matColor = Color(0xFFEFE4D8), matFrac = 0.034f, lipColor = Color(0xFF511E12),
    ),
    // ---- Modern ----
    FrameStyle(
        2, "Noir", "Modern",
        moldingColors = listOf(Color(0xFF3A3A3A), Color(0xFF141414), Color(0xFF060606)),
        moldingFrac = 0.03f, bevelOuter = Color(0xFF4A4A4A), bevelInner = Color(0xFF000000),
        matColor = Color(0xFF0C0C0C), matFrac = 0.02f, lipColor = Color(0xFF2A2A2A),
    ),
    FrameStyle(
        9, "Ebony", "Modern",
        moldingColors = listOf(Color(0xFF2C2C30), Color(0xFF141416), Color(0xFF050506)),
        moldingFrac = 0.05f, bevelOuter = Color(0xFF3C3C42), bevelInner = Color(0xFF000000),
        matColor = Color(0xFF101012), matFrac = 0.03f, lipColor = Color(0xFF34343A),
    ),
    FrameStyle(
        14, "Espresso", "Modern",
        moldingColors = listOf(Color(0xFF4A382C), Color(0xFF2E2017), Color(0xFF17100B)),
        moldingFrac = 0.045f, bevelOuter = Color(0xFF5E4838), bevelInner = Color(0xFF0E0906),
        matColor = Color(0xFF1A1510), matFrac = 0.024f, lipColor = Color(0xFF3A2C22),
    ),
    FrameStyle(
        19, "Charcoal", "Modern",
        moldingColors = listOf(Color(0xFF4E5157), Color(0xFF303236), Color(0xFF17181A)),
        moldingFrac = 0.04f, bevelOuter = Color(0xFF5E6168), bevelInner = Color(0xFF0C0D0E),
        matColor = Color(0xFF131416), matFrac = 0.026f, lipColor = Color(0xFF3A3C40),
    ),
    FrameStyle(
        20, "Slate", "Modern",
        moldingColors = listOf(Color(0xFF6E7B8A), Color(0xFF47515E), Color(0xFF2A313A)),
        moldingFrac = 0.042f, bevelOuter = Color(0xFF8795A4), bevelInner = Color(0xFF1C222A),
        matColor = Color(0xFFEDEFF2), matFrac = 0.03f, lipColor = Color(0xFF55606D),
    ),
    // ---- More Metal ----
    FrameStyle(
        21, "Brass", "Metal",
        moldingColors = listOf(Color(0xFFF2DC8E), Color(0xFFCBA84F), Color(0xFF86692A), Color(0xFFC4A24A)),
        moldingFrac = 0.044f, bevelOuter = Color(0xFFFBEFBF), bevelInner = Color(0xFF4F3C12),
        matColor = Color(0xFFF4EEDD), matFrac = 0.03f, lipColor = Color(0xFF86692A),
    ),
    FrameStyle(
        22, "Copper", "Metal",
        moldingColors = listOf(Color(0xFFF2B48C), Color(0xFFC1703F), Color(0xFF7E4322), Color(0xFFBE7446)),
        moldingFrac = 0.044f, bevelOuter = Color(0xFFF9D3B6), bevelInner = Color(0xFF55260F),
        matColor = Color(0xFFF4E8DF), matFrac = 0.03f, lipColor = Color(0xFF7E4322),
    ),
    FrameStyle(
        23, "Platinum", "Metal",
        moldingColors = listOf(Color(0xFFFCFDFE), Color(0xFFD6DADF), Color(0xFFA7ADB5), Color(0xFFE6E9EC)),
        moldingFrac = 0.036f, bevelOuter = Color(0xFFFFFFFF), bevelInner = Color(0xFF888E96),
        matColor = Color(0xFFFBFCFD), matFrac = 0.028f, lipColor = Color(0xFFB4BAC2),
    ),
    // ---- More Wood ----
    FrameStyle(
        24, "Driftwood", "Wood",
        moldingColors = listOf(Color(0xFFBFB3A2), Color(0xFF938876), Color(0xFF675E4F)),
        moldingFrac = 0.046f, bevelOuter = Color(0xFFD6CBBB), bevelInner = Color(0xFF4A4336),
        matColor = Color(0xFFF3EFE6), matFrac = 0.028f, lipColor = Color(0xFF675E4F),
    ),
    FrameStyle(
        25, "Teak", "Wood",
        moldingColors = listOf(Color(0xFFC68B4E), Color(0xFF9A6530), Color(0xFF66401C)),
        moldingFrac = 0.046f, bevelOuter = Color(0xFFDDA869), bevelInner = Color(0xFF4A2D12),
        matColor = Color(0xFFF2E9DC), matFrac = 0.028f, lipColor = Color(0xFF66401C),
    ),
    // ---- More Classic ----
    FrameStyle(
        26, "Baroque", "Classic",
        moldingColors = listOf(Color(0xFFF6E0A0), Color(0xFFC79B42), Color(0xFF7E5E22), Color(0xFFBE954A)),
        moldingFrac = 0.078f, bevelOuter = Color(0xFFFFF2CC), bevelInner = Color(0xFF3E2C0E),
        matColor = Color(0xFFF2EAD6), matFrac = 0.042f, lipColor = Color(0xFF7E5E22),
    ),
    FrameStyle(
        27, "Ivory", "Classic",
        moldingColors = listOf(Color(0xFFFBF7EE), Color(0xFFE6DECB), Color(0xFFC9BFA6)),
        moldingFrac = 0.05f, bevelOuter = Color(0xFFFFFFFA), bevelInner = Color(0xFFB0A588),
        matColor = Color(0xFFF7F2E7), matFrac = 0.034f, lipColor = Color(0xFFC9BFA6),
    ),
    // ---- More Minimal ----
    FrameStyle(
        28, "Black Mat", "Minimal",
        matColor = Color(0xFF0A0A0C), matFrac = 0.075f, lipColor = Color(0xFF2E2E34),
    ),
)

fun frameById(id: Int): FrameStyle = FRAMES.firstOrNull { it.id == id } ?: FRAMES[0]

/** Frame categories in display order, each with its styles. */
val FRAME_CATEGORIES: List<Pair<String, List<FrameStyle>>> =
    listOf("Smart", "Minimal", "Wood", "Metal", "Classic", "Modern").map { cat ->
        cat to FRAMES.filter { it.category == cat }
    }

/** Builds a molding tuned to a dominant photo colour (for the Adaptive frame). */
fun adaptiveFrameFor(color: Color): FrameStyle {
    fun mix(c: Color, other: Color, t: Float) = Color(
        red = c.red + (other.red - c.red) * t,
        green = c.green + (other.green - c.green) * t,
        blue = c.blue + (other.blue - c.blue) * t,
    )
    val light = mix(color, Color.White, 0.35f)
    val dark = mix(color, Color.Black, 0.45f)
    return FrameStyle(
        id = 50, name = "Adaptive", category = "Smart", adaptive = true,
        moldingColors = listOf(light, color, dark),
        moldingFrac = 0.045f, bevelOuter = mix(color, Color.White, 0.55f), bevelInner = mix(color, Color.Black, 0.6f),
        matColor = mix(color, Color.White, 0.82f), matFrac = 0.03f, lipColor = dark,
    )
}

/** The recommended default frame. */
val RECOMMENDED_FRAME: FrameStyle = FRAMES.firstOrNull { it.recommended } ?: FRAMES[1]

/**
 * Wraps [content] (the photo/video, which should fill) with the given frame.
 * [widthScale] multiplies the molding + mat thickness (user-adjustable, ~0.5..2).
 */
@Composable
fun FramedContent(
    frameId: Int,
    widthScale: Float = 1f,
    adaptiveColor: Color? = null,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val base = frameById(frameId)
    val f = if (base.adaptive && adaptiveColor != null) adaptiveFrameFor(adaptiveColor) else base
    val ws = widthScale.coerceIn(0.4f, 2.2f)
    BoxWithConstraints(modifier.background(Color.Black)) {
        val minSide = minOf(maxWidth, maxHeight)
        val molding = minSide * f.moldingFrac * ws
        val mat = minSide * f.matFrac * ws
        val framed = f.moldingColors.isNotEmpty() || f.matColor != null
        // How deep the photo sits below the mat/molding — drives the inner-recess shadow.
        val recess = minSide * (if (f.moldingColors.isNotEmpty()) 0.016f else 0.010f) * ws

        if (f.moldingColors.isNotEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    // A cross-molding sheen: lit on the top-left bevel, shadowed on the bottom-right.
                    .background(Brush.linearGradient(f.moldingColors))
                    .then(if (f.bevelOuter != null) Modifier.border(1.5.dp, f.bevelOuter) else Modifier),
            ) {
                // Soft shadow the molding casts down onto the mat (depth of the moulding profile).
                Box(Modifier.fillMaxSize().padding(molding * 0.82f)) {
                    Box(
                        Modifier.fillMaxSize()
                            .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent)))
                    )
                }
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(molding)
                        .then(if (f.bevelInner != null) Modifier.border(2.dp, f.bevelInner) else Modifier),
                ) { MatAndPhoto(f, mat, recess, framed, content) }
            }
        } else {
            MatAndPhoto(f, mat, recess, framed, content)
        }
    }
}

@Composable
private fun MatAndPhoto(
    f: FrameStyle,
    mat: androidx.compose.ui.unit.Dp,
    recess: androidx.compose.ui.unit.Dp,
    framed: Boolean,
    content: @Composable BoxScope.() -> Unit,
) {
    if (f.matColor != null) {
        Box(Modifier.fillMaxSize().background(f.matColor)) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(mat)
                    .then(if (f.lipColor != null) Modifier.border(1.dp, f.lipColor) else Modifier),
            ) {
                content()
                if (framed) InnerRecessShadow(recess)
            }
        }
    } else {
        Box(
            Modifier.fillMaxSize().then(if (f.lipColor != null) Modifier.border(1.dp, f.lipColor) else Modifier),
        ) {
            content()
            if (framed) InnerRecessShadow(recess)
        }
    }
}

/**
 * A soft shadow on the four inner edges of the photo opening, so the image reads as set *into*
 * the frame rather than printed flat on top of it — the key cue that sells a real frame.
 */
@Composable
private fun BoxScope.InnerRecessShadow(depth: androidx.compose.ui.unit.Dp) {
    androidx.compose.foundation.Canvas(Modifier.matchParentSize()) {
        val d = depth.toPx().coerceAtLeast(2f)
        val sh = Color.Black.copy(alpha = 0.38f)
        val w = size.width; val h = size.height
        drawRect(Brush.verticalGradient(listOf(sh, Color.Transparent), startY = 0f, endY = d),
            size = androidx.compose.ui.geometry.Size(w, d))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, sh), startY = h - d, endY = h),
            topLeft = androidx.compose.ui.geometry.Offset(0f, h - d), size = androidx.compose.ui.geometry.Size(w, d))
        drawRect(Brush.horizontalGradient(listOf(sh, Color.Transparent), startX = 0f, endX = d),
            size = androidx.compose.ui.geometry.Size(d, h))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, sh), startX = w - d, endX = w),
            topLeft = androidx.compose.ui.geometry.Offset(w - d, 0f), size = androidx.compose.ui.geometry.Size(d, h))
    }
}
