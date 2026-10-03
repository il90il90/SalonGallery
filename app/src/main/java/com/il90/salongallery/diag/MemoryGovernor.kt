package com.il90.salongallery.diag

import android.app.ActivityManager
import android.content.Context

/**
 * Decides, live, how many photos a single spread may hold — instead of a fixed cap. It weighs three
 * things: the device's total RAM (its class), how much memory is free *right now*, and the size one
 * decoded photo costs at the slideshow's decode resolution. A roomy device showing on a big wall can
 * fan out many photos; a cheap TV box, or any device under pressure, is pulled back automatically so
 * the slideshow keeps running instead of crashing.
 *
 * It is deliberately conservative. Bitmaps are decoded as hardware (GPU) textures, which do not show
 * up in the Java heap, so we never trust free heap alone — the device RAM class is the real ceiling
 * and live pressure only ever tightens it, never loosens it past that ceiling.
 */
object MemoryGovernor {

    /**
     * @param maxSpreadPhotos how many photos one spread may decode/show at once (>= 4)
     * @param pressure 0f (plenty free) .. 1f (almost none) — the max of heap use, system use, low-mem
     */
    data class Budget(val maxSpreadPhotos: Int, val pressure: Float)

    /** Absolute guard rails, whatever the arithmetic says. */
    private const val FLOOR = 4
    private const val CEILING = 24

    /**
     * @param decodePx the longest-edge pixel size each photo is decoded to (from the slideshow)
     */
    fun assess(context: Context, decodePx: Int): Budget = runCatching {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val rt = Runtime.getRuntime()

        val heapUsedFrac = (rt.totalMemory() - rt.freeMemory()).toFloat() / rt.maxMemory().coerceAtLeast(1L)
        val sysUsedFrac = if (mi.totalMem > 0) 1f - mi.availMem.toFloat() / mi.totalMem else 0.5f
        val pressure = maxOf(heapUsedFrac, sysUsedFrac, if (mi.lowMemory) 0.95f else 0f).coerceIn(0f, 1f)

        // Ceiling from the device's RAM tier — the real upper bound for hardware-bitmap textures.
        val totalGb = mi.totalMem / (1024.0 * 1024.0 * 1024.0)
        val tierMax = when {
            totalGb < 1.0 -> 6
            totalGb < 1.5 -> 8
            totalGb < 2.0 -> 10
            totalGb < 3.0 -> 12
            totalGb < 4.0 -> 16
            else -> CEILING
        }

        // How big a slice of the *currently free* system RAM we let one live spread occupy. Smaller
        // as pressure climbs — this is the "if 80% is used, behave differently" tiering, made smooth.
        val frac = when {
            pressure >= 0.90f -> 0.045f
            pressure >= 0.80f -> 0.07f
            pressure >= 0.70f -> 0.10f
            else -> 0.14f
        }
        // Worst-case cost of one decoded photo (a square ARGB_8888 bitmap at the decode size).
        val bytesPerPhoto = decodePx.toLong() * decodePx.toLong() * 4L
        val fromFree = if (bytesPerPhoto > 0L) (mi.availMem * frac / bytesPerPhoto).toInt() else tierMax

        val maxPhotos = minOf(tierMax, fromFree).coerceIn(FLOOR, CEILING)
        Budget(maxPhotos, pressure)
    }.getOrDefault(Budget(FLOOR, 0.5f))
}
