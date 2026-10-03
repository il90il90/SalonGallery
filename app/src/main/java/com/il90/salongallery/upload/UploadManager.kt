package com.il90.salongallery.upload

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.il90.salongallery.UploadService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * A process-wide upload queue, drained by [UploadService] (a foreground service), so a transfer keeps
 * running even when the app is closed or the phone's screen is off until every photo/clip is sent.
 *
 * The UI enqueues work and observes [progress] to show a count; the service keeps the process alive
 * with a progress notification and does the actual sending.
 */
object UploadManager {

    /** One queued file to send, with everything the sender needs. */
    data class Item(
        val host: String, val port: Int, val album: String?,
        val quality: UploadQuality, val uri: String, val isVideo: Boolean,
    )

    data class Progress(val done: Int, val total: Int, val running: Boolean)

    private val _progress = MutableStateFlow(Progress(0, 0, false))
    val progress: StateFlow<Progress> = _progress

    private val queue = ConcurrentLinkedQueue<Item>()
    private val total = AtomicInteger(0)
    private val done = AtomicInteger(0)
    @Volatile private var running = false

    /** Queue a batch of uris and make sure the foreground service is running to drain it. */
    @Synchronized
    fun enqueue(context: Context, host: String, port: Int, album: String?, quality: UploadQuality, uris: List<Uri>) {
        if (uris.isEmpty()) return
        // Start a fresh count only when nothing is in flight, so a batch added mid-transfer extends
        // the current progress instead of resetting it.
        if (!running && queue.isEmpty()) { total.set(0); done.set(0) }
        val cr = context.contentResolver
        uris.forEach { u ->
            val isVideo = runCatching { cr.getType(u)?.startsWith("video") == true }.getOrDefault(false)
            queue.add(Item(host, port, album, quality, u.toString(), isVideo))
        }
        total.addAndGet(uris.size)
        publish()
        runCatching { ContextCompat.startForegroundService(context.applicationContext, Intent(context.applicationContext, UploadService::class.java)) }
    }

    /** Service: take the next queued file, or null when the queue is empty. */
    fun poll(): Item? = queue.poll()

    fun onStarted() { running = true; publish() }
    fun onOneDone() { done.incrementAndGet(); publish() }

    /** Service: the queue drained. Keep the final counts visible (for the UI's "done" line) but mark
     *  it no longer running; the next [enqueue] resets the counters. */
    fun onFinished() { running = false; publish() }

    val isRunning get() = running

    private fun publish() { _progress.value = Progress(done.get(), total.get(), running) }
}
