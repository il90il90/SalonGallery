package com.il90.salongallery

import android.os.Handler
import android.os.Looper
import android.service.dreams.DreamService
import android.widget.ImageView
import coil.load
import com.il90.salongallery.net.ScreenSessionHolder
import com.il90.salongallery.net.isVideoName
import java.io.File

/**
 * Android screensaver (Daydream). When the device is idle, the system can launch this to
 * show the gallery's photos full-screen — turning any Android TV / tablet into a frame
 * without opening the app. Selected in Settings → Screen saver.
 */
class SalonDreamService : DreamService() {

    private val handler = Handler(Looper.getMainLooper())
    private var files: List<File> = emptyList()
    private var idx = 0
    private var intervalMs = 30_000L
    private var imageView: ImageView? = null

    private val advance = object : Runnable {
        override fun run() {
            val f = files.getOrNull(idx % files.size.coerceAtLeast(1))
            if (f != null) imageView?.load(f) { crossfade(700) }
            idx++
            handler.postDelayed(this, intervalMs)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isFullscreen = true
        isInteractive = false
        isScreenBright = true

        val iv = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundColor(android.graphics.Color.BLACK)
        }
        imageView = iv
        setContentView(iv)

        runCatching {
            val session = ScreenSessionHolder.getOrCreate(applicationContext, android.os.Build.MODEL ?: "Screen", BuildConfig.VERSION_NAME)
            // A simple, reliable photo slideshow (videos play in the app, not the screensaver).
            files = session.activeFiles().filter { !isVideoName(it.name) }
            intervalMs = session.intervalMs.value.coerceIn(5_000L, 3_600_000L)
        }
        handler.post(advance)
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacks(advance)
        imageView = null
        super.onDetachedFromWindow()
    }
}
