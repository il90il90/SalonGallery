package com.meylon.salongallery.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.view.WindowManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.meylon.salongallery.R
import com.meylon.salongallery.net.DisplayMode
import com.meylon.salongallery.net.PhotoFit
import com.meylon.salongallery.net.ScreenOrientation
import com.meylon.salongallery.net.SlideEffect
import com.meylon.salongallery.net.ScreenSessionHolder
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
    // The session lives in a process-wide holder so it survives backgrounding; the
    // foreground service (started from MainActivity) keeps the process alive.
    val session = remember { ScreenSessionHolder.getOrCreate(context, deviceName, actions.version) }
    val activity = remember(context) { context.findActivity() }

    val mode by session.mode.collectAsStateWithLifecycle()
    val libraryVersion by session.libraryVersion.collectAsStateWithLifecycle()
    val videoVersion by session.videoVersion.collectAsStateWithLifecycle()
    val frameId by session.frameId.collectAsStateWithLifecycle()
    val intervalMs by session.intervalMs.collectAsStateWithLifecycle()
    val shuffle by session.shuffle.collectAsStateWithLifecycle()
    val effect by session.effect.collectAsStateWithLifecycle()
    val photoFit by session.photoFit.collectAsStateWithLifecycle()
    val orientation by session.orientation.collectAsStateWithLifecycle()
    val brightness by session.brightness.collectAsStateWithLifecycle()
    val running by session.running.collectAsStateWithLifecycle()
    val currentIndex by session.currentIndex.collectAsStateWithLifecycle()

    var showSettings by remember { mutableStateOf(false) }

    // Keep the display awake permanently.
    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }

    // A single ExoPlayer, reused for whatever video is set.
    val exo = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }
    DisposableEffect(Unit) { onDispose { exo.release() } }
    LaunchedEffect(videoVersion, mode) {
        if (mode == DisplayMode.VIDEO && session.videoFile.exists()) {
            exo.setMediaItem(MediaItem.fromUri(Uri.fromFile(session.videoFile)))
            exo.prepare(); exo.play()
        } else {
            exo.pause()
        }
    }

    LaunchedEffect(brightness) {
        activity?.window?.let { w ->
            val lp = w.attributes
            lp.screenBrightness = if (brightness < 0f) WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            else brightness.coerceIn(0.02f, 1f)
            w.attributes = lp
        }
    }
    LaunchedEffect(orientation) {
        activity?.requestedOrientation = when (orientation) {
            ScreenOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            ScreenOrientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            ScreenOrientation.AUTO -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    // Pure fullscreen: hide the system bars entirely; only reveal them while the
    // settings sheet is open so it is comfortable to use.
    DisposableEffect(showSettings) {
        activity?.window?.let { w ->
            val c = WindowCompat.getInsetsController(w, view)
            c.systemBarsBehavior =
                androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (showSettings) c.show(WindowInsetsCompat.Type.systemBars())
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
            ) { showSettings = true },
    ) {
        when {
            mode == DisplayMode.VIDEO && session.videoFile.exists() ->
                FramedContent(frameId, Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exo
                                useController = false
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                setBackgroundColor(android.graphics.Color.BLACK)
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }

            mode == DisplayMode.SLIDESHOW && files.isNotEmpty() ->
                FramedContent(frameId, Modifier.fillMaxSize()) {
                    Slideshow(
                        files = files,
                        currentIndex = currentIndex,
                        intervalMs = intervalMs,
                        shuffle = shuffle,
                        effect = effect,
                        fit = photoFit,
                        onNext = { session.currentIndex.value = it },
                    )
                }

            else -> WaitingToPair(deviceName = deviceName, running = running)
        }
    }

    if (showSettings) {
        val w = remember { context.resources.displayMetrics.widthPixels }
        val h = remember { context.resources.displayMetrics.heightPixels }
        val stat = remember { runCatching { android.os.StatFs(context.filesDir.path) }.getOrNull() }
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
private fun Slideshow(
    files: List<File>,
    currentIndex: Int,
    intervalMs: Long,
    shuffle: Boolean,
    effect: SlideEffect,
    fit: PhotoFit,
    onNext: (Int) -> Unit,
) {
    if (files.isEmpty()) return
    val idx = currentIndex.coerceIn(0, files.size - 1)
    LaunchedEffect(idx, shuffle, intervalMs, files.size) {
        if (files.size <= 1) return@LaunchedEffect
        delay(intervalMs)
        val next = if (shuffle) (files.indices - idx).randomOrNull() ?: idx
        else (idx + 1) % files.size
        onNext(next)
    }
    AnimatedContent(
        targetState = idx,
        transitionSpec = {
            when (effect) {
                SlideEffect.NONE -> fadeIn(tween(1)) togetherWith fadeOut(tween(1))
                SlideEffect.SLIDE ->
                    (slideInHorizontally(tween(600)) { it } + fadeIn(tween(600))) togetherWith
                        (slideOutHorizontally(tween(600)) { -it } + fadeOut(tween(600)))
                SlideEffect.ZOOM ->
                    (scaleIn(tween(800), initialScale = 0.9f) + fadeIn(tween(800))) togetherWith
                        (scaleOut(tween(800), targetScale = 1.05f) + fadeOut(tween(800)))
                else -> fadeIn(tween(800)) togetherWith fadeOut(tween(800))
            }
        },
        label = "slide",
    ) { i ->
        val file = files[i.coerceIn(0, files.size - 1)]
        val scaleMod = if (effect == SlideEffect.KENBURNS) {
            val scale = remember(i) { Animatable(1f) }
            LaunchedEffect(i) { scale.animateTo(1.14f, tween(intervalMs.toInt(), easing = LinearEasing)) }
            Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value }
        } else Modifier
        PhotoContent(file, fit, scaleMod)
    }
}

@Composable
private fun PhotoContent(file: File, fit: PhotoFit, scaleMod: Modifier) {
    when (fit) {
        PhotoFit.FILL -> AsyncImage(
            model = file, contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().then(scaleMod),
        )
        PhotoFit.FIT -> AsyncImage(
            model = file, contentDescription = null, contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize().then(scaleMod),
        )
        PhotoFit.BLUR -> Box(Modifier.fillMaxSize()) {
            // Blurred, zoomed copy fills the background so there are no black bars…
            AsyncImage(
                model = file, contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().blur(28.dp).graphicsLayer { scaleX = 1.1f; scaleY = 1.1f },
            )
            // …with the whole photo shown, in focus, on top.
            AsyncImage(
                model = file, contentDescription = null, contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().then(scaleMod),
            )
        }
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
            Spacer(Modifier.height(10.dp))
            Text(
                stringResource(R.string.screen_tap_hint),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary, textAlign = TextAlign.Center,
            )
            Spacer(Modifier.weight(1f))
        }
    }
}
