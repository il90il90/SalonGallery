package com.meylon.salongallery.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotateRad
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class FishDef(
    val baseY: Float,      // 0..1 vertical band
    val size: Float,       // body length in px-ish (scaled by screen)
    val speed: Float,      // horizontal speed (fraction of width / sec)
    val dir: Int,          // +1 right, -1 left
    val phase: Float,      // undulation phase offset
    val depth: Float,      // 0 far (small, dim, slow) .. 1 near
    val top: Color, val bottom: Color, val stripe: Color?,
    val startX: Float,
)

private data class Bubble(val x: Float, val speed: Float, val r: Float, val phase: Float, val startY: Float)
private data class Plant(val x: Float, val h: Float, val sway: Float, val hue: Color, val blades: Int)

private val PALETTES = listOf(
    Triple(Color(0xFFFF8A3D), Color(0xFFE05A1A), Color(0xFFFFF4E6)),  // clownfish (orange + white stripe)
    Triple(Color(0xFF3FA9F5), Color(0xFF1C6FB5), null),              // blue tang
    Triple(Color(0xFFFFD23F), Color(0xFFE0A100), null),              // yellow tang
    Triple(Color(0xFFEDEDED), Color(0xFF9AA6B2), Color(0xFF2B2B33)), // angelfish (silver + dark stripe)
    Triple(Color(0xFFFF5E7A), Color(0xFFC62E52), Color(0xFFFFE3EA)), // rose
    Triple(Color(0xFF7FE0C4), Color(0xFF2E9B82), null),              // teal
)

/** A lively, self-contained animated aquarium rendered on the Canvas (undulating fish,
 *  swaying plants, rising bubbles, god-rays and caustic light). */
@Composable
fun AquariumScene(modifier: Modifier = Modifier) {
    var now by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (true) withFrameNanos { now = it - start }
    }
    val t = now / 1_000_000_000f  // seconds

    val rnd = remember { Random(42) }
    val fish = remember {
        List(10) { i ->
            val pal = PALETTES[rnd.nextInt(PALETTES.size)]
            val depth = rnd.nextFloat()
            FishDef(
                baseY = 0.12f + rnd.nextFloat() * 0.74f,
                size = (0.07f + rnd.nextFloat() * 0.07f) * (0.5f + depth),
                speed = (0.03f + rnd.nextFloat() * 0.05f) * (0.5f + depth),
                dir = if (rnd.nextBoolean()) 1 else -1,
                phase = rnd.nextFloat() * 6.28f,
                depth = depth,
                top = pal.first, bottom = pal.second, stripe = pal.third,
                startX = rnd.nextFloat(),
            )
        }.sortedBy { it.depth }
    }
    val bubbles = remember {
        List(34) { Bubble(rnd.nextFloat(), 0.04f + rnd.nextFloat() * 0.08f, 2f + rnd.nextFloat() * 6f, rnd.nextFloat() * 6.28f, rnd.nextFloat()) }
    }
    val plants = remember {
        List(7) { Plant(rnd.nextFloat(), 0.18f + rnd.nextFloat() * 0.24f, 0.4f + rnd.nextFloat() * 0.8f, if (rnd.nextBoolean()) Color(0xFF2E7D5B) else Color(0xFF3B8E57), 3 + rnd.nextInt(3)) }
    }

    Canvas(modifier.fillMaxSize()) {
        val w = size.width; val h = size.height

        // --- water ---
        drawRect(Brush.verticalGradient(listOf(Color(0xFF0E4D74), Color(0xFF083B5C), Color(0xFF041E30), Color(0xFF02121E))))

        // --- god rays ---
        for (i in 0 until 5) {
            val rx = w * (0.1f + 0.2f * i) + sin(t * 0.2f + i) * w * 0.02f
            val rw = w * (0.05f + 0.02f * (i % 3))
            val p = Path().apply {
                moveTo(rx, 0f); lineTo(rx + rw, 0f); lineTo(rx + rw * 2.4f, h); lineTo(rx - rw * 1.2f, h); close()
            }
            drawPath(p, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.05f), Color.Transparent)))
        }
        // --- caustic shimmer ---
        for (i in 0 until 7) {
            val cx = (w * ((i * 0.17f + t * 0.03f) % 1f))
            val cy = h * (0.05f + 0.12f * (i % 4)) + sin(t + i) * 8f
            drawCircleSoft(cx, cy, w * 0.08f, Color.White.copy(alpha = 0.05f))
        }

        // --- sandy bottom ---
        val sandTop = h * 0.9f
        drawRect(Brush.verticalGradient(listOf(Color(0xFF6B5A3E).copy(alpha = 0f), Color(0xFF7A6646), Color(0xFF5C4C34)), startY = sandTop, endY = h), topLeft = Offset(0f, sandTop), size = androidx.compose.ui.geometry.Size(w, h - sandTop))

        // --- plants ---
        plants.forEach { pl -> drawPlant(pl, w, h, t) }

        // --- fish (far to near) ---
        fish.forEach { f ->
            val travel = (f.startX + t * f.speed * f.dir)
            val cx = ((travel % 1f) + 1f) % 1f * (w * 1.2f) - w * 0.1f
            val cy = h * (f.baseY + 0.015f * sin(t * 0.6f + f.phase))
            drawFish(f, cx, cy, w, t)
        }

        // --- bubbles ---
        bubbles.forEach { b ->
            val y = h - (((b.startY + t * b.speed) % 1f) * h)
            val x = w * b.x + sin(t * 1.6f + b.phase) * 10f
            drawCircle(Color.White.copy(alpha = 0.16f), b.r, Offset(x, y))
            drawCircle(Color.White.copy(alpha = 0.28f), b.r * 0.4f, Offset(x - b.r * 0.3f, y - b.r * 0.3f))
        }

        // --- depth vignette ---
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color(0xFF02121E).copy(alpha = 0.5f)), center = Offset(w / 2, h * 0.42f), radius = w * 0.75f))
    }
}

