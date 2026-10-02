package com.il90.salongallery.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Every spread places all its photos, fully on the slide (tilt included), on any screen shape. */
class SpreadLayoutsTest {

    private val screens = listOf(
        960f to 540f, 1280f to 720f, 540f to 960f, 800f to 600f, 600f to 800f, 1100f to 450f, 700f to 700f,
        // Inside a thick frame/mat the slide is a little smaller and not exactly 16:9.
        880f to 470f,
    )

    @Test fun everyLayoutStaysOnScreen() {
        for (style in SpreadStyle.entries) for ((w, h) in screens) for (n in style.minN..style.maxN) for (seed in 0 until 60) {
            val plan = placeSpread(style, n, w, h, seed)
            val what = "$style n=$n ${w}x$h seed=$seed"
            assertEquals("count $what", n, plan.items.size)
            plan.items.forEachIndexed { i, p ->
                assertTrue("size $what #$i", p.w > 8f && p.h > 8f)
                val eps = 0.01f
                assertTrue("left $what #$i", p.cx - p.extX >= -eps)
                assertTrue("top $what #$i", p.cy - p.extY >= -eps)
                assertTrue("right $what #$i", p.cx + p.extX <= w + eps)
                assertTrue("bottom $what #$i", p.cy + p.extY <= h + eps)
            }
        }
    }

    /** Layouts that are meant to tile must not cover one another (only Stack/Fan/Magazine overlap). */
    @Test fun tiledLayoutsDoNotOverlap() {
        val tiled = listOf(SpreadStyle.GRID, SpreadStyle.FILMSTRIP, SpreadStyle.GALLERY, SpreadStyle.COLUMNS, SpreadStyle.BUBBLES, SpreadStyle.FRAMES, SpreadStyle.PATCHWORK)
        for (style in tiled) for ((w, h) in screens) for (n in style.minN..style.maxN) for (seed in 0 until 10) {
            val items = placeSpread(style, n, w, h, seed).items
            for (a in items.indices) for (b in a + 1 until items.size) {
                val p = items[a]; val q = items[b]
                val overlapX = minOf(p.x + p.w, q.x + q.w) - maxOf(p.x, q.x)
                val overlapY = minOf(p.y + p.h, q.y + q.h) - maxOf(p.y, q.y)
                val touching = overlapX > 1f && overlapY > 1f
                val circlesOk = style == SpreadStyle.BUBBLES && run {
                    val dx = p.cx - q.cx; val dy = p.cy - q.cy
                    kotlin.math.sqrt(dx * dx + dy * dy) >= (p.w + q.w) / 2f - 1f
                }
                assertTrue("$style n=$n ${w}x$h seed=$seed #$a/#$b overlap", !touching || circlesOk)
            }
        }
    }

    /** Mixed-cell grids keep every cell a usable photo shape (no slivers that slice faces). */
    @Test fun gridCellsAreNotSlivers() {
        for (style in listOf(SpreadStyle.FRAMES, SpreadStyle.PATCHWORK)) for ((w, h) in screens) for (n in style.minN..style.maxN) for (seed in 0 until 40) {
            placeSpread(style, n, w, h, seed).items.forEach { p ->
                val ar = maxOf(p.w / p.h, p.h / p.w)
                assertTrue("$style n=$n ${w}x$h seed=$seed aspect $ar", ar <= 3.2f)
            }
        }
    }

    /** Pure, so the same slide always renders the same way. */
    @Test fun deterministicPerSeed() {
        for (style in SpreadStyle.entries) assertEquals(placeSpread(style, 5, 960f, 540f, 7), placeSpread(style, 5, 960f, 540f, 7))
    }
}
