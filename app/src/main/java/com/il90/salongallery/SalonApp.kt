package com.il90.salongallery

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.memory.MemoryCache

/**
 * App-wide Coil image loader.
 *
 * Big photos (e.g. a 4000×2252 phone shot) could show up as a **black frame** on some TVs: Coil
 * decodes to a hardware (GPU-texture) bitmap by default, and a device whose max GL texture size is
 * smaller than the image simply fails to upload the texture and draws nothing. Turning hardware
 * bitmaps off makes Coil hand Compose a normal software bitmap, which Skia can always draw (it tiles
 * past the GL limit) — so every photo renders, at every size, on every box. For a wall that shows a
 * handful of images at a time the extra memory is negligible, and a soft crossfade hides the decode.
 */
class SalonApp : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .allowHardware(false)
            .crossfade(200)
            .memoryCache { MemoryCache.Builder(this).maxSizePercent(0.30).build() }
            .build()
}
