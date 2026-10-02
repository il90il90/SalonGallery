package com.il90.salongallery.ui

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/*
 * Pure geometry for the multi-photo "spread" layouts — no Compose here, so every layout can be
 * checked off-device (see SpreadLayoutsTest): each one must place exactly n prints, and every print,
 * tilt included, must sit fully inside the slide. [placeSpread] enforces that last rule itself as a
 * final safety pass, so a layout can never let a photo run off the screen.
 */

/** The look of one placed print. */
enum class PrintStyle {
    /** Edge-to-edge photo with a hairline bevel (grids, columns, film frames). */
    CELL,
    /** A photo print with an even white border. */
    PRINT,
    /** A Polaroid: white border, deep bottom lip. */
    POLAROID,
    /** A black picture frame with a white mat (gallery wall). */
    FRAMED,
    /** A round cut-out with a white ring. */
    CIRCLE,
}

/** The surface a spread sits on. */
enum class SpreadSurface { MAT, TABLE, CORK, FILM, VELVET, WALL, LINEN, PAPER, BLACK }

/** The multi-photo layouts built from a [SpreadPlan] (Mosaic and Scatter have their own renderers). */
enum class SpreadStyle(val surface: SpreadSurface) {
    GRID(SpreadSurface.MAT),
    POLAROID(SpreadSurface.CORK),
    FILMSTRIP(SpreadSurface.FILM),
    STACK(SpreadSurface.TABLE),
    FAN(SpreadSurface.VELVET),
    GALLERY(SpreadSurface.WALL),
    CLOTHESLINE(SpreadSurface.LINEN),
    BUBBLES(SpreadSurface.MAT),
    MAGAZINE(SpreadSurface.PAPER),
    COLUMNS(SpreadSurface.BLACK),
}

/**
 * One print: its un-rotated box ([x],[y] top-left, [w]×[h], in the slide's units — dp on screen),
 * turned by [rot] degrees about its own centre. Later items draw on top.
 */
data class Placement(val x: Float, val y: Float, val w: Float, val h: Float, val rot: Float, val style: PrintStyle) {
    val cx get() = x + w / 2f
    val cy get() = y + h / 2f
    /** Half the size of the axis-aligned box around the tilted print. */
    val extX get() = rotExt(w, h, rot).first
    val extY get() = rotExt(w, h, rot).second
}

/**
 * Where the photos go plus surface details: clothes[lines] (empty when none) and the film band
 * ([film], null when none).
 */
data class SpreadPlan(
    val items: List<Placement>,
    val lines: List<Clothesline> = emptyList(),
    val film: FilmBand? = null,
)

/** A line strung from ([y0] at the left edge) to ([y1] at the right edge), sagging [sag] mid-way. */
data class Clothesline(val y0: Float, val y1: Float, val sag: Float) {
    fun yAt(t: Float) = y0 + (y1 - y0) * t + sag * 4f * t * (1f - t)
}

/** The black film band behind a filmstrip: its rect and whether the frames run horizontally. */
data class FilmBand(val x: Float, val y: Float, val w: Float, val h: Float, val horizontal: Boolean)

private fun rotExt(w: Float, h: Float, rotDeg: Float): Pair<Float, Float> {
    val r = Math.toRadians(rotDeg.toDouble())
    val c = abs(cos(r)).toFloat(); val s = abs(sin(r)).toFloat()
    return (w * c + h * s) / 2f to (w * s + h * c) / 2f
}

