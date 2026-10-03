package com.il90.salongallery.diag

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import java.io.File

/**
 * A tiny, dependency-free crash recorder. We install an uncaught-exception handler that writes the
 * stack trace (plus a snapshot of device + memory state, since most of our crashes are memory
 * related) to a file, then hands off to the platform's own handler so the process still dies
 * normally. On the next launch the app reads that file and shows it, so a crash at the salon — on a
 * screen nobody is watching — is no longer invisible: we can see exactly what failed and why.
 *
 * Everything here is wrapped so the recorder itself can never crash the app or get in the way of the
 * real crash: a failure to write the report just means no report, never a second exception.
 */
object CrashLog {

    private const val FILE = "last_crash.txt"

    /** Install once, from Application.onCreate. Safe to call more than once. */
    fun install(context: Context) {
        val app = context.applicationContext
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching { write(app, thread, throwable) }
            // Chain to whatever handler was there before (the platform one), so the OS still
            // reports/kills the process exactly as it would have.
            previous?.uncaughtException(thread, throwable)
        }
    }

    private fun write(context: Context, thread: Thread, throwable: Throwable) {
        val sw = java.io.StringWriter()
        throwable.printStackTrace(java.io.PrintWriter(sw))
        val report = buildString {
            append("SalonGallery crash\n")
            append("time: ").append(
                java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())
            ).append('\n')
            append("thread: ").append(thread.name).append('\n')
            append(deviceLine(context)).append('\n')
            append(memoryLine(context)).append('\n')
            append("\n")
            append(sw.toString())
        }
        runCatching { File(context.filesDir, FILE).writeText(report.take(16_000)) }
    }

    /** The saved crash report, or null if there was no crash since it was last cleared. */
    fun pending(context: Context): String? = runCatching {
        val f = File(context.applicationContext.filesDir, FILE)
        if (f.exists()) f.readText().ifBlank { null } else null
    }.getOrNull()

    /** Forget the saved crash (call once the user has seen it). */
    fun clear(context: Context) {
        runCatching { File(context.applicationContext.filesDir, FILE).delete() }
    }

    /** A one-line device descriptor for the report. */
    fun deviceLine(context: Context): String = runCatching {
        "device: ${Build.MANUFACTURER} ${Build.MODEL} · Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    }.getOrDefault("device: ?")

    /** A one-line snapshot of current memory, so a crash report shows how tight things were. */
    fun memoryLine(context: Context): String = runCatching {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val rt = Runtime.getRuntime()
        val mb = 1024L * 1024L
        val usedHeap = (rt.totalMemory() - rt.freeMemory()) / mb
        val maxHeap = rt.maxMemory() / mb
        "memory: heap ${usedHeap}/${maxHeap} MB · system avail ${mi.availMem / mb}/${mi.totalMem / mb} MB" +
            (if (mi.lowMemory) " · LOW" else "") + " · heapClass ${am.memoryClass} MB"
    }.getOrDefault("memory: ?")
}
