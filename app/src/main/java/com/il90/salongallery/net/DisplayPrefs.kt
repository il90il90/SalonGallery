package com.il90.salongallery.net

import android.content.Context
import java.util.Calendar

/**
 * Display-local settings kept in SharedPreferences (read synchronously on server &
 * UI threads): a custom device name, an optional PIN that gates the on-screen
 * settings, and an auto-sleep schedule that blanks the frame during quiet hours.
 */
class DisplayPrefs(context: Context) {
    private val sp = context.applicationContext.getSharedPreferences("salon_display", Context.MODE_PRIVATE)

    var customName: String
        get() = sp.getString("name", "") ?: ""
        set(v) = sp.edit().putString("name", v.trim()).apply()

    /** Empty = no PIN. */
    var pin: String
        get() = sp.getString("pin", "") ?: ""
        set(v) = sp.edit().putString("pin", v.trim()).apply()

    val hasPin: Boolean get() = pin.isNotEmpty()

    var scheduleEnabled: Boolean
        get() = sp.getBoolean("sched", false)
        set(v) = sp.edit().putBoolean("sched", v).apply()

    /** Minutes-of-day the screen goes to sleep (default 23:00). */
    var sleepStartMin: Int
        get() = sp.getInt("sleepStart", 23 * 60)
        set(v) = sp.edit().putInt("sleepStart", v).apply()

    /** Minutes-of-day the screen wakes (default 07:00). */
    var sleepEndMin: Int
        get() = sp.getInt("sleepEnd", 7 * 60)
        set(v) = sp.edit().putInt("sleepEnd", v).apply()

    var rssEnabled: Boolean
        get() = sp.getBoolean("rssOn", false)
        set(v) = sp.edit().putBoolean("rssOn", v).apply()

    /** RSS/Atom feed URLs to scroll along the bottom of the wall, one per line. */
    var rssFeeds: List<String>
        get() = (sp.getString("rssFeeds", "") ?: "").split("\n").map { it.trim() }.filter { it.isNotBlank() }
        set(v) = sp.edit().putString("rssFeeds", v.joinToString("\n")).apply()

    var rssPos: String
        get() = sp.getString("rssPos", "bottom") ?: "bottom"
        set(v) = sp.edit().putString("rssPos", v).apply()
    var rssShowImage: Boolean
        get() = sp.getBoolean("rssImg", false)
        set(v) = sp.edit().putBoolean("rssImg", v).apply()
    var rssShowSource: Boolean
        get() = sp.getBoolean("rssSrc", true)
        set(v) = sp.edit().putBoolean("rssSrc", v).apply()
    var rssShowSummary: Boolean
        get() = sp.getBoolean("rssSum", false)
        set(v) = sp.edit().putBoolean("rssSum", v).apply()

    // ---- On-screen overlays (clock / weather / text). Persisted so they survive a Display restart
    // and so the Remote can show their real current state instead of guessing. ----

    var clockOn: Boolean
        get() = sp.getBoolean("clkOn", false)
        set(v) = sp.edit().putBoolean("clkOn", v).apply()
    var clockPos: String
        get() = sp.getString("clkPos", "top_start") ?: "top_start"
        set(v) = sp.edit().putString("clkPos", v).apply()
    var clockDate: Boolean
        get() = sp.getBoolean("clkDate", true)
        set(v) = sp.edit().putBoolean("clkDate", v).apply()
    var clockStyle: String
        get() = sp.getString("clkStyle", "digital") ?: "digital"
        set(v) = sp.edit().putString("clkStyle", v).apply()
    var clockSize: String
        get() = sp.getString("clkSize", "m") ?: "m"
        set(v) = sp.edit().putString("clkSize", v).apply()