/** A smooth closed path through [pts] (midpoint quadratic smoothing) — rounded, organic shapes. */
private fun smoothClosedPath(pts: List<Offset>): Path {
    val p = Path()
    if (pts.size < 3) return p
    val first = Offset((pts.last().x + pts[0].x) / 2f, (pts.last().y + pts[0].y) / 2f)
    p.moveTo(first.x, first.y)
    for (i in pts.indices) {
        val cur = pts[i]
        val next = pts[(i + 1) % pts.size]
        val mid = Offset((cur.x + next.x) / 2f, (cur.y + next.y) / 2f)
        p.quadraticBezierTo(cur.x, cur.y, mid.x, mid.y)
    }
    p.close()
    return p
}

private fun DrawScope.drawCircleSoft(cx: Float, cy: Float, r: Float, color: Color) {
    drawCircle(Brush.radialGradient(listOf(color, Color.Transparent), center = Offset(cx, cy), radius = r), r, Offset(cx, cy))
}

private fun DrawScope.drawPlant(pl: Plant, w: Float, h: Float, t: Float) {
    val baseX = w * pl.x
    val baseY = h * 0.93f
    val height = h * pl.h
    for (b in 0 until pl.blades) {
        val off = (b - pl.blades / 2) * (w * 0.012f)
        val sway = sin(t * 0.8f + pl.sway + b) * (w * 0.02f)
        val path = Path().apply {
            moveTo(baseX + off, baseY)
            quadraticBezierTo(baseX + off + sway * 0.5f, baseY - height * 0.5f, baseX + off + sway, baseY - height)
            quadraticBezierTo(baseX + off + sway * 0.5f + w * 0.01f, baseY - height * 0.5f, baseX + off + w * 0.012f, baseY)
            close()
        }
        drawPath(path, pl.hue.copy(alpha = 0.9f))
    }
}

