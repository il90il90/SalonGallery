package com.meylon.salongallery.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.WindowManager
import android.widget.VideoView
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.meylon.salongallery.R
import com.meylon.salongallery.net.DisplayMode
import com.meylon.salongallery.net.ScreenOrientation
import com.meylon.salongallery.net.ScreenSession
import com.meylon.salongallery.ui.components.LogoChip
import com.meylon.salongallery.ui.components.SalonBackground
import com.meylon.salongallery.ui.components.SectionLabel
import com.meylon.salongallery.ui.theme.ElecBorder
import com.meylon.salongallery.ui.theme.NeonCyan
import com.meylon.salongallery.ui.theme.TextPrimary
import com.meylon.salongallery.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.io.File

private fun Context.findActivity(): Activity? {
    var c: Context? = this
    while (c is ContextWrapper) { if (c is Activity) return c; c = c.baseContext }
    return null
}

@Composable
fun ScreenModeScreen(actions: AppActions) {
    val context = LocalContext.current
    val view = LocalView.current
    val deviceName = remember { Build.MODEL ?: "Salon Screen" }
    val session = remember { ScreenSession(context, deviceName, actions.version) }
    val activity = remember(context) { context.findActivity() }

    DisposableEffect(Unit) {
        session.start()
        onDispose { session.stop() }
    }

    val mode by session.mode.collectAsStateWithLifecycle()
    val libraryVersion by session.libraryVersion.collectAsStateWithLifecycle()
    val videoVersion by session.videoVersion.collectAsStateWithLifecycle()
    val frameId by session.frameId.collectAsStateWithLifecycle()
    val intervalMs by session.intervalMs.collectAsStateWithLifecycle()
    val shuffle by session.shuffle.collectAsStateWithLifecycle()
    val orientation by session.orientation.collectAsStateWithLifecycle()
    val brightness by session.brightness.collectAsStateWithLifecycle()
    val running by session.running.collectAsStateWithLifecycle()

    var showSettings by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(false) }
    val chromeVisible = showControls || mode == DisplayMode.WAITING

    // Apply brightness to the window.
    LaunchedEffect(brightness) {
        activity?.window?.let { w ->
            val lp = w.attributes
            lp.screenBrightness = if (brightness < 0f) WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            else brightness.coerceIn(0.02f, 1f)
            w.attributes = lp
        }
    }
    // Apply orientation.
    LaunchedEffect(orientation) {
        activity?.requestedOrientation = when (orientation) {
            ScreenOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            ScreenOrientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            ScreenOrientation.AUTO -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    // Immersive: hide system bars unless controls are showing.
    DisposableEffect(chromeVisible) {
        activity?.window?.let { w ->
            val c = WindowCompat.getInsetsController(w, view)
            if (chromeVisible) c.show(WindowInsetsCompat.Type.systemBars())
            else c.hide(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {}
    }

    val files = remember(libraryVersion) { session.library.list() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { showControls = !showControls },
    ) {
        when {
            mode == DisplayMode.VIDEO && session.videoFile.exists() ->
                FramedContent(frameId, Modifier.fillMaxSize()) {
                    key(videoVersion) {
                        AndroidView(
                            factory = { ctx ->
                                VideoView(ctx).apply { setOnPreparedListener { mp -> mp.isLooping = true; start() } }
                            },
                            update = { vv -> vv.setVideoPath(session.videoFile.absolutePath); vv.start() },
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

            mode == DisplayMode.SLIDESHOW && files.isNotEmpty() ->
                FramedContent(frameId, Modifier.fillMaxSize()) {
                    Slideshow(files = files, version = libraryVersion, intervalMs = intervalMs, shuffle = shuffle)
                }

            else -> WaitingToPair(deviceName = deviceName, running = running)
        }

        // Top gear, visible only when chrome is showing.
        if (chromeVisible) {
            Box(Modifier.fillMaxWidth().safeDrawingPadding().padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(42.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0x66000000))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { showSettings = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.Tv, contentDescription = "Settings",
                        tint = Color.White, modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }

    if (showSettings) {
        val w = remember { context.resources.displayMetrics.widthPixels }
        val h = remember { context.resources.displayMetrics.heightPixels }
        val stat = remember {
            runCatching { android.os.StatFs(context.filesDir.path) }.getOrNull()
        }
        SettingsSheet(
            actions = actions,
            onDismiss = { showSettings = false },
            extra = {
                Spacer(Modifier.height(16.dp))
                ScreenInfoContent(
                    widthPx = w, heightPx = h,
                    freeBytes = stat?.availableBytes ?: 0L,
                    totalBytes = stat?.totalBytes ?: 0L,
                    photoCount = files.size,
                )
            },
        )
    }
}

@Composable
private fun Slideshow(files: List<File>, version: Long, intervalMs: Long, shuffle: Boolean) {
    if (files.isEmpty()) return
    var idx by remember(version, shuffle) { mutableIntStateOf(0) }
    val order = remember(version, shuffle, files.size) {
        if (shuffle) files.indices.shuffled() else files.indices.toList()
    }
    LaunchedEffect(version, shuffle, intervalMs, files.size) {
        if (files.size <= 1) return@LaunchedEffect
        while (true) {
            delay(intervalMs)
            idx = (idx + 1) % order.size
        }
    }
    val file = files[order[idx % order.size]]
    Crossfade(targetState = file, animationSpec = tween(700), label = "slide") { f ->
        AsyncImage(
            model = f,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun WaitingToPair(deviceName: String, running: Boolean) {
    SalonBackground {
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(1f))
            LogoChip(size = 92, icon = Icons.Outlined.Tv)
            Spacer(Modifier.height(20.dp))
            SectionLabel(stringResource(R.string.screen_overline))
            Spacer(Modifier.height(10.dp))
            Text(
                stringResource(R.string.screen_ready_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary, textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(deviceName, style = MaterialTheme.typography.titleLarge, color = NeonCyan, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.screen_ready_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary, textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.clip(RoundedCornerShape(50)).background(Color(0xFF14141F))
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Box(Modifier.size(7.dp).clip(RoundedCornerShape(50)).background(if (running) Color(0xFF34D399) else ElecBorder))
                Text(
                    stringResource(if (running) R.string.screen_advertising else R.string.screen_starting),
                    style = MaterialTheme.typography.labelMedium, color = TextSecondary,
                )
            }
            Spacer(Modifier.weight(1f))
        }
    }
}