    var weatherOn: Boolean
        get() = sp.getBoolean("wxOn", false)
        set(v) = sp.edit().putBoolean("wxOn", v).apply()
    var weatherPlace: String
        get() = sp.getString("wxPlace", "") ?: ""
        set(v) = sp.edit().putString("wxPlace", v).apply()
    var weatherLat: Double
        get() = java.lang.Double.longBitsToDouble(sp.getLong("wxLat", 0L))
        set(v) = sp.edit().putLong("wxLat", java.lang.Double.doubleToRawLongBits(v)).apply()
    var weatherLon: Double
        get() = java.lang.Double.longBitsToDouble(sp.getLong("wxLon", 0L))
        set(v) = sp.edit().putLong("wxLon", java.lang.Double.doubleToRawLongBits(v)).apply()
    var weatherUnits: String
        get() = sp.getString("wxUnits", "c") ?: "c"
        set(v) = sp.edit().putString("wxUnits", v).apply()
    var weatherPos: String
        get() = sp.getString("wxPos", "top_end") ?: "top_end"
        set(v) = sp.edit().putString("wxPos", v).apply()

    var textContent: String
        get() = sp.getString("txtContent", "") ?: ""
        set(v) = sp.edit().putString("txtContent", v).apply()
    var textPos: String
        get() = sp.getString("txtPos", "bottom") ?: "bottom"
        set(v) = sp.edit().putString("txtPos", v).apply()
    var textSize: String
        get() = sp.getString("txtSize", "m") ?: "m"
        set(v) = sp.edit().putString("txtSize", v).apply()
    var textColor: String
        get() = sp.getString("txtColor", "white") ?: "white"
        set(v) = sp.edit().putString("txtColor", v).apply()
    var textFont: String
        get() = sp.getString("txtFont", "classic") ?: "classic"
        set(v) = sp.edit().putString("txtFont", v).apply()

    // ---- Slideshow look/behaviour, persisted so a Display power-cycle keeps the user's choices. ----
    var layout: String
        get() = sp.getString("layout", "single") ?: "single"
        set(v) = sp.edit().putString("layout", v).apply()
    var motion: String
        get() = sp.getString("motion", "off") ?: "off"
        set(v) = sp.edit().putString("motion", v).apply()
    var motionSpeed: String
        get() = sp.getString("mspeed", "medium") ?: "medium"
        set(v) = sp.edit().putString("mspeed", v).apply()
    var photoFit: String
        get() = sp.getString("fit", "fill") ?: "fill"
        set(v) = sp.edit().putString("fit", v).apply()
    var bgColor: String
        get() = sp.getString("bg", "black") ?: "black"
        set(v) = sp.edit().putString("bg", v).apply()
    var spreadMix: String
        get() = sp.getString("spreadmix", "always") ?: "always"
        set(v) = sp.edit().putString("spreadmix", v).apply()
    var spreadStagger: Boolean
        get() = sp.getBoolean("stagger", false)
        set(v) = sp.edit().putBoolean("stagger", v).apply()
    var slideShuffle: Boolean
        get() = sp.getBoolean("shuffle", false)
        set(v) = sp.edit().putBoolean("shuffle", v).apply()
    var slideInterval: Long
        get() = sp.getLong("interval", 30000L)
        set(v) = sp.edit().putLong("interval", v).apply()
    var collageOn: Boolean
        get() = sp.getBoolean("collage", false)
        set(v) = sp.edit().putBoolean("collage", v).apply()
    var orientation: String
        get() = sp.getString("orient", "auto") ?: "auto"
        set(v) = sp.edit().putString("orient", v).apply()

    /** True if, right now, the schedule says the frame should be asleep. */
    fun isSleepingNow(nowMinOfDay: Int = currentMinOfDay()): Boolean {
        if (!scheduleEnabled) return false
        val start = sleepStartMin
        val end = sleepEndMin
        if (start == end) return false
        return if (start < end) nowMinOfDay in start until end   // same-day window
        else nowMinOfDay >= start || nowMinOfDay < end            // overnight window
    }

    companion object {
        fun currentMinOfDay(): Int {
            val c = Calendar.getInstance()
            return c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE)
        }
        fun fmt(min: Int): String = "%02d:%02d".format((min / 60) % 24, min % 60)
    }
}