/** Builds [style] for [n] photos (clamped to 3..5) on a [w]×[h] slide; [seed] varies it stably. */
fun placeSpread(style: SpreadStyle, n: Int, w: Float, h: Float, seed: Int): SpreadPlan {
    val count = n.coerceIn(3, 5)
    if (!(w > 0f) || !(h > 0f) || w.isInfinite() || h.isInfinite()) return SpreadPlan(emptyList())
    val r = kotlin.random.Random(seed.toLong() * 2654435761L + style.ordinal * 97L + 1)
    val raw = when (style) {
        SpreadStyle.GRID -> SpreadPlan(grid(count, w, h))
        SpreadStyle.POLAROID -> SpreadPlan(tossed(count, w, h, r, PrintStyle.POLAROID, 1.2f, 7f))
        SpreadStyle.FILMSTRIP -> filmstrip(count, w, h)
        SpreadStyle.STACK -> SpreadPlan(stack(count, w, h, r))
        SpreadStyle.FAN -> SpreadPlan(fan(count, w, h))
        SpreadStyle.GALLERY -> SpreadPlan(gallery(count, w, h, r))
        SpreadStyle.CLOTHESLINE -> clothesline(count, w, h, r)
        SpreadStyle.BUBBLES -> SpreadPlan(bubbles(count, w, h, r))
        SpreadStyle.MAGAZINE -> SpreadPlan(magazine(count, w, h, r))
        SpreadStyle.COLUMNS -> SpreadPlan(columns(count, w, h))
    }
    // Safety pass: shrink anything too big for the slide, then pull it fully inside.
    val margin = min(w, h) * 0.012f
    return raw.copy(items = raw.items.map { contain(it, w, h, margin) })
}

/** Scales a print down if its tilted box can't fit, then moves it so the whole box is inside. */
internal fun contain(p: Placement, w: Float, h: Float, margin: Float): Placement {
    var pw = p.w; var ph = p.h
    val (ex, ey) = rotExt(pw, ph, p.rot)
    val fit = min(((w - 2 * margin) / (2 * ex)).coerceAtMost(1f), ((h - 2 * margin) / (2 * ey)).coerceAtMost(1f))
    if (fit < 1f) { pw *= fit; ph *= fit }
    val (ex2, ey2) = rotExt(pw, ph, p.rot)
    val cx = p.cx.coerceIn(margin + ex2, maxOf(margin + ex2, w - margin - ex2))
    val cy = p.cy.coerceIn(margin + ey2, maxOf(margin + ey2, h - margin - ey2))
    return p.copy(x = cx - pw / 2f, y = cy - ph / 2f, w = pw, h = ph)
}

// ---- the layouts ---------------------------------------------------------------------------

/** Rows of equal cells filling the slide; a short row's cells widen to fill it. */
private fun grid(n: Int, w: Float, h: Float): List<Placement> {
    val g = min(w, h) * 0.02f
    val landscape = w >= h
    val rows: List<Int> = if (landscape) when (n) { 3 -> listOf(3); 4 -> listOf(2, 2); else -> listOf(3, 2) }
    else when (n) { 3 -> listOf(1, 2); 4 -> listOf(2, 2); else -> listOf(2, 1, 2) }
    val out = mutableListOf<Placement>()
    val rh = (h - g * (rows.size + 1)) / rows.size
    rows.forEachIndexed { ri, cnt ->
        val cw = (w - g * (cnt + 1)) / cnt
        for (c in 0 until cnt) out += Placement(g + c * (cw + g), g + ri * (rh + g), cw, rh, 0f, PrintStyle.CELL)
    }
    return out
}

/** Prints tossed on a jittered grid with a slight tilt (Polaroids on a cork board). */
private fun tossed(n: Int, w: Float, h: Float, r: kotlin.random.Random, style: PrintStyle, aspectHW: Float, maxTilt: Float): List<Placement> {
    val landscape = w >= h
    val cols = if (landscape) min(n, 3) else 2
    val rows = (n + cols - 1) / cols
    val cellW = w / cols; val cellH = h / rows
    // Fit a print (width pw, height pw*aspect) in its cell with room to tilt.
    val pw = min(cellW * 0.8f, cellH * 0.8f / aspectHW)
    return List(n) { k ->
        val col = k % cols; val row = k / cols
        val rowCount = if (row == rows - 1) n - row * cols else cols
        val rowOffset = (cols - rowCount) * cellW / 2f
        val s = pw * (0.92f + r.nextFloat() * 0.12f)
        val ph = s * aspectHW
        val cx = rowOffset + col * cellW + cellW / 2f + (r.nextFloat() - 0.5f) * cellW * 0.16f
        val cy = row * cellH + cellH / 2f + (r.nextFloat() - 0.5f) * cellH * 0.12f
        Placement(cx - s / 2f, cy - ph / 2f, s, ph, (r.nextFloat() - 0.5f) * 2f * maxTilt, style)
    }
}

