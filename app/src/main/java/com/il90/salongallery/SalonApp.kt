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
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .allowHardware(true)
            .crossfade(180)
            .memoryCache { MemoryCache.Builder(this).maxSizePercent(0.35).build() }
            .build()
}
