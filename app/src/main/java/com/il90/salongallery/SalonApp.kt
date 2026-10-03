package com.il90.salongallery

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.memory.MemoryCache

/**
 * App-wide Coil image loader.
 *
 * Hardware (GPU-texture) bitmaps are kept on — they are light on memory and fast to draw. The old
 * black-frame problem on big photos (a bitmap larger than the device's max GL texture size fails to
 * upload and draws nothing) is handled instead by capping every slideshow decode to a safe size
 * (see the slideshow code), which also means each photo is decoded ONCE and reused everywhere — in a
 * collage cell and full-screen alike — so a spread never shows an empty cell while a photo decodes.
 */
class SalonApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        // Record any uncaught crash to a file so the NEXT launch can show what went wrong — a screen
        // at the salon crashes unattended, so this is the only way we learn why.
        com.il90.salongallery.diag.CrashLog.install(this)
    }

    // When the system reports memory pressure, release Coil's in-memory bitmaps right away. (Coil also
    // trims itself, but on a tight TV box this blunt, immediate clear is the safer belt-and-braces.)
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            runCatching { coil.Coil.imageLoader(this).memoryCache?.clear() }
        }
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .allowHardware(true)
            .crossfade(180)
            // Leaner in-memory cache (was 0.35) so a cheap TV box keeps headroom for the bitmaps a
            // multi-photo spread holds live while it is on screen; the disk cache still backs fast reuse.
            .memoryCache { MemoryCache.Builder(this).maxSizePercent(0.25).build() }
            .build()
}