/** A strip of 35 mm frames across a black film band (down the screen when it's portrait). */
private fun filmstrip(n: Int, w: Float, h: Float): SpreadPlan {
    val horizontal = w >= h
    val along = if (horizontal) w else h
    val across = if (horizontal) h else w
    val g = along * 0.012f
    // Frames are always landscape 3:2 photos: on a horizontal strip the long side runs along it,
    // on a vertical (portrait-screen) strip the long side runs across it.
    val crossPerAlong = if (horizontal) 1f / 1.5f else 1.5f
    var fa = (along * 0.94f - g * (n - 1)) / n           // frame size along the strip
    // Band = frame + a sprocket row each side; keep it within ~86% of the cross size.
    fun band(a: Float) = a * crossPerAlong * 1.5f
    if (band(fa) > across * 0.86f) fa = across * 0.86f / (crossPerAlong * 1.5f)
    val fc = fa * crossPerAlong                           // frame size across the strip
    val sp = fc * 0.25f
    val total = fa * n + g * (n - 1)
    val start = (along - total) / 2f
    val mid = across / 2f
    val items = List(n) { k ->
        val a = start + k * (fa + g)
        if (horizontal) Placement(a, mid - fc / 2f, fa, fc, 0f, PrintStyle.CELL)
        else Placement(mid - fc / 2f, a, fc, fa, 0f, PrintStyle.CELL)
    }
    val bandLen = total + g * 4
    val bandStart = (along - bandLen) / 2f
    val bandObj = if (horizontal) FilmBand(bandStart, mid - fc / 2f - sp, bandLen, fc + 2 * sp, true)
    else FilmBand(mid - fc / 2f - sp, bandStart, fc + 2 * sp, bandLen, false)
    return SpreadPlan(items, film = bandObj)
}

/** A loose pile of prints, each nudged and turned, the last one on top and nearly straight. */
private fun stack(n: Int, w: Float, h: Float, r: kotlin.random.Random): List<Placement> {
    val landscape = w >= h
    val ph = if (landscape) h * 0.56f else w * 0.5f
    val pw = ph * 1.33f
    return List(n) { k ->
        val last = k == n - 1
        val spreadX = w * 0.17f; val spreadY = h * 0.14f
        // Fan the lower prints outwards around the centre, alternating sides.
        val side = if (k % 2 == 0) -1f else 1f
        val dx = if (last) 0f else side * spreadX * (0.45f + r.nextFloat() * 0.55f)
        val dy = if (last) 0f else (r.nextFloat() - 0.5f) * 2f * spreadY
        val rot = if (last) (r.nextFloat() - 0.5f) * 4f else side * (5f + r.nextFloat() * 9f)
        val s = if (last) 1.06f else 0.9f + r.nextFloat() * 0.1f
        Placement(w / 2f + dx - pw * s / 2f, h / 2f + dy - ph * s / 2f, pw * s, ph * s, rot, PrintStyle.PRINT)
    }
}

/** Cards fanned out like a hand of playing cards, pivoting on a point below the screen. */
private fun fan(n: Int, w: Float, h: Float): List<Placement> {
    val landscape = w >= h
    val ch = if (landscape) h * 0.6f else h * 0.42f
    val cw = ch * 0.74f
    val step = if (landscape) 13f else 11f
    val radius = ch * 0.95f
    val pivotY = h * 0.5f + radius * 0.92f
    val stretch = if (landscape) 1.55f else 1.0f                 // wider arc on a wide screen
    return List(n) { k ->
        val a = (k - (n - 1) / 2f) * step
        val rad = Math.toRadians(a.toDouble())
        val cx = w / 2f + (radius * sin(rad)).toFloat() * stretch
        val cy = pivotY - (radius * cos(rad)).toFloat()
        Placement(cx - cw / 2f, cy - ch / 2f, cw, ch, a, PrintStyle.PRINT)
    }
}