/** Draws a single fish whose whole body undulates along a sine spine (realistic swim). */
private fun DrawScope.drawFish(f: FishDef, cx: Float, cy: Float, screenW: Float, t: Float) {
    val len = screenW * f.size
    val bodyW = len * 0.42f
    val dir = f.dir.toFloat()
    val segs = 18
    val wig = t * 6.5f + f.phase
    val alpha = 0.55f + 0.45f * f.depth

    // Spine points (local space, nose at x=0 toward tail at x=-len*dir) with growing undulation.
    val spine = ArrayList<Offset>(segs + 1)
    for (i in 0..segs) {
        val u = i / segs.toFloat()
        val x = -dir * len * u
        val amp = len * 0.11f * u * u
        val y = sin(wig - u * 3.2f) * amp
        spine.add(Offset(cx + x, cy + y))
    }
    fun widthAt(u: Float): Float {
        // fish silhouette: small nose, max near 0.28, taper to thin at tail
        val p = if (u < 0.28f) (u / 0.28f) else (1f - (u - 0.28f) / 0.72f)
        return bodyW * 0.5f * p.coerceIn(0.05f, 1f)
    }

    // Build outline (top then bottom reversed) using perpendiculars.
    val top = ArrayList<Offset>(segs + 1)
    val bot = ArrayList<Offset>(segs + 1)
    for (i in 0..segs) {
        val u = i / segs.toFloat()
        val a = spine[i]
        val b = spine[(i + 1).coerceAtMost(segs)]
        val dx = b.x - a.x; val dy = b.y - a.y
        val dl = kotlin.math.hypot(dx, dy).coerceAtLeast(0.001f)
        val nx = -dy / dl; val ny = dx / dl
        val wdt = widthAt(u)
        top.add(Offset(a.x + nx * wdt, a.y + ny * wdt))
        bot.add(Offset(a.x - nx * wdt, a.y - ny * wdt))
    }
    // Smooth, rounded body outline (nose → tail along the top, back along the bottom).
    val outline = ArrayList<Offset>(top.size + bot.size)
    outline.addAll(top)
    for (i in segs downTo 0) outline.add(bot[i])
    val body = smoothClosedPath(outline)

    // Tail fin at the spine end, wiggling with the body.
    val tail = spine[segs]
    val tailAngle = sin(wig - 3.2f) * 0.5f
    val tailLen = len * 0.3f
    rotateRad(tailAngle, pivot = tail) {
        val tp = Path().apply {
            moveTo(tail.x, tail.y)
            quadraticBezierTo(tail.x + dir * tailLen * 0.5f, tail.y - tailLen * 0.5f, tail.x + dir * tailLen, tail.y - tailLen * 0.42f)
            quadraticBezierTo(tail.x + dir * tailLen * 0.78f, tail.y, tail.x + dir * tailLen, tail.y + tailLen * 0.42f)
            quadraticBezierTo(tail.x + dir * tailLen * 0.5f, tail.y + tailLen * 0.5f, tail.x, tail.y)
            close()
        }
        drawPath(tp, Brush.verticalGradient(listOf(f.top.copy(alpha = alpha), f.bottom.copy(alpha = alpha))))
    }

    // Dorsal fin (top, mid-body).
    val mid = spine[(segs * 0.4f).toInt()]
    val dp = Path().apply {
        moveTo(mid.x - dir * len * 0.08f, mid.y - bodyW * 0.42f)
        quadraticBezierTo(mid.x, mid.y - bodyW * 0.78f, mid.x + dir * len * 0.12f, mid.y - bodyW * 0.3f)
        close()
    }
    drawPath(dp, f.bottom.copy(alpha = alpha * 0.9f))

    // Body with top-lit gradient.
    drawPath(body, Brush.verticalGradient(listOf(f.top.copy(alpha = alpha), f.bottom.copy(alpha = alpha))))

    // Stripes (vertical bands) for striped species.
    f.stripe?.let { st ->
        for (s in 0 until 3) {
            val u = 0.22f + s * 0.22f
            val i = (u * segs).toInt()
            val a = top[i]; val b = bot[i]
            drawLine(st.copy(alpha = alpha * 0.9f), a, b, strokeWidth = len * 0.06f)
        }
    }

    // Pectoral fin (small, near head, flapping).
    val headU = 0.3f
    val hi = (headU * segs).toInt()
    val hp = spine[hi]
    val flap = sin(wig * 1.5f) * 0.4f
    rotateRad(flap, pivot = hp) {
        val pf = Path().apply {
            moveTo(hp.x, hp.y)
            lineTo(hp.x - dir * len * 0.12f, hp.y + bodyW * 0.3f)
            lineTo(hp.x - dir * len * 0.04f, hp.y + bodyW * 0.1f)
            close()
        }
        drawPath(pf, f.top.copy(alpha = alpha * 0.7f))
    }

    // Eye near the nose.
    val nose = spine[1]
    val eyeX = nose.x + dir * len * 0.04f
    val eyeY = nose.y - bodyW * 0.12f
    drawCircle(Color.White.copy(alpha = alpha), len * 0.05f, Offset(eyeX, eyeY))
    drawCircle(Color(0xFF101418).copy(alpha = alpha), len * 0.025f, Offset(eyeX + dir * len * 0.012f, eyeY))
}