/** A salon-style gallery wall: framed photos of mixed sizes in a balanced arrangement. */
private fun gallery(n: Int, w: Float, h: Float, r: kotlin.random.Random): List<Placement> {
    // Templates in a unit box (x, y, w, h), laid out for landscape and transposed for portrait.
    val land: List<FloatArray> = when (n) {
        3 -> listOf(f(0.05f, 0.12f, 0.36f, 0.76f), f(0.46f, 0.08f, 0.49f, 0.44f), f(0.52f, 0.58f, 0.36f, 0.34f))
        4 -> listOf(f(0.30f, 0.10f, 0.40f, 0.80f), f(0.05f, 0.14f, 0.21f, 0.34f), f(0.05f, 0.54f, 0.21f, 0.32f), f(0.74f, 0.22f, 0.21f, 0.56f))
        else -> listOf(
            f(0.31f, 0.10f, 0.38f, 0.62f), f(0.05f, 0.10f, 0.22f, 0.36f), f(0.05f, 0.52f, 0.22f, 0.38f),
            f(0.73f, 0.10f, 0.22f, 0.44f), f(0.73f, 0.60f, 0.22f, 0.30f),
        )
    }
    val mirror = r.nextBoolean()
    val landscape = w >= h
    return land.map { t ->
        var (x, y, tw, th) = listOf(t[0], t[1], t[2], t[3])
        if (mirror) x = 1f - x - tw
        if (!landscape) { val ox = x; val ow = tw; x = y; y = ox; tw = th; th = ow }
        Placement(x * w, y * h, tw * w, th * h, 0f, PrintStyle.FRAMED)
    }
}

private fun f(vararg v: Float) = v

/** Polaroids pegged to a sagging line strung across the slide (two lines on a portrait screen). */
private fun clothesline(n: Int, w: Float, h: Float, r: kotlin.random.Random): SpreadPlan {
    val landscape = w >= h
    val perLine = if (landscape) listOf(n) else listOf((n + 1) / 2, n / 2)
    val lineBand = h / perLine.size                       // vertical room each line gets
    val items = mutableListOf<Placement>()
    val lines = mutableListOf<Clothesline>()
    perLine.forEachIndexed { li, cnt ->
        val top = li * lineBand
        val y0 = top + lineBand * (0.13f + r.nextFloat() * 0.05f)
        val y1 = top + lineBand * (0.13f + r.nextFloat() * 0.05f)
        val sag = lineBand * 0.06f
        lines += Clothesline(y0, y1, sag)
        val slot = w * 0.92f / cnt
        var pw = slot * 0.84f
        var ph = pw * 1.2f
        val maxH = lineBand * 0.72f
        if (ph > maxH) { ph = maxH; pw = ph / 1.2f }
        for (k in 0 until cnt) {
            val cx = w * 0.04f + slot * (k + 0.5f)
            val t = cx / w
            // The line's height here: straight between its ends, plus a parabolic sag.
            val ly = y0 + (y1 - y0) * t + sag * 4f * t * (1f - t)
            items += Placement(cx - pw / 2f, ly - ph * 0.04f, pw, ph, (r.nextFloat() - 0.5f) * 7f, PrintStyle.POLAROID)
        }
    }
    return SpreadPlan(items, lines = lines)
}

/** Round cut-outs: one large bubble and smaller ones gathered around it. */
private fun bubbles(n: Int, w: Float, h: Float, r: kotlin.random.Random): List<Placement> {
    // (cx, cy, diameter) in units of the short side, for a landscape slide centred on (0,0).
    val t: List<FloatArray> = when (n) {
        3 -> listOf(f(-0.42f, 0f, 0.84f), f(0.5f, -0.22f, 0.5f), f(0.46f, 0.3f, 0.4f))
        4 -> listOf(f(-0.1f, 0f, 0.82f), f(-0.78f, -0.18f, 0.5f), f(0.66f, -0.24f, 0.46f), f(0.62f, 0.3f, 0.36f))
        else -> listOf(f(0f, 0.02f, 0.78f), f(-0.74f, -0.24f, 0.46f), f(-0.68f, 0.3f, 0.34f), f(0.72f, -0.26f, 0.44f), f(0.7f, 0.28f, 0.36f))
    }
    val landscape = w >= h
    val mirror = r.nextBoolean()
    val pts = t.map { b ->
        var bx = b[0] * (if (mirror) -1f else 1f); var by = b[1]
        if (!landscape) { val o = bx; bx = by; by = o }
        floatArrayOf(bx, by, b[2])
    }
    // Scale the whole cluster as one so it fits the slide — keeps the bubbles apart on any shape.
    val minX = pts.minOf { it[0] - it[2] / 2f }; val maxX = pts.maxOf { it[0] + it[2] / 2f }
    val minY = pts.minOf { it[1] - it[2] / 2f }; val maxY = pts.maxOf { it[1] + it[2] / 2f }
    val k = min(w * 0.94f / (maxX - minX), h * 0.94f / (maxY - minY))
    val ox = w / 2f - (minX + maxX) / 2f * k; val oy = h / 2f - (minY + maxY) / 2f * k
    return pts.map { b ->
        val d = b[2] * k
        Placement(ox + b[0] * k - d / 2f, oy + b[1] * k - d / 2f, d, d, 0f, PrintStyle.CIRCLE)
    }
}

/** A magazine spread: one bleed photo across most of the page, bordered insets beside it. */
private fun magazine(n: Int, w: Float, h: Float, r: kotlin.random.Random): List<Placement> {
    val landscape = w >= h
    val rest = n - 1
    val heroLeft = r.nextBoolean()
    val out = mutableListOf<Placement>()
    if (landscape) {
        val heroW = w * 0.62f
        val hx = if (heroLeft) 0f else w - heroW
        out += Placement(hx, 0f, heroW, h, 0f, PrintStyle.CELL)
        val colW = w - heroW
        val g = h * 0.04f
        val ih = (h - g * (rest + 1)) / rest
        val iw = min(colW * 0.86f, ih * 1.45f)
        // Insets straddle the hero's edge a little, like a cut-out laid on the page.
        val overlap = iw * 0.14f
        val cx = if (heroLeft) heroW + colW / 2f - overlap / 2f else colW / 2f + overlap / 2f
        for (k in 0 until rest) out += Placement(cx - iw / 2f, g + k * (ih + g), iw, ih, 0f, PrintStyle.PRINT)
    } else {
        val heroH = h * 0.58f
        val hy = if (heroLeft) 0f else h - heroH
        out += Placement(0f, hy, w, heroH, 0f, PrintStyle.CELL)
        // The insets go two to a row beneath (or above) the hero.
        val cols = min(rest, 2)
        val rows = (rest + cols - 1) / cols
        val area = h - heroH
        val g = w * 0.04f
        val iw = (w - g * (cols + 1)) / cols
        val ih = min((area - g * (rows + 1)) / rows, iw * 1.0f)
        val overlap = ih * 0.12f
        val blockH = rows * ih + (rows - 1) * g
        val by = if (heroLeft) heroH + (area - blockH) / 2f - overlap else (area - blockH) / 2f + overlap
        for (k in 0 until rest) {
            val row = k / cols; val col = k % cols
            val inRow = if (row == rows - 1) rest - row * cols else cols
            val rowOff = (cols - inRow) * (iw + g) / 2f
            out += Placement(g + rowOff + col * (iw + g), by + row * (ih + g), iw, ih, 0f, PrintStyle.PRINT)
        }
    }
    return out
}

/** Tall slices side by side (stacked bands on a portrait screen), separated by thin black gaps. */
private fun columns(n: Int, w: Float, h: Float): List<Placement> {
    val landscape = w >= h
    val g = min(w, h) * 0.012f
    return if (landscape) {
        val cw = (w - g * (n + 1)) / n
        List(n) { k -> Placement(g + k * (cw + g), g, cw, h - 2 * g, 0f, PrintStyle.CELL) }
    } else {
        val ch = (h - g * (n + 1)) / n
        List(n) { k -> Placement(g, g + k * (ch + g), w - 2 * g, ch, 0f, PrintStyle.CELL) }
    }
}
