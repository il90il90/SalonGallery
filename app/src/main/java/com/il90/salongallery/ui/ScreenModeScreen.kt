package com.il90.salongallery.ui

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
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.focusable
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.Lan
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.TextFieldColors
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.il90.salongallery.R
import com.il90.salongallery.net.DisplayMode
import com.il90.salongallery.net.isVideoName
import com.il90.salongallery.net.PhotoFit
import com.il90.salongallery.net.PhotoFilter
import com.il90.salongallery.net.PhotoTransform
import com.il90.salongallery.net.LayoutMode
import com.il90.salongallery.net.MotionMode
import com.il90.salongallery.net.NowSlide
import com.il90.salongallery.net.SpreadMix
import com.il90.salongallery.net.PhotoFocus
import com.il90.salongallery.net.MotionSpeed
import com.il90.salongallery.net.ScreenOrientation
import com.il90.salongallery.net.SlideEffect
import com.il90.salongallery.net.ScreenSessionHolder
import com.il90.salongallery.net.TextOverlay
import com.il90.salongallery.net.TextPos
import com.il90.salongallery.ui.components.GradientButton
import com.il90.salongallery.ui.components.LogoChip
import com.il90.salongallery.ui.components.OutlineButton
import com.il90.salongallery.ui.components.SalonBackground
import com.il90.salongallery.ui.components.SectionLabel
import com.il90.salongallery.net.ScreenSession
import com.il90.salongallery.net.DisplayPrefs
import com.il90.salongallery.ui.theme.ElecBorder
import com.il90.salongallery.ui.theme.ElecBg
import com.il90.salongallery.ui.theme.ElecSurface
import com.il90.salongallery.ui.theme.NeonCyan
import com.il90.salongallery.ui.theme.TextPrimary
import com.il90.salongallery.ui.theme.TextSecondary
import com.il90.salongallery.ui.theme.TextTertiary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File

private fun Context.findActivity(): Activity? {
    var c: Context? = this
    while (c is ContextWrapper) { if (c is Activity) return c; c = c.baseContext }
    return null
}

@androidx.annotation.OptIn(UnstableApi::class)
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
    val frameRandom by session.frameRandom.collectAsStateWithLifecycle()
    val framePool by session.framePool.collectAsStateWithLifecycle()
    val frameWidth by session.frameWidth.collectAsStateWithLifecycle()
    val intervalMs by session.intervalMs.collectAsStateWithLifecycle()
    val shuffle by session.shuffle.collectAsStateWithLifecycle()
    val effect by session.effect.collectAsStateWithLifecycle()
    val effectPool by session.effectPool.collectAsStateWithLifecycle()
    val filterPoolNames by session.filterPool.collectAsStateWithLifecycle()
    val filterPool = remember(filterPoolNames) { filterPoolNames.map { PhotoFilter.from(it) } }
    val photoFit by session.photoFit.collectAsStateWithLifecycle()
    val bgColor by session.bgColor.collectAsStateWithLifecycle()
    val photoFilter by session.photoFilter.collectAsStateWithLifecycle()
    val collage by session.collage.collectAsStateWithLifecycle()
    val layout by session.layout.collectAsStateWithLifecycle()
    val layoutPool by session.layoutPool.collectAsStateWithLifecycle()
    val motion by session.motion.collectAsStateWithLifecycle()
    val spreadStagger by session.spreadStagger.collectAsStateWithLifecycle()
    val smartGroup by session.smartGroup.collectAsStateWithLifecycle()
    val spreadMix by session.spreadMix.collectAsStateWithLifecycle()
    val motionSpeed by session.motionSpeed.collectAsStateWithLifecycle()
    val textOverlay by session.textOverlay.collectAsStateWithLifecycle()
    val clock by session.clock.collectAsStateWithLifecycle()
    val weather by session.weather.collectAsStateWithLifecycle()
    val weatherNow by session.weatherNow.collectAsStateWithLifecycle()
    val receiving by session.receiving.collectAsStateWithLifecycle()
    val rssOn by session.rssOn.collectAsStateWithLifecycle()
    val rssFeeds by session.rssFeeds.collectAsStateWithLifecycle()
    val rssConfig by session.rssConfig.collectAsStateWithLifecycle()
    val orientation by session.orientation.collectAsStateWithLifecycle()
    val brightness by session.brightness.collectAsStateWithLifecycle()
    val volume by session.volume.collectAsStateWithLifecycle()
    val running by session.running.collectAsStateWithLifecycle()
    val currentIndex by session.currentIndex.collectAsStateWithLifecycle()
    val musicVersion by session.musicVersion.collectAsStateWithLifecycle()
    val musicPlaying by session.musicPlaying.collectAsStateWithLifecycle()
    val musicShuffle by session.musicShuffle.collectAsStateWithLifecycle()
    val musicNext by session.musicNextTrigger.collectAsStateWithLifecycle()
    val musicPrev by session.musicPrevTrigger.collectAsStateWithLifecycle()
    val screensaverReq by session.screensaverTrigger.collectAsStateWithLifecycle()
    val resetRoleReq by session.resetRoleTrigger.collectAsStateWithLifecycle()

    // Android TV: keep D-pad focus on the display so the remote's arrows flip photos.
    val rootFocus = remember { androidx.compose.ui.focus.FocusRequester() }
    LaunchedEffect(Unit) { runCatching { rootFocus.requestFocus() } }

    // Auto-sleep schedule + content schedule (business hours): re-evaluate every 30s.
    var sleeping by remember { mutableStateOf(session.prefs.isSleepingNow()) }
    LaunchedEffect(Unit) {
        session.applyContentSchedule()
        while (true) {
            sleeping = session.prefs.isSleepingNow()
            session.applyContentSchedule()   // switch album when the open/closed window changes
            delay(30_000)
        }
    }

    // Keep the display awake permanently.
    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }

    // A dedicated ExoPlayer for the background-music playlist, driven by the session.
    val musicExo = remember {
        ExoPlayer.Builder(context).build().apply { repeatMode = Player.REPEAT_MODE_ALL }
    }
    DisposableEffect(Unit) {
        val l = object : Player.Listener {
            override fun onMediaItemTransition(item: MediaItem?, reason: Int) {
                session.musicIndex.value = musicExo.currentMediaItemIndex
            }
        }
        musicExo.addListener(l)
        onDispose { musicExo.removeListener(l); musicExo.release() }
    }
    LaunchedEffect(musicVersion) {
        val tracks = session.music.list()
        if (tracks.isEmpty()) {
            musicExo.clearMediaItems()
        } else {
            val keepIndex = musicExo.currentMediaItemIndex.coerceIn(0, tracks.size - 1)
            val wasEmpty = musicExo.mediaItemCount == 0
            musicExo.setMediaItems(tracks.map { MediaItem.fromUri(Uri.fromFile(it)) })
            musicExo.prepare()
            if (!wasEmpty) musicExo.seekTo(keepIndex, 0)
        }
    }
    LaunchedEffect(musicPlaying, sleeping) { musicExo.playWhenReady = musicPlaying && !sleeping }
    // Volume from the remote → apply straight to the players (reliable on Android TV).
    LaunchedEffect(volume) { musicExo.volume = volume }
    LaunchedEffect(musicShuffle) { musicExo.shuffleModeEnabled = musicShuffle }
    LaunchedEffect(musicNext) { if (musicNext > 0 && musicExo.mediaItemCount > 0) musicExo.seekToNext() }
    LaunchedEffect(musicPrev) { if (musicPrev > 0 && musicExo.mediaItemCount > 0) musicExo.seekToPrevious() }
    // The Remote asked this screen to open Android's screensaver settings.
    LaunchedEffect(screensaverReq) {
        if (screensaverReq > 0) openScreensaverSettings(context)
    }
    // The Remote asked this screen to drop back to role selection.
    LaunchedEffect(resetRoleReq) {
        if (resetRoleReq > 0) actions.onChangeRole()
    }

    // Brightness is applied as a software dimming scrim (see below) so it works like real
    // picture brightness on every device, including Android TV where window brightness is ignored.
    LaunchedEffect(orientation) {
        activity?.requestedOrientation = when (orientation) {
            ScreenOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            ScreenOrientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            ScreenOrientation.AUTO -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    // Pure fullscreen: hide the system bars entirely. The screen shows nothing but the
    // gallery — all control happens from the Remote.
    DisposableEffect(Unit) {
        activity?.window?.let { w ->
            val c = WindowCompat.getInsetsController(w, view)
            c.systemBarsBehavior =
                androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            c.hide(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {}
    }

    // RSS headlines: refetch on change, then every 5 minutes.
    var rssItems by remember { mutableStateOf<List<com.il90.salongallery.net.RssItem>>(emptyList()) }
    LaunchedEffect(rssOn, rssFeeds) {
        if (!rssOn || rssFeeds.isEmpty()) { rssItems = emptyList(); return@LaunchedEffect }
        while (true) {
            rssItems = com.il90.salongallery.net.RssFeed.fetch(rssFeeds)
            delay(5 * 60 * 1000L)
        }
    }

    val files = remember(libraryVersion) { session.activeFiles() }

    // Resolve the active frame (random shuffles per photo) and, for the Adaptive frame,
    // derive a molding colour from the current photo.
    val activeFrameId = remember(frameRandom, frameId, framePool, currentIndex) {
        if (frameRandom && framePool.isNotEmpty())
            framePool[kotlin.random.Random(currentIndex.toLong()).nextInt(framePool.size)]
        else frameId
    }
    var adaptiveColor by remember { mutableStateOf<Color?>(null) }
    LaunchedEffect(activeFrameId, currentIndex, libraryVersion) {
        if (!frameById(activeFrameId).adaptive) { adaptiveColor = null; return@LaunchedEffect }
        val f = files.getOrNull(currentIndex.coerceIn(0, maxOf(0, files.size - 1)))
        adaptiveColor = if (f != null && !isVideoName(f.name))
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { dominantColor(f) } else null
    }

    // "Auto" background: a muted, darkened wash of the current photo's dominant colour, so the bars
    // around a photo that doesn't fill the screen blend into it. Recomputed per slide; black fallback.
    var autoBg by remember { mutableStateOf<Color?>(null) }
    LaunchedEffect(bgColor, currentIndex, libraryVersion) {
        if (bgColor != "auto") { autoBg = null; return@LaunchedEffect }
        val f = files.getOrNull(currentIndex.coerceIn(0, maxOf(0, files.size - 1)))
        autoBg = if (f != null && !isVideoName(f.name))
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { dominantColor(f)?.let { muteDark(it) } } else null
    }
    val slideBg = if (bgColor == "auto") (autoBg ?: Color.Black) else bgColorOf(bgColor)

    // Orientation map (name -> isPortrait) for the auto-collage, decoded off the main thread.
    var orientationMap by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    LaunchedEffect(libraryVersion, collage) {
        if (!collage) return@LaunchedEffect
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val m = HashMap(orientationMap)
            files.forEach { f ->
                if (!isVideoName(f.name) && !m.containsKey(f.name)) {
                    runCatching {
                        val b = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        android.graphics.BitmapFactory.decodeFile(f.path, b)
                        if (b.outWidth > 0 && b.outHeight > 0) m[f.name] = b.outHeight > b.outWidth
                    }
                }
            }
            orientationMap = m
        }
    }

    // Flip photos with the TV remote's left/right arrows (and wake from sleep on any press/tap).
    fun step(delta: Int) {
        val n = files.size
        if (n > 1) session.currentIndex.value = ((currentIndex + delta) % n + n) % n
    }
    // Up/Down browse frames on the screen (local preview only — not saved). Shows the frame name.
    var browseFrame by remember { mutableStateOf<Int?>(null) }
    var browseTick by remember { mutableStateOf(0L) }
    // The Remote taking control of the frame clears the local browse override.
    LaunchedEffect(frameId, frameRandom) { browseFrame = null }
    fun browse(delta: Int) {
        val ids = FRAMES.map { it.id }
        val cur = ids.indexOf(browseFrame ?: activeFrameId).coerceAtLeast(0)
        browseFrame = ids[((cur + delta) % ids.size + ids.size) % ids.size]
        browseTick = System.currentTimeMillis()
    }
    val shownFrame = browseFrame ?: activeFrameId
    var showBrowseLabel by remember { mutableStateOf(false) }
    LaunchedEffect(browseTick) {
        if (browseTick > 0L) { showBrowseLabel = true; delay(3000); showBrowseLabel = false }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(slideBg)
            .focusRequester(rootFocus)
            .focusable()
            .onKeyEvent { ev ->
                if (ev.type != KeyEventType.KeyDown) return@onKeyEvent false
                if (sleeping) { sleeping = false; return@onKeyEvent true }
                when (ev.key) {
                    Key.DirectionLeft, Key.MediaPrevious, Key.MediaRewind -> { step(-1); true }
                    Key.DirectionRight, Key.MediaNext, Key.MediaFastForward -> { step(1); true }
                    Key.DirectionUp -> { browse(-1); true }
                    Key.DirectionDown -> { browse(1); true }
                    else -> false
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { if (sleeping) sleeping = false },
    ) {
        when {
            mode != DisplayMode.WAITING && files.isNotEmpty() ->
                FramedContent(shownFrame, frameWidth, adaptiveColor, Modifier.fillMaxSize()) {
                    Slideshow(
                        files = files,
                        currentIndex = currentIndex,
                        intervalMs = intervalMs,
                        shuffle = shuffle,
                        effect = effect,
                        effectPool = effectPool,
                        filterPool = filterPool,
                        fit = photoFit,
                        bg = slideBg,
                        filter = photoFilter,
                        volume = volume,
                        collageOn = collage,
                        layout = layout,
                        layoutPool = layoutPool,
                        motion = motion,
                        stagger = spreadStagger,
                        smartGroup = smartGroup,
                        spreadMix = spreadMix,
                        motionSpeed = motionSpeed,
                        orientationMap = orientationMap,
                        transformOf = { session.transformFor(it.name) },
                        durationOf = { session.durationFor(it.name) },
                        focusOf = { session.focusFor(it.name) },
                        onNext = { session.currentIndex.value = it },
                        reportSlide = { style, names, seed -> session.nowSlide.value = NowSlide(style, names, seed) },
                    )
                }

            else -> {
                // Re-read periodically so the Wi-Fi name appears once the location permission is granted.
                var net by remember { mutableStateOf(networkInfo(context)) }
                LaunchedEffect(running) { while (true) { net = networkInfo(context); delay(3000) } }
                WaitingToPair(
                    deviceName = session.effectiveName(),
                    running = running,
                    ssid = net.first,
                    address = net.second,
                    version = actions.version,
                    availableVersion = actions.availableVersion,
                    checking = actions.isChecking,
                    onCheckUpdate = actions.onCheckUpdate,
                    onChangeRole = actions.onChangeRole,
                )
            }
        }

        // Frame name while browsing frames with the TV remote's up/down (top-right).
        androidx.compose.animation.AnimatedVisibility(
            visible = browseFrame != null && showBrowseLabel,
            enter = androidx.compose.animation.fadeIn(), exit = androidx.compose.animation.fadeOut(),
            modifier = Modifier.align(androidx.compose.ui.AbsoluteAlignment.TopRight).safeDrawingPadding().padding(22.dp),
        ) {
            Row(
                Modifier.clip(RoundedCornerShape(50)).background(Color.Black.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Outlined.Image, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(16.dp))
                Text(
                    frameById(browseFrame ?: 0).name,
                    style = TextStyle(fontFamily = com.il90.salongallery.ui.theme.ContentFont, fontSize = 18.sp, color = Color.White),
                )
            }
        }

        if (mode != DisplayMode.WAITING) OverlayLayer(textOverlay, clock, weather, weatherNow)

        if (mode != DisplayMode.WAITING && rssOn && rssItems.isNotEmpty()) {
            RssTicker(rssItems, rssConfig)
        }

        // While a batch of photos is streaming in, cover the decoding gaps with a warm "Receiving…"
        // screen instead of letting the wall flash black (which looks like the TV is off).
        androidx.compose.animation.AnimatedVisibility(
            visible = receiving, enter = androidx.compose.animation.fadeIn(), exit = androidx.compose.animation.fadeOut(),
        ) { ReceivingOverlay(count = files.size) }

        // Brightness as real "picture" dimming — a software scrim that works on every
        // device (including Android TV, where window brightness is ignored).
        if (brightness in 0f..0.999f) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = (1f - brightness) * 0.82f)))
        }

        // Auto-sleep: cover everything in near-black with a faint clock; tap wakes it.
        if (sleeping) {
            Box(
                Modifier.fillMaxSize().background(Color.Black).clickable(
                    interactionSource = remember { MutableInteractionSource() }, indication = null,
                ) { sleeping = false },
                contentAlignment = Alignment.Center,
            ) {
                Box(Modifier.graphicsLayer { alpha = 0.32f }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ClockView(
                            style = clock.style,
                            modifier = Modifier,
                            showDate = clock.showDate,
                            alignEnd = false,
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            stringResource(R.string.sleep_tap_wake),
                            style = TextStyle(fontSize = 14.sp, color = Color.White),
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun Slideshow(
    files: List<File>,
    currentIndex: Int,
    intervalMs: Long,
    shuffle: Boolean,
    effect: SlideEffect,
    effectPool: List<String>,
    filterPool: List<PhotoFilter> = emptyList(),
    fit: PhotoFit,
    bg: Color = Color.Black,
    filter: PhotoFilter,
    volume: Float,
    collageOn: Boolean,
    layout: LayoutMode = LayoutMode.SINGLE,
    layoutPool: List<String> = emptyList(),
    motion: MotionMode = MotionMode.OFF,
    stagger: Boolean = false,
    smartGroup: Boolean = false,
    spreadMix: SpreadMix = SpreadMix.ALWAYS,
    motionSpeed: MotionSpeed = MotionSpeed.MEDIUM,
    orientationMap: Map<String, Boolean>,
    transformOf: (File) -> PhotoTransform,
    durationOf: (File) -> Int,
    focusOf: (File) -> PhotoFocus? = { null },
    onNext: (Int) -> Unit,
    reportSlide: (String, List<String>, Int) -> Unit = { _, _, _ -> },
) {
    if (files.isEmpty()) return
    val idx = currentIndex.coerceIn(0, files.size - 1)
    val currentIsVideo = isVideoName(files[idx].name)
    val screenLandscape = androidx.compose.ui.platform.LocalConfiguration.current.let { it.screenWidthDp >= it.screenHeightDp }

    // Smart grouping: a background-built cache of each photo's dominant colour (packed ARGB). While it
    // fills, grouping falls back to sequential; once ready, spreads gather similar-coloured photos.
    val colorCache = remember(files) { java.util.concurrent.ConcurrentHashMap<String, Int>() }
    var colorsReady by remember(files, smartGroup) { mutableStateOf(false) }
    LaunchedEffect(files, smartGroup) {
        colorsReady = false
        if (!smartGroup) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            files.forEach { f ->
                if (!isVideoName(f.name)) colorCache.getOrPut(f.name) { dominantColor(f)?.toArgb() ?: 0 }
            }
        }
        colorsReady = true
    }

    // No-repeat playback order: a permutation of every item that the slideshow walks strictly in
    // order, so every photo appears once before any repeats — this is what stops "the same photos I
    // started with keep coming back". Shuffle → a random permutation; Smart grouping → clustered by
    // colour (so a spread of consecutive entries is colour-matched AND still never repeats within a
    // cycle); otherwise the library's own order. Rebuilt only when one of these inputs changes.
    val playOrder = remember(files, shuffle, smartGroup, colorsReady) {
        val base = files.indices.toMutableList()
        when {
            smartGroup && colorsReady -> base.sortedBy { colorCache[files[it].name] ?: 0 }
            shuffle -> base.shuffled()
            else -> base.toList()
        }
    }
    val posOf = remember(playOrder) {
        HashMap<Int, Int>(playOrder.size * 2).apply { playOrder.forEachIndexed { p, i -> put(i, p) } }
    }

    // Which items (by index) form the slide starting at [from]: a collage of same-orientation
    // photos that would otherwise leave big side gaps, or just the single item.
    // Which spread (if any) the slide starting at [from] is: fixed by the layout setting, or for
    // RANDOM a seeded per-slide draw (so a given slide always renders the same way) over every
    // spread layout. [spreadMix] decides how often a slide is a spread at all — the others show one
    // photo. A layout that needs more photos than there are falls back to a Grid. Needs 4+ stills.
    val stillCount = remember(files) { files.count { !isVideoName(it.name) } }

    // The spread layouts "Mix" (RANDOM) draws from — the user's chosen subset, or every spread when
    // the pool is empty / none of the chosen names are valid spreads.
    val randomSpreads = remember(layoutPool) {
        layoutPool.map { LayoutMode.from(it) }.filter { it.isSpread }.distinct()
            .ifEmpty { LayoutMode.SPREADS }
    }

    // Smart, live memory budget: how many photos a spread may hold RIGHT NOW — derived from the
    // device's RAM, how much is free this moment, and the cost of one photo at the decode size, rather
    // than a fixed number. Re-checked on a slow ticker so it adapts as memory frees up or tightens,
    // without churning every frame (which would make a spread's photo count flicker mid-slide).
    val memCtx = androidx.compose.ui.platform.LocalContext.current
    var spreadBudget by remember {
        mutableStateOf(com.il90.salongallery.diag.MemoryGovernor.assess(memCtx, slidePx(memCtx)))
    }
    LaunchedEffect(files) {
        while (true) {
            spreadBudget = com.il90.salongallery.diag.MemoryGovernor.assess(memCtx, slidePx(memCtx))
            delay(3000)
        }
    }

    // Proactive memory hygiene: don't wait for the device to be critical — start freeing early. Every
    // 10s we read the pressure and, once it crosses ~70% (climbing toward trouble), drop Coil's idle
    // in-memory cache so bitmaps from past/warmed slides are released and usage shrinks back down; the
    // next slides simply decode fresh again. The slide ON screen is held by its composables, not only
    // the cache, so it never blanks — only off-screen extras go. The throttle scales with pressure:
    // the tighter things are, the sooner we're allowed to clear again (but never so often we thrash).
    LaunchedEffect(files) {
        var lastTrim = 0L
        while (true) {
            delay(10_000)
            val pressure = com.il90.salongallery.diag.MemoryGovernor.assess(memCtx, slidePx(memCtx)).pressure
            val now = System.currentTimeMillis()
            val minGapMs = when {
                pressure >= 0.85f -> 20_000L   // very tight: clear as often as every 20s
                pressure >= 0.78f -> 30_000L
                pressure >= 0.70f -> 45_000L   // just over the line: gentle, infrequent clears
                else -> Long.MAX_VALUE          // plenty of headroom: leave the cache alone
            }
            if (now - lastTrim >= minGapMs) {
                runCatching { coil.Coil.imageLoader(memCtx).memoryCache?.clear() }
                lastTrim = now
            }
        }
    }

    fun rangeOf(m: LayoutMode): IntRange = when (m) {
        LayoutMode.MOSAIC, LayoutMode.SCATTER -> 4..5
        // Prefer at least 4 photos, but never exceed the style's own max — Triptych is exactly 3, so
        // maxOf(minN,4)=4 would make an EMPTY 4..3 range and crash Random.nextInt. Clamp the low end
        // to maxN so the range is always valid.
        else -> SpreadStyle.valueOf(m.name).let { minOf(maxOf(it.minN, 4), it.maxN)..it.maxN }
    }
    // Settle on a spread we can actually fill: if the library is too small OR the live memory budget
    // is below the layout's minimum photo count, drop to a light Grid (min 4) instead of rendering a
    // heavy layout (e.g. a 8–12 cell patchwork) with empty cells or under memory strain.
    fun resolveSpread(m: LayoutMode): LayoutMode {
        val need = rangeOf(m).first
        return if (stillCount < need || spreadBudget.maxSpreadPhotos < need) LayoutMode.GRID else m
    }
    fun spreadAt(from: Int): LayoutMode? {
        if (stillCount < 4 || layout == LayoutMode.SINGLE) return null
        val rr = kotlin.random.Random(from.toLong() * 7919 + 17)
        // AUTO adapts to the photo on screen: a shot that already fills the wall is usually shown
        // alone (with the odd decorative spread for variety); a shot whose orientation is opposite
        // the screen — which would otherwise leave big side/top gaps — is grouped into a spread that
        // tiles several photos to fill the space. Seeded, so a slide always renders the same way.
        if (layout == LayoutMode.AUTO) {
            val portrait = orientationMap[files[from].name]
            val gappy = portrait != null && portrait == screenLandscape
            val chance = if (gappy) 0.85f else 0.28f
            if (rr.nextFloat() >= chance) return null
            val pool = if (gappy) AUTO_FILL_SPREADS else AUTO_VARIETY_SPREADS
            val m = pool[rr.nextInt(pool.size)]
            return resolveSpread(m)
        }
        if (rr.nextFloat() >= spreadMix.chance) return null
        val m = if (layout == LayoutMode.RANDOM) randomSpreads[rr.nextInt(randomSpreads.size)] else layout
        return resolveSpread(m)
    }

    // The slide anchored at index [from]: the photos it shows, and the index of the NEXT slide's
    // anchor — both taken by walking [playOrder] from [from]'s position, so a spread consumes the next
    // consecutive entries in the no-repeat order and the next anchor is the first entry after them.
    // Nothing is shown twice until the whole order has played through.
    fun slideFrom(from: Int): Pair<List<Int>, Int> {
        val n = playOrder.size
        if (n == 0) return listOf(from) to from
        val p = posOf[from] ?: 0
        val anchor = playOrder[p]
        fun entry(off: Int) = playOrder[(p + off) % n]
        if (isVideoName(files[anchor].name)) return listOf(anchor) to entry(1)
        // A spread: the anchor plus the next consecutive stills in the order (a clip ends the spread —
        // it becomes its own slide next). Count within the layout's range, seeded per anchor, capped by
        // the live memory budget and how many stills exist.
        spreadAt(anchor)?.let { m ->
            val range = rangeOf(m)
            val want = (range.first + kotlin.random.Random(anchor.toLong() * 31 + 3).nextInt(range.last - range.first + 1))
                .coerceAtMost(minOf(stillCount, spreadBudget.maxSpreadPhotos))
            val out = mutableListOf<Int>()
            var walked = 0
            while (out.size < want && walked < n) {
                val idx = entry(walked)
                if (isVideoName(files[idx].name)) break
                out.add(idx); walked++
            }
            if (out.size >= 3) return out to entry(out.size)
        }
        // Collage-fill a single photo whose orientation would otherwise leave big side/top gaps: pull
        // the next same-orientation stills from the order.
        if (collageOn && files.size >= 2) {
            val portrait = orientationMap[files[anchor].name]
            if (portrait != null && portrait == screenLandscape) {
                val want = if (screenLandscape) 3 else 2
                val out = mutableListOf(anchor)
                var walked = 1
                while (out.size < want && walked < n) {
                    val idx = entry(walked)
                    if (!isVideoName(files[idx].name) && orientationMap[files[idx].name] == portrait) { out.add(idx); walked++ }
                    else break
                }
                if (out.size >= 2) return out to entry(out.size)
            }
        }
        return listOf(anchor) to entry(1)
    }

    fun membersAt(from: Int): List<Int> = slideFrom(from).first

    val ctx = androidx.compose.ui.platform.LocalContext.current
    fun nextIndexFrom(from: Int): Int {
        if (files.size <= 1) return from
        val next = slideFrom(from).second
        // Never land back on the same index (would freeze the advance timer) — step on in the order.
        return if (next == from) playOrder[((posOf[from] ?: 0) + 1) % playOrder.size] else next
    }
    fun advanceFrom(from: Int) {
        if (files.size > 1) onNext(runCatching { nextIndexFrom(from) }.getOrDefault((from + 1) % files.size))
    }

    // Decode every still of a slide and WAIT until they are all in Coil's memory cache, so the wall
    // never swaps to a slide whose photos haven't loaded (no blank cell during the transition).
    suspend fun awaitSlideReady(index: Int) {
        val spx = slidePx(ctx)
        val loader = coil.Coil.imageLoader(ctx)
        coroutineScope {
            // Only the first few cells are force-decoded here; a big spread's remaining cells arrive
            // from the warm pass and crossfade in, so we never hold a dozen full-size decodes at once.
            membersAt(index).map { files[it] }.filter { !isVideoName(it.name) }.take(SLIDE_READY_CAP).map { f ->
                async(Dispatchers.IO) {
                    runCatching {
                        loader.execute(
                            coil.request.ImageRequest.Builder(ctx).data(f)
                                .size(spx, spx).precision(coil.size.Precision.INEXACT).build()
                        )
                    }
                }
            }.awaitAll()
        }
    }

    // Still photos advance on their own duration (or the slideshow default); videos advance when they end.
    // Every step is guarded so no single slide's layout maths can ever throw (which would crash the wall)
    // or stall it: whatever happens, we always advance to a valid next index. A 24/7 wall must never freeze.
    LaunchedEffect(idx, shuffle, intervalMs, files.size, currentIsVideo, collageOn, layout) {
        if (currentIsVideo || files.size <= 1) return@LaunchedEffect
        val sec = runCatching { durationOf(files[idx]) }.getOrDefault(0)
        delay(if (sec > 0) sec * 1000L else intervalMs)
        // Make sure the next slide's photos are decoded before switching (cap the wait so a slow
        // decode can never stall the show), then advance. Any error → just step to the next item.
        val next = runCatching { nextIndexFrom(idx) }.getOrDefault((idx + 1) % files.size)
        runCatching { withTimeout(2500) { awaitSlideReady(next) } }
        onNext(next)
    }
    // Tell the session exactly what this slide is (layout + the photos in it), so the Remote app can
    // mirror the wall instead of only showing the single "current" photo.
    LaunchedEffect(idx, layout, spreadMix, collageOn, files.size) {
        val m = membersAt(idx)
        val sp = spreadAt(idx)
        val style = sp?.name?.lowercase() ?: if (m.size > 1) "collage" else "single"
        reportSlide(style, m.map { files[it].name }, idx)
    }

    // Warm the next slide into Coil's cache ahead of time, decoded at screen size, so a spread (or a
    // big photo) never shows its empty mat/frame first and then pops the pictures in a second later.
    // We warm ONLY the slide that immediately follows the current one (not two ahead) and cap how many
    // of its cells we pre-decode, so the warm buffer can never pile a dozen full-size bitmaps on top of
    // the slide already on screen — that pile-up was crashing modest TV boxes after a run of spreads.
    // (Shuffle can't be predicted, so there we just warm a few upcoming files.)
    LaunchedEffect(idx, files.size, layout, shuffle, spreadMix, smartGroup) {
        if (files.size <= 1) return@LaunchedEffect
        val spx = slidePx(ctx)
        fun warm(f: File) {
            if (isVideoName(f.name)) return
            coil.Coil.imageLoader(ctx).enqueue(
                coil.request.ImageRequest.Builder(ctx).data(f)
                    .size(spx, spx)
                    .precision(coil.size.Precision.INEXACT)
                    .build()
            )
        }
        // Warm the actual next slide (predicted from the no-repeat order, which works for shuffle too).
        membersAt(nextIndexFrom(idx)).take(SLIDE_READY_CAP).forEach { warm(files[it]) }
    }
    val poolEnums = remember(effectPool) { effectPool.map { SlideEffect.from(it) }.filter { it != SlideEffect.RANDOM } }
    // Resolve the effect for a given slide: a seeded random from the pool when RANDOM.
    fun effAt(i: Int): SlideEffect =
        if (effect == SlideEffect.RANDOM && poolEnums.isNotEmpty())
            poolEnums[kotlin.random.Random(i.toLong()).nextInt(poolEnums.size)]
        else effect

    AnimatedContent(
        targetState = idx,
        transitionSpec = { transitionFor(effAt(targetState)) },
        label = "slide",
    ) { i ->
        val file = files[i.coerceIn(0, files.size - 1)]
        val members = membersAt(i.coerceIn(0, files.size - 1))
        // The "Random" look shuffles a different tasteful filter onto each photo.
        val eff = resolveLook(filter, i, filterPool)
        if (isVideoName(file.name)) {
            VideoSlide(
                file = file, volume = volume, fit = fit, vignette = eff == PhotoFilter.VIGNETTE,
                loop = files.size <= 1, capSec = durationOf(file),
                onEnded = { advanceFrom(i) },
            )
        } else if (members.size >= 3 && spreadAt(i) != null) {
            val mf = members.map { files[it] }
            val rotOf: (File) -> Int = { transformOf(it).rotNorm }
            val sp = spreadAt(i)
            MotionBox(motion, motionSpeed, seed = i) {
                when (sp) {
                    LayoutMode.MOSAIC -> MosaicSlide(mf, filter = eff, seed = i, landscape = screenLandscape, rotOf = rotOf, focusOf = focusOf, stagger = stagger, bg = bg)
                    LayoutMode.SCATTER -> ScatterSlide(mf, filter = eff, seed = i, rotOf = rotOf, focusOf = focusOf, stagger = stagger)
                    else -> PlannedSpread(mf, SpreadStyle.valueOf(sp!!.name), filter = eff, seed = i, rotOf = rotOf, focusOf = focusOf, stagger = stagger, bg = bg)
                }
            }
        } else if (members.size >= 2) {
            MotionBox(motion, motionSpeed, seed = i) {
                CollageSlide(members.map { files[it] }, filter = eff, horizontal = screenLandscape, rotOf = { transformOf(it).rotNorm }, focusOf = focusOf, bg = bg)
            }
        } else {
            val kb = if (effAt(i) == SlideEffect.KENBURNS) {
                val a = remember(i) { Animatable(1f) }
                LaunchedEffect(i) { a.animateTo(1.14f, tween(intervalMs.toInt(), easing = LinearEasing)) }
                a
            } else null
            // Ken Burns already moves the photo — don't stack the ambient motion on top of it.
            MotionBox(if (kb != null) MotionMode.OFF else motion, motionSpeed, seed = i) {
                PhotoContent(file, fit, transformOf(file), eff, focus = focusOf(file), bg = bg) { kb?.value ?: 1f }
            }
        }
    }
}

/** Maps a [SlideEffect] to its AnimatedContent enter/exit transition. */
private fun AnimatedContentTransitionScope<Int>.transitionFor(e: SlideEffect): ContentTransform = when (e) {
    SlideEffect.NONE -> fadeIn(tween(1)) togetherWith fadeOut(tween(1))
    SlideEffect.FADE -> fadeIn(tween(800)) togetherWith fadeOut(tween(800))
    SlideEffect.DISSOLVE -> fadeIn(tween(1600)) togetherWith fadeOut(tween(1600))
    SlideEffect.SLIDE ->
        (slideInHorizontally(tween(600)) { it } + fadeIn(tween(600))) togetherWith
            (slideOutHorizontally(tween(600)) { -it } + fadeOut(tween(600)))
    SlideEffect.SLIDERIGHT ->
        (slideInHorizontally(tween(600)) { -it } + fadeIn(tween(600))) togetherWith
            (slideOutHorizontally(tween(600)) { it } + fadeOut(tween(600)))
    SlideEffect.SLIDEUP ->
        (slideInVertically(tween(600)) { it } + fadeIn(tween(600))) togetherWith
            (slideOutVertically(tween(600)) { -it } + fadeOut(tween(600)))
    SlideEffect.SLIDEDOWN ->
        (slideInVertically(tween(600)) { -it } + fadeIn(tween(600))) togetherWith
            (slideOutVertically(tween(600)) { it } + fadeOut(tween(600)))
    SlideEffect.ZOOM ->
        (scaleIn(tween(800), initialScale = 0.85f) + fadeIn(tween(800))) togetherWith
            (scaleOut(tween(800), targetScale = 1.1f) + fadeOut(tween(800)))
    SlideEffect.ZOOMOUT ->
        (scaleIn(tween(800), initialScale = 1.15f) + fadeIn(tween(800))) togetherWith
            (scaleOut(tween(800), targetScale = 0.9f) + fadeOut(tween(800)))
    SlideEffect.REVEAL ->
        (scaleIn(tween(800), initialScale = 0.7f) + fadeIn(tween(800))) togetherWith fadeOut(tween(600))
    SlideEffect.GROW ->
        (scaleIn(tween(1000), initialScale = 0.4f) + fadeIn(tween(1000))) togetherWith
            (scaleOut(tween(800), targetScale = 1.3f) + fadeOut(tween(800)))
    SlideEffect.SWAP ->
        (slideInHorizontally(tween(700)) { it } + scaleIn(tween(700), initialScale = 0.8f) + fadeIn(tween(700))) togetherWith
            (slideOutHorizontally(tween(700)) { -it } + scaleOut(tween(700), targetScale = 0.8f) + fadeOut(tween(700)))
    SlideEffect.DRIFT ->
        (slideInVertically(tween(1200)) { it / 8 } + fadeIn(tween(1200))) togetherWith fadeOut(tween(1200))
    SlideEffect.CARDSTACK ->
        (scaleIn(tween(700), initialScale = 0.9f) + slideInVertically(tween(700)) { it / 6 } + fadeIn(tween(700))) togetherWith fadeOut(tween(500))
    SlideEffect.KENBURNS -> fadeIn(tween(800)) togetherWith fadeOut(tween(800))
    SlideEffect.RANDOM -> fadeIn(tween(800)) togetherWith fadeOut(tween(800))
}

/**
 * Lays several same-orientation photos side by side as one piece — a clean gallery "multi-aperture
 * mat": a soft mat-coloured separator between photos, each photo recessed by a hairline edge.
 */
/**
 * Wraps a still slide in a subtle, endless "living photo" motion while it waits on screen: a slow
 * breathing ZOOM, a gentle DRIFT across a slightly enlarged frame, or a soft BREATHE that dims and
 * brightens. MIX picks one per slide from [seed]. Everything stays clipped to the slide so the motion
 * never spills over the frame/mat, and the animated value is read only in the draw phase (no
 * per-frame recomposition). Each slide starts its motion from rest, so it eases in with the transition.
 */
@Composable
private fun MotionBox(mode: MotionMode, speed: MotionSpeed, seed: Int, content: @Composable () -> Unit) {
    val mixPool = listOf(
        MotionMode.ZOOM, MotionMode.DRIFT, MotionMode.BREATHE, MotionMode.SWAY, MotionMode.GLIDE,
        MotionMode.FLOAT, MotionMode.PULSE, MotionMode.RISE, MotionMode.FALL, MotionMode.SLIDEX,
        MotionMode.WOBBLE, MotionMode.PARALLAX, MotionMode.GLOW, MotionMode.ROCK,
    )
    val m = if (mode == MotionMode.MIX) mixPool[kotlin.random.Random(seed.toLong() * 131 + 5).nextInt(mixPool.size)] else mode
    if (m == MotionMode.OFF) { content(); return }
    val t = androidx.compose.animation.core.rememberInfiniteTransition(label = "motion")
    val p = t.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            tween(speed.sweepMs, easing = androidx.compose.animation.core.CubicBezierEasing(0.37f, 0f, 0.63f, 1f)),
            androidx.compose.animation.core.RepeatMode.Reverse,
        ),
        label = "motionP",
    )
    // A per-slide drift direction, so consecutive photos don't all slide the same way.
    val r = remember(seed) { kotlin.random.Random(seed.toLong() * 977 + 3) }
    val dirX = remember(seed) { if (r.nextBoolean()) 1f else -1f }
    val dirY = remember(seed) { (r.nextFloat() - 0.5f) * 1.2f }
    Box(Modifier.fillMaxSize().clipToBounds()) {
        Box(
            Modifier.fillMaxSize().graphicsLayer {
                val v = p.value
                when (m) {
                    MotionMode.ZOOM -> { val s = 1f + 0.05f * v; scaleX = s; scaleY = s }
                    MotionMode.DRIFT -> {
                        // Enlarged just enough that the ±2% pan never reveals an edge.
                        scaleX = 1.06f; scaleY = 1.06f
                        translationX = (v - 0.5f) * 2f * dirX * size.width * 0.02f
                        translationY = (v - 0.5f) * 2f * dirY * size.height * 0.02f
                    }
                    MotionMode.BREATHE -> { val s = 1f + 0.015f * v; scaleX = s; scaleY = s }
                    // A gentle rocking rotation; the slight scale-up hides the corners as it turns.
                    MotionMode.SWAY -> { scaleX = 1.06f; scaleY = 1.06f; rotationZ = (v - 0.5f) * 2f * 1.1f }
                    // A cinematic Ken-Burns: slow zoom while panning diagonally.
                    MotionMode.GLIDE -> {
                        val s = 1.05f + 0.06f * v; scaleX = s; scaleY = s
                        translationX = (v - 0.5f) * 2f * dirX * size.width * 0.02f
                        translationY = (v - 0.5f) * 2f * dirY * size.height * 0.02f
                    }
                    // A gentle vertical bob.
                    MotionMode.FLOAT -> { scaleX = 1.05f; scaleY = 1.05f; translationY = (v - 0.5f) * 2f * size.height * 0.018f }
                    // A pendulum swing, pivoting near the top edge.
                    MotionMode.SWING -> { scaleX = 1.07f; scaleY = 1.07f; transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0f); rotationZ = (v - 0.5f) * 2f * 1.4f }
                    // A firm scale pulse.
                    MotionMode.PULSE -> { val s = 1f + 0.07f * v; scaleX = s; scaleY = s }
                    // A subtle 3-D tilt around the vertical axis.
                    MotionMode.TILT -> { cameraDistance = 16f * density; scaleX = 1.08f; scaleY = 1.08f; rotationY = (v - 0.5f) * 2f * 6f }
                    // A subtle 3-D tilt around the horizontal axis.
                    MotionMode.TILTY -> { cameraDistance = 16f * density; scaleX = 1.08f; scaleY = 1.08f; rotationX = (v - 0.5f) * 2f * 6f }
                    // Slow zoom while rising up the frame.
                    MotionMode.RISE -> { val s = 1.06f + 0.06f * v; scaleX = s; scaleY = s; translationY = -(v - 0.5f) * 2f * size.height * 0.02f }
                    // Slow zoom while settling down the frame.
                    MotionMode.FALL -> { val s = 1.06f + 0.06f * v; scaleX = s; scaleY = s; translationY = (v - 0.5f) * 2f * size.height * 0.02f }
                    // A straight horizontal pan across a slightly enlarged photo.
                    MotionMode.SLIDEX -> { scaleX = 1.07f; scaleY = 1.07f; translationX = (v - 0.5f) * 2f * dirX * size.width * 0.028f }
                    // A soft wobble: a little turn with a little scale.
                    MotionMode.WOBBLE -> { val s = 1.05f + 0.02f * v; scaleX = s; scaleY = s; rotationZ = (v - 0.5f) * 2f * 1.0f }
                    // Parallax: zoom one way while drifting the other.
                    MotionMode.PARALLAX -> { val s = 1.08f - 0.04f * v; scaleX = s; scaleY = s; translationX = (v - 0.5f) * 2f * dirX * size.width * 0.02f }
                    // ROCK: a wider, slower rotation than SWAY.
                    MotionMode.ROCK -> { scaleX = 1.07f; scaleY = 1.07f; rotationZ = (v - 0.5f) * 2f * 1.8f }
                    else -> {}
                }
            },
        ) { content() }
        // BREATHE: a soft fade down and back up, as a dark veil (fading the photo itself would let the
        // cream mat or black backing show through).
        if (m == MotionMode.BREATHE) {
            Box(Modifier.fillMaxSize().graphicsLayer { alpha = 0.22f * p.value }.background(Color.Black))
        }
        // GLOW: a soft brighten and back, as a white veil.
        if (m == MotionMode.GLOW) {
            Box(Modifier.fillMaxSize().graphicsLayer { alpha = 0.16f * p.value }.background(Color.White))
        }
    }
}

/**
 * 3–5 photos in a gutter-separated grid on a warm mat — the gutters are the "divider strips". The
 * template is chosen by [seed] so each slide gets a different but stable arrangement.
 */
@Composable
private fun MosaicSlide(files: List<File>, filter: PhotoFilter, seed: Int, landscape: Boolean, rotOf: (File) -> Int = { 0 }, focusOf: (File) -> PhotoFocus? = { null }, stagger: Boolean = false, bg: Color = Color(0xFFEBE4D7)) {
    val cf = lookFilter(filter)
    val mat = bg   // the gutters follow the chosen Background colour
    val gap = 10.dp
    val r = kotlin.random.Random(seed.toLong() * 104729 + 7)
    val n = files.size.coerceIn(3, 5)
    val f = files.take(n)

    // Building blocks: a row/column of cells sharing space equally.
    @Composable fun cell(file: File, m: Modifier) {
        val st = rememberStagger(f.indexOf(file), stagger)
        CollageCell(file, cf, m.staggered(st), rot = rotOf(file), focus = focusOf(file))
    }
    @Composable fun strip(items: List<File>, m: Modifier, horizontal: Boolean) {
        if (horizontal) Row(m, horizontalArrangement = Arrangement.spacedBy(gap)) { items.forEach { cell(it, Modifier.weight(1f).fillMaxHeight()) } }
        else Column(m, verticalArrangement = Arrangement.spacedBy(gap)) { items.forEach { cell(it, Modifier.weight(1f).fillMaxWidth()) } }
    }
    // Four or more in one strip makes thin slivers that slice faces in half — fold them into a 2×2
    // block instead (two strips side by side), so every cell stays close to a normal photo shape.
    @Composable fun stack(items: List<File>, m: Modifier, horizontal: Boolean) {
        if (items.size < 4) { strip(items, m, horizontal); return }
        val half = (items.size + 1) / 2
        if (horizontal) Column(m, verticalArrangement = Arrangement.spacedBy(gap)) {
            strip(items.take(half), Modifier.weight(1f).fillMaxWidth(), horizontal = true)
            strip(items.drop(half), Modifier.weight(1f).fillMaxWidth(), horizontal = true)
        } else Row(m, horizontalArrangement = Arrangement.spacedBy(gap)) {
            strip(items.take(half), Modifier.weight(1f).fillMaxHeight(), horizontal = false)
            strip(items.drop(half), Modifier.weight(1f).fillMaxHeight(), horizontal = false)
        }
    }
    // "Hero + strip": one large photo taking ~60% along the main axis, the rest sharing a strip
    // beside it. On a landscape screen the hero sits left/right; on portrait, top/bottom.
    @Composable fun heroAndStrip(heroFirst: Boolean) {
        val hero = f[0]; val rest = f.drop(1)
        if (landscape) Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(gap)) {
            if (heroFirst) { cell(hero, Modifier.weight(1.5f).fillMaxHeight()); stack(rest, Modifier.weight(1f).fillMaxHeight(), horizontal = false) }
            else { stack(rest, Modifier.weight(1f).fillMaxHeight(), horizontal = false); cell(hero, Modifier.weight(1.5f).fillMaxHeight()) }
        } else Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(gap)) {
            if (heroFirst) { cell(hero, Modifier.weight(1.5f).fillMaxWidth()); stack(rest, Modifier.weight(1f).fillMaxWidth(), horizontal = true) }
            else { stack(rest, Modifier.weight(1f).fillMaxWidth(), horizontal = true); cell(hero, Modifier.weight(1.5f).fillMaxWidth()) }
        }
    }
    // "Two bands": the photos split into two rows (landscape) or two columns (portrait).
    @Composable fun twoBands(firstCount: Int) {
        val a = f.take(firstCount); val b = f.drop(firstCount)
        if (landscape) Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(gap)) {
            strip(a, Modifier.weight(1f).fillMaxWidth(), horizontal = true); strip(b, Modifier.weight(1f).fillMaxWidth(), horizontal = true)
        } else Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(gap)) {
            strip(a, Modifier.weight(1f).fillMaxHeight(), horizontal = false); strip(b, Modifier.weight(1f).fillMaxHeight(), horizontal = false)
        }
    }

    Box(Modifier.fillMaxSize().background(mat).padding(gap)) {
        when (n) {
            3 -> heroAndStrip(heroFirst = r.nextBoolean())
            4 -> when (r.nextInt(3)) { 0 -> twoBands(2); else -> heroAndStrip(heroFirst = r.nextBoolean()) }
            else -> when (r.nextInt(3)) { 0 -> twoBands(2); 1 -> twoBands(3); else -> heroAndStrip(heroFirst = r.nextBoolean()) }
        }
        if (filter == PhotoFilter.VIGNETTE) VignetteOverlay()
    }
}

/**
 * 3–5 photos "tossed on a table": each a white-bordered print, slightly tilted, placed on a
 * jittered grid so they read as a casual spread without burying one another. Seeded by [seed].
 */
@Composable
private fun ScatterSlide(files: List<File>, filter: PhotoFilter, seed: Int, rotOf: (File) -> Int = { 0 }, focusOf: (File) -> PhotoFocus? = { null }, stagger: Boolean = false) {
    val cf = lookFilter(filter)
    val r = kotlin.random.Random(seed.toLong() * 65537 + 11)
    val n = files.size.coerceIn(3, 5)
    val f = files.take(n)
    val density = androidx.compose.ui.platform.LocalDensity.current.density
    // clipToBounds: a print's shadow/tilt must never spill out over the frame or mat.
    BoxWithConstraints(Modifier.fillMaxSize().clipToBounds().background(Color(0xFF26201B))) {
        // Faint table-top vignette so the prints sit "in" the surface rather than float on flat black.
        Box(Modifier.matchParentSize().background(Brush.radialGradient(listOf(Color(0xFF3A312A), Color(0xFF1C1714)), radius = maxWidth.value * density * 0.75f)))
        val w = maxWidth.value; val h = maxHeight.value          // dp
        val landscape = w >= h
        val cols = if (landscape) (if (n <= 3) n else 3) else 2
        val rows = (n + cols - 1) / cols
        val cellW = w / cols; val cellH = h / rows
        // A print is a bit smaller than its cell so tilt + jitter leave breathing room.
        val base = minOf(cellW, cellH) * 0.86f
        f.forEachIndexed { k, file ->
            val col = k % cols; val row = k / cols
            // Centre a short last row.
            val rowCount = if (row == rows - 1) n - row * cols else cols
            val rowOffset = (cols - rowCount) * cellW / 2f
            val size = base * (0.9f + r.nextFloat() * 0.2f)
            val tilt = (r.nextFloat() - 0.5f) * 16f                    // ±8°
            // Half the tilted print's bounding box, so jitter can't push a print past the edge.
            val rad = Math.toRadians(kotlin.math.abs(tilt).toDouble())
            val ext = size / 2f * (kotlin.math.cos(rad) + kotlin.math.sin(rad)).toFloat()
            val cx = (rowOffset + col * cellW + cellW / 2f + (r.nextFloat() - 0.5f) * cellW * 0.22f).coerceIn(ext, maxOf(ext, w - ext))
            val cy = (row * cellH + cellH / 2f + (r.nextFloat() - 0.5f) * cellH * 0.22f).coerceIn(ext, maxOf(ext, h - ext))
            val border = size * 0.035f
            val st = rememberStagger(k, stagger)
            Box(
                Modifier
                    // Anchor at the physical top-left: the translation below is measured from the
                    // left edge, but a plain Box child starts at TopStart — the top-RIGHT on an
                    // RTL (Hebrew) screen — which pushed every print off the right side.
                    .align(androidx.compose.ui.AbsoluteAlignment.TopLeft)
                    .size(size.dp)
                    .graphicsLayer {
                        translationX = (cx - size / 2f) * density
                        translationY = (cy - size / 2f) * density
                        rotationZ = tilt
                        shadowElevation = 22f * density
                        shape = androidx.compose.ui.graphics.RectangleShape
                        clip = false
                    }
                    .staggered(st)
                    .background(Color(0xFFF7F3EC))
                    .padding(start = border.dp, top = border.dp, end = border.dp, bottom = (border * 2.4f).dp),
            ) {
                // The print is square, so the photo's own quarter-turn is a plain rotation (no swap).
                val rot = rotOf(file)
                AsyncImage(
                    model = rememberSlideModel(file), contentDescription = null, contentScale = ContentScale.Crop, colorFilter = cf,
                    alignment = focusAlignment(focusOf(file), 1f, 1f),
                    modifier = Modifier.fillMaxSize().graphicsLayer { rotationZ = rot.toFloat() },
                )
            }
        }
        if (filter == PhotoFilter.VIGNETTE) VignetteOverlay()
    }
}

/**
 * A spread's staggered entrance: photo [k] fades, rises and settles in a beat after the one before
 * it. Off → already fully shown. Read through [staggered] in the draw phase only.
 */
@Composable
private fun rememberStagger(k: Int, on: Boolean): androidx.compose.runtime.State<Float> {
    val a = remember { Animatable(if (on) 0f else 1f) }
    LaunchedEffect(on) {
        if (on) {
            a.snapTo(0f)
            delay(350L + k.coerceAtLeast(0) * 420L)
            a.animateTo(1f, tween(750, easing = androidx.compose.animation.core.FastOutSlowInEasing))
        } else a.snapTo(1f)
    }
    return a.asState()
}

private fun Modifier.staggered(v: androidx.compose.runtime.State<Float>): Modifier = graphicsLayer {
    val t = v.value
    alpha = t
    val s = 0.9f + 0.1f * t
    scaleX = s; scaleY = s
    translationY = (1f - t) * 28.dp.toPx()
}

/** The surface a planned spread sits on. */
// The plain "paper"/"mat"/"white" surfaces follow the user's Background colour, so choosing Black
// gives a dark wall instead of a bright page. The deliberately-textured surfaces (cork, velvet,
// film, wall, linen, table) keep their look — they sit on the [bg] anyway.
private fun surfaceBrush(s: SpreadSurface, bg: Color = Color.Black): Brush = when (s) {
    SpreadSurface.MAT, SpreadSurface.PAPER, SpreadSurface.WHITE, SpreadSurface.BLACK -> SolidColor(bg)
    SpreadSurface.TABLE -> Brush.radialGradient(listOf(Color(0xFF3A312A), Color(0xFF1C1714)))
    SpreadSurface.CORK -> Brush.radialGradient(listOf(Color(0xFFC79D6E), Color(0xFF9C7149)))
    SpreadSurface.FILM -> Brush.radialGradient(listOf(Color(0xFF2E2D33), Color(0xFF111114)))
    SpreadSurface.VELVET -> Brush.radialGradient(listOf(Color(0xFF4A1F2C), Color(0xFF1C0A11)))
    SpreadSurface.WALL -> Brush.verticalGradient(listOf(Color(0xFFE9E4DC), Color(0xFFD6CFC2)))
    SpreadSurface.LINEN -> Brush.verticalGradient(listOf(Color(0xFFF1ECE3), Color(0xFFDFD7C8)))
}

/**
 * Draws one of the planned [SpreadStyle] layouts: positions come from [placeSpread] (pure and
 * bounds-checked, so no photo can leave the slide), each print drawn in its [PrintStyle], plus the
 * surface details — the film band with its sprocket holes, the clothesline with a peg per print.
 */
@Composable
private fun PlannedSpread(
    files: List<File>, style: SpreadStyle, filter: PhotoFilter, seed: Int,
    rotOf: (File) -> Int = { 0 }, focusOf: (File) -> PhotoFocus? = { null }, stagger: Boolean = false,
    bg: Color = Color.Black,
) {
    val cf = lookFilter(filter)
    BoxWithConstraints(Modifier.fillMaxSize().clipToBounds().background(surfaceBrush(style.surface, bg))) {
        val w = maxWidth.value; val h = maxHeight.value
        val plan = remember(style, files.size, w, h, seed) { placeSpread(style, files.size, w, h, seed) }
        val density = androidx.compose.ui.platform.LocalDensity.current.density

        plan.film?.let { band ->
            androidx.compose.foundation.Canvas(Modifier.matchParentSize()) {
                val d = density
                drawRect(Color(0xFF0B0B0C), Offset(band.x * d, band.y * d), androidx.compose.ui.geometry.Size(band.w * d, band.h * d))
                // Sprocket holes along both long edges of the band.
                val across = if (band.horizontal) band.h else band.w
                val hole = across * 0.07f
                val step = hole * 2.2f
                val len = if (band.horizontal) band.w else band.h
                var a = step / 2f
                while (a + hole < len) {
                    for (edge in 0..1) {
                        val off = if (edge == 0) across * 0.045f else across - across * 0.045f - hole * 0.75f
                        val (x, y) = if (band.horizontal) (band.x + a) to (band.y + off) else (band.x + off) to (band.y + a)
                        val (hw, hh) = if (band.horizontal) hole to hole * 0.75f else hole * 0.75f to hole
                        drawRoundRect(
                            Color(0xFFEDE6D8).copy(alpha = 0.85f), Offset(x * d, y * d),
                            androidx.compose.ui.geometry.Size(hw * d, hh * d),
                            androidx.compose.ui.geometry.CornerRadius(hole * 0.18f * d),
                        )
                    }
                    a += step
                }
            }
        }
        if (plan.lines.isNotEmpty()) {
            androidx.compose.foundation.Canvas(Modifier.matchParentSize()) {
                val d = density
                plan.lines.forEach { l ->
                    val path = androidx.compose.ui.graphics.Path()
                    val steps = 32
                    for (i in 0..steps) {
                        val t = i / steps.toFloat()
                        val x = t * w * d; val y = l.yAt(t) * d
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(path, Color(0xFF6E6150), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.2f * d))
                }
            }
        }

        plan.items.forEachIndexed { k, p ->
            val file = files.getOrNull(k) ?: return@forEachIndexed
            val st = rememberStagger(k, stagger)
            val circle = p.style == PrintStyle.CIRCLE
            val shape = if (circle) androidx.compose.foundation.shape.CircleShape else androidx.compose.ui.graphics.RectangleShape
            val minSide = minOf(p.w, p.h)
            Box(
                Modifier
                    // Absolute placement: offsets are measured from the left even on an RTL screen.
                    .align(androidx.compose.ui.AbsoluteAlignment.TopLeft)
                    .absoluteOffset(p.x.dp, p.y.dp)
                    .size(p.w.dp, p.h.dp)
                    .graphicsLayer {
                        rotationZ = p.rot
                        shadowElevation = if (p.style == PrintStyle.CELL) 0f else 16f * density
                        this.shape = shape
                        clip = false
                    }
                    .staggered(st),
            ) {
                val rot = rotOf(file); val focus = focusOf(file)
                when (p.style) {
                    PrintStyle.CELL -> CollageCell(file, cf, Modifier.fillMaxSize(), rot = rot, focus = focus)
                    PrintStyle.PRINT -> Box(Modifier.fillMaxSize().background(Color(0xFFF7F3EC)).padding((minSide * 0.04f).dp)) {
                        CollageCell(file, cf, Modifier.fillMaxSize(), rot = rot, focus = focus)
                    }
                    PrintStyle.POLAROID -> Box(
                        Modifier.fillMaxSize().background(Color(0xFFFBF9F4))
                            .padding(start = (p.w * 0.05f).dp, top = (p.w * 0.05f).dp, end = (p.w * 0.05f).dp, bottom = (p.w * 0.19f).dp),
                    ) { CollageCell(file, cf, Modifier.fillMaxSize(), rot = rot, focus = focus) }
                    PrintStyle.FRAMED -> Box(Modifier.fillMaxSize().background(Color(0xFF1E1B18)).padding((minSide * 0.035f).dp)) {
                        Box(Modifier.fillMaxSize().background(Color(0xFFF5F2EB)).padding((minSide * 0.075f).dp)) {
                            CollageCell(file, cf, Modifier.fillMaxSize(), rot = rot, focus = focus)
                        }
                    }
                    PrintStyle.CIRCLE -> Box(
                        Modifier.fillMaxSize().clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(0xFFF7F3EC)).padding((minSide * 0.035f).dp)
                            .clip(androidx.compose.foundation.shape.CircleShape),
                    ) { CollageCell(file, cf, Modifier.fillMaxSize(), rot = rot, focus = focus) }
                }
                // A wooden peg holding each print to the clothesline.
                if (style == SpreadStyle.CLOTHESLINE) {
                    Box(
                        Modifier.align(Alignment.TopCenter)
                            .offset(y = (-minSide * 0.06f).dp)
                            .size((minSide * 0.07f).dp, (minSide * 0.2f).dp)
                            .clip(RoundedCornerShape((minSide * 0.015f).dp))
                            .background(Brush.horizontalGradient(listOf(Color(0xFFC9A57A), Color(0xFFA9845A)))),
                    )
                }
            }
        }
        if (filter == PhotoFilter.VIGNETTE) VignetteOverlay()
    }
}

@Composable
private fun CollageSlide(files: List<File>, filter: PhotoFilter, horizontal: Boolean, rotOf: (File) -> Int = { 0 }, focusOf: (File) -> PhotoFocus? = { null }, bg: Color = Color(0xFFEBE4D7)) {
    val cf = lookFilter(filter)
    val mat = bg      // gutters follow the chosen Background colour
    val gap = 12.dp
    Box(Modifier.fillMaxSize().background(mat).padding(gap)) {
        if (horizontal) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                files.forEach { f -> CollageCell(f, cf, Modifier.weight(1f).fillMaxHeight(), rot = rotOf(f), focus = focusOf(f)) }
            }
        } else {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(gap)) {
                files.forEach { f -> CollageCell(f, cf, Modifier.weight(1f).fillMaxWidth(), rot = rotOf(f), focus = focusOf(f)) }
            }
        }
        if (filter == PhotoFilter.VIGNETTE) VignetteOverlay()
    }
}

@Composable
private fun CollageCell(f: File, cf: androidx.compose.ui.graphics.ColorFilter?, modifier: Modifier, rot: Int = 0, focus: PhotoFocus? = null) {
    BoxWithConstraints(modifier.clipToBounds(), contentAlignment = Alignment.Center) {
        // Honour the photo's display rotation like PhotoContent does: lay a quarter-turned image
        // out with width/height swapped (requiredSize — plain size() gets clamped) so it still fills.
        val swapped = rot == 90 || rot == 270
        val bw = if (swapped) maxHeight else maxWidth
        val bh = if (swapped) maxWidth else maxHeight
        AsyncImage(
            model = rememberSlideModel(f), contentDescription = null, contentScale = ContentScale.Crop, colorFilter = cf,
            // Aim the crop at the faces rather than the middle of the photo.
            alignment = focusAlignment(focus, bw.value, bh.value),
            modifier = Modifier
                .requiredSize(bw, bh)
                .graphicsLayer { rotationZ = rot.toFloat() },
        )
        // Hairline bevel so each photo reads as recessed into the mat.
        Box(Modifier.matchParentSize().border(1.dp, Color.Black.copy(alpha = 0.28f)))
        androidx.compose.foundation.Canvas(Modifier.matchParentSize()) {
            val d = (size.minDimension * 0.02f).coerceIn(3f, 14f)
            val sh = Color.Black.copy(alpha = 0.3f)
            drawRect(Brush.verticalGradient(listOf(sh, Color.Transparent), 0f, d), size = androidx.compose.ui.geometry.Size(size.width, d))
            drawRect(Brush.horizontalGradient(listOf(sh, Color.Transparent), 0f, d), size = androidx.compose.ui.geometry.Size(d, size.height))
        }
    }
}

/** Plays one video library item; advances the slideshow when it finishes (unless it's the only item). */
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun VideoSlide(file: File, volume: Float, fit: PhotoFit, vignette: Boolean, loop: Boolean, capSec: Int, onEnded: () -> Unit) {
    val context = LocalContext.current
    val advanced = remember(file.path) { java.util.concurrent.atomic.AtomicBoolean(false) }
    fun finishOnce() { if (advanced.compareAndSet(false, true)) onEnded() }
    val player = remember(file.path) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.fromFile(file)))
            repeatMode = if (loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            playWhenReady = true
            prepare()
        }
    }
    LaunchedEffect(volume) { player.volume = volume }
    // Optional per-clip cap: cut to the next item after capSec seconds.
    if (!loop && capSec > 0) LaunchedEffect(file.path) { delay(capSec * 1000L); finishOnce() }
    // Watchdog: if the clip never starts playing (unsupported codec, corrupt file, stuck buffering),
    // move on instead of freezing the wall on it forever. A playable clip reaches READY within a few
    // seconds, so this never cuts a good video short.
    if (!loop) LaunchedEffect(file.path) {
        delay(15_000)
        if (player.playbackState != Player.STATE_READY && player.playbackState != Player.STATE_ENDED) finishOnce()
    }
    DisposableEffect(file.path) {
        val l = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED && !loop) finishOnce()
            }
            // A load/decode failure must not freeze the slideshow — skip to the next item.
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) { if (!loop) finishOnce() }
        }
        player.addListener(l)
        onDispose { player.removeListener(l); player.release() }
    }
    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    resizeMode = if (fit == PhotoFit.FIT) AspectRatioFrameLayout.RESIZE_MODE_FIT
                        else AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
        if (vignette) VignetteOverlay()
    }
}

/** Extracts a pleasing dominant colour from a photo for the Adaptive frame. */

internal fun dominantColor(f: java.io.File): Color? = runCatching {
    val opts = android.graphics.BitmapFactory.Options().apply { inSampleSize = 4 }
    val bmp = android.graphics.BitmapFactory.decodeFile(f.path, opts) ?: return null
    val palette = androidx.palette.graphics.Palette.from(bmp).generate()
    bmp.recycle()
    val c = palette.getVibrantColor(0).takeIf { it != 0 }
        ?: palette.getMutedColor(0).takeIf { it != 0 }
        ?: palette.getDominantColor(0).takeIf { it != 0 }
        ?: return null
    Color(c)
}.getOrNull()

// AUTO layout pools. FILL spreads tile several same-ish photos edge to edge to cover a wall a single
// mis-oriented photo would leave gappy; VARIETY spreads are the occasional decorative grouping used
// when the photo already fits. All have a low minimum count so they work with modest libraries.
private val AUTO_FILL_SPREADS = listOf(
    LayoutMode.GRID, LayoutMode.ROWS, LayoutMode.COLUMNS, LayoutMode.TRIPTYCH, LayoutMode.CAROUSEL, LayoutMode.MAGAZINE,
)
private val AUTO_VARIETY_SPREADS = listOf(
    LayoutMode.MOSAIC, LayoutMode.GALLERY, LayoutMode.POLAROID, LayoutMode.SCATTER,
)

/** A ColorFilter for the colour-matrix "looks" (null = leave the image untouched). */
/** Resolves the "Random" look to a per-photo filter from [pool] (seeded by slide index). */
private val RANDOM_LOOK_POOL = listOf(PhotoFilter.NONE, PhotoFilter.MONO, PhotoFilter.SEPIA, PhotoFilter.WARM, PhotoFilter.COOL, PhotoFilter.VIGNETTE)
private fun resolveLook(filter: PhotoFilter, index: Int, pool: List<PhotoFilter> = emptyList()): PhotoFilter {
    if (filter != PhotoFilter.RANDOM) return filter
    val p = pool.ifEmpty { RANDOM_LOOK_POOL }
    return p[kotlin.random.Random(index.toLong() * 2654435761L).nextInt(p.size)]
}

private fun lookFilter(filter: PhotoFilter): androidx.compose.ui.graphics.ColorFilter? = when (filter) {
    PhotoFilter.MONO -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) }
    )
    PhotoFilter.SEPIA -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            0.393f, 0.769f, 0.189f, 0f, 0f,
            0.349f, 0.686f, 0.168f, 0f, 0f,
            0.272f, 0.534f, 0.131f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
        ))
    )
    PhotoFilter.WARM -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            1.12f, 0f, 0f, 0f, 6f,
            0f, 1.0f, 0f, 0f, 0f,
            0f, 0f, 0.85f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
        ))
    )
    PhotoFilter.COOL -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            0.88f, 0f, 0f, 0f, 0f,
            0f, 0.98f, 0f, 0f, 0f,
            0f, 0f, 1.15f, 0f, 6f,
            0f, 0f, 0f, 1f, 0f,
        ))
    )
    PhotoFilter.VIVID -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(1.45f) }
    )
    PhotoFilter.POP -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(1.8f) }
    )
    PhotoFilter.NOIR -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            0.404f, 0.793f, 0.154f, 0f, -45f,
            0.404f, 0.793f, 0.154f, 0f, -45f,
            0.404f, 0.793f, 0.154f, 0f, -45f,
            0f, 0f, 0f, 1f, 0f,
        ))
    )
    PhotoFilter.FADE -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            0.82f, 0f, 0f, 0f, 28f,
            0f, 0.82f, 0f, 0f, 26f,
            0f, 0f, 0.82f, 0f, 30f,
            0f, 0f, 0f, 1f, 0f,
        ))
    )
    PhotoFilter.CINEMA -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            1.12f, 0f, 0f, 0f, 2f,
            0f, 0.96f, 0.04f, 0f, 0f,
            0.04f, 0f, 1.04f, 0f, 8f,
            0f, 0f, 0f, 1f, 0f,
        ))
    )
    PhotoFilter.GOLDEN -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            1.18f, 0f, 0f, 0f, 12f,
            0f, 1.04f, 0f, 0f, 4f,
            0f, 0f, 0.78f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
        ))
    )
    PhotoFilter.DUSK -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            1.06f, 0f, 0f, 0f, 6f,
            0f, 0.9f, 0f, 0f, 0f,
            0f, 0f, 1.12f, 0f, 8f,
            0f, 0f, 0f, 1f, 0f,
        ))
    )
    PhotoFilter.FROST -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            0.9f, 0f, 0f, 0f, 6f,
            0f, 0.98f, 0f, 0f, 8f,
            0f, 0f, 1.18f, 0f, 12f,
            0f, 0f, 0f, 1f, 0f,
        ))
    )
    PhotoFilter.MATTE -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            0.85f, 0.05f, 0.05f, 0f, 22f,
            0.05f, 0.85f, 0.05f, 0f, 22f,
            0.05f, 0.05f, 0.85f, 0f, 24f,
            0f, 0f, 0f, 1f, 0f,
        ))
    )
    PhotoFilter.ROSE -> androidx.compose.ui.graphics.ColorFilter.colorMatrix(
        androidx.compose.ui.graphics.ColorMatrix(floatArrayOf(
            1.1f, 0f, 0f, 0f, 10f,
            0f, 0.96f, 0f, 0f, 2f,
            0f, 0f, 1.0f, 0f, 6f,
            0f, 0f, 0f, 1f, 0f,
        ))
    )
    else -> null
}

/** A soft dark vignette drawn over the media (used by the VIGNETTE look). */
@Composable
private fun BoxScope.VignetteOverlay() {
    Box(
        Modifier.matchParentSize().background(
            Brush.radialGradient(
                0.0f to Color.Transparent, 0.68f to Color.Transparent, 1.0f to Color.Black.copy(alpha = 0.62f),
            )
        )
    )
}

/**
 * Where to anchor a [ContentScale.Crop] of a photo into a [boxW]×[boxH] box (any unit, only the ratio
 * matters, measured before any display rotation) so its [focus] — the faces — stays in view instead
 * of the plain centre: horizontally the faces are centred, vertically they sit a little above the
 * middle (natural headroom), each clamped so the crop never runs past the photo's edge. Absolute
 * (not start/end) bias, so an RTL screen doesn't mirror it. No focus (no face found) → centred
 * across, but leaning to the upper part when trimming top/bottom — that's where heads usually are,
 * so a tall photo in a wide slot shows faces rather than a belly.
 */
private val NoFocusAlignment = androidx.compose.ui.BiasAbsoluteAlignment(0f, -0.4f)

internal fun focusAlignment(focus: PhotoFocus?, boxW: Float, boxH: Float): Alignment {
    if (focus == null || focus.aspect <= 0f || !(boxW > 0f) || !(boxH > 0f) || boxW.isInfinite() || boxH.isInfinite()) return NoFocusAlignment
    val boxAr = boxW / boxH
    // Fraction of the picture left visible along each axis once Crop fills the box.
    val vx = if (focus.aspect > boxAr) boxAr / focus.aspect else 1f
    val vy = if (focus.aspect < boxAr) focus.aspect / boxAr else 1f
    fun bias(f: Float, v: Float, at: Float): Float {
        if (v >= 0.999f) return 0f
        val start = (f - v * at).coerceIn(0f, 1f - v)
        return start / (1f - v) * 2f - 1f
    }
    return androidx.compose.ui.BiasAbsoluteAlignment(bias(focus.x, vx, 0.5f), bias(focus.y, vy, 0.42f))
}

/** The one decode size every slideshow image shares, capped so a hardware-bitmap texture is always
 *  safe and so each photo is decoded ONCE and reused by its collage cell and full-screen alike.
 *  Kept at 1600 (plenty sharp on a 1080p/1440p wall) so a landscape photo is ~5 MB, not ~9 MB — a
 *  spread of several photos plus the warmed next slide then stays well within a cheap TV box's
 *  memory and the app no longer crashes after a run of busy slides. */
private const val SLIDE_MAX_PX = 1600

/** How many of a spread's cells we force-decode before switching to it; the rest crossfade in from
 *  the warm pass. Bounds the burst of simultaneous full-size decodes at a transition. The total
 *  number of photos a spread may hold is decided live by [com.il90.salongallery.diag.MemoryGovernor]. */
private const val SLIDE_READY_CAP = 6
private fun slidePx(ctx: android.content.Context): Int =
    ctx.resources.displayMetrics.let { minOf(maxOf(it.widthPixels, it.heightPixels), SLIDE_MAX_PX) }
@Composable
private fun rememberSlideModel(file: File): coil.request.ImageRequest {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val px = remember { slidePx(ctx) }
    return remember(file, px) {
        coil.request.ImageRequest.Builder(ctx).data(file).size(px, px).precision(coil.size.Precision.INEXACT).build()
    }
}

/** The chosen backing colour behind photos and letterbox bars. */
internal fun bgColorOf(key: String): Color = when (key.lowercase()) {
    "charcoal" -> Color(0xFF14110E)
    "slate" -> Color(0xFF2B2F36)
    "warm" -> Color(0xFF1C140D)
    "white" -> Color(0xFFF2EEE6)
    else -> Color.Black   // "black" and "auto" (auto resolves to a per-photo colour at the call site)
}

/** A muted, darkened version of a colour — for the "Auto" background wash behind a photo. */
internal fun muteDark(c: Color): Color = Color(c.red * 0.3f, c.green * 0.3f, c.blue * 0.3f, 1f)

/** Renders one photo, applying its studio [transform], a [filter] look and an optional Ken-Burns [kb] zoom. */
@Composable
fun PhotoContent(file: File, fit: PhotoFit, transform: PhotoTransform, filter: PhotoFilter = PhotoFilter.NONE, focus: PhotoFocus? = null, bg: Color = Color.Black, kb: () -> Float = { 1f }) {
    val cf = lookFilter(filter)
    val rot = transform.rotNorm
    val swapped = rot == 90 || rot == 270
    // Black backing (never the cream mat) so a not-yet-decoded photo shows black, not a white
    // flash; clipToBounds so the per-photo zoom / Ken-Burns / rotation never spills over the frame.
    BoxWithConstraints(Modifier.fillMaxSize().clipToBounds().background(bg), contentAlignment = Alignment.Center) {
        // When rotated a quarter turn, lay the image out with width/height swapped so that after the
        // 90°/270° turn it lands back filling the frame (a portrait rotated to landscape still fills).
        val w = if (swapped) maxHeight else maxWidth
        val h = if (swapped) maxWidth else maxHeight
        // requiredSize, not size: the parent clamps a plain size() to its own constraints, which
        // would squash the swapped (tall) layout back to a square before the quarter turn.
        fun imgMod(extraScale: Float = 1f) = Modifier.requiredSize(w, h).graphicsLayer {
            rotationZ = rot.toFloat()
            val s = transform.scale * kb() * extraScale
            scaleX = s; scaleY = s
            // Pan is applied in screen space (after rotation) so the controls stay intuitive.
            translationX = transform.offX * size.width
            translationY = transform.offY * size.height
        }
        // A hand-made crop (studio zoom/pan) is the user's framing; otherwise aim the crop at the faces.
        val handCropped = transform.scale > 1.001f || kotlin.math.abs(transform.offX) > 0.001f || kotlin.math.abs(transform.offY) > 0.001f
        val cropAlign = if (handCropped) Alignment.Center else focusAlignment(focus, w.value, h.value)
        when (fit) {
            PhotoFit.FILL -> AsyncImage(
                model = rememberSlideModel(file), contentDescription = null, contentScale = ContentScale.Crop, colorFilter = cf,
                alignment = cropAlign, modifier = imgMod(),
            )
            PhotoFit.FIT -> AsyncImage(
                model = rememberSlideModel(file), contentDescription = null, contentScale = ContentScale.Fit, colorFilter = cf, modifier = imgMod(),
            )
            PhotoFit.BLUR -> {
                AsyncImage(
                    model = rememberSlideModel(file), contentDescription = null, contentScale = ContentScale.Crop, colorFilter = cf,
                    modifier = imgMod(1.1f).blur(28.dp),
                )
                AsyncImage(
                    model = rememberSlideModel(file), contentDescription = null, contentScale = ContentScale.Fit, colorFilter = cf, modifier = imgMod(),
                )
            }
        }
        if (filter == PhotoFilter.VIGNETTE) VignetteOverlay()
    }
}

@Composable
internal fun BoxScope.OverlayLayer(
    text: TextOverlay, clock: com.il90.salongallery.net.ClockConfig,
    weather: com.il90.salongallery.net.WeatherConfig = com.il90.salongallery.net.WeatherConfig(),
    weatherNow: com.il90.salongallery.net.WeatherNow = com.il90.salongallery.net.WeatherNow(),
) {
    if (clock.on) {
        // Absolute corners (not start/end) so the arrows match on an RTL/Hebrew screen too.
        val align: Alignment = when (clock.pos) {
            com.il90.salongallery.net.ClockPos.TOP_START -> androidx.compose.ui.AbsoluteAlignment.TopLeft
            com.il90.salongallery.net.ClockPos.TOP_END -> androidx.compose.ui.AbsoluteAlignment.TopRight
            com.il90.salongallery.net.ClockPos.BOTTOM_START -> androidx.compose.ui.AbsoluteAlignment.BottomLeft
            com.il90.salongallery.net.ClockPos.BOTTOM_END -> androidx.compose.ui.AbsoluteAlignment.BottomRight
            com.il90.salongallery.net.ClockPos.CENTER -> Alignment.Center
        }
        val right = clock.pos == com.il90.salongallery.net.ClockPos.TOP_END || clock.pos == com.il90.salongallery.net.ClockPos.BOTTOM_END
        ClockView(
            style = clock.style,
            modifier = Modifier.align(align).safeDrawingPadding().padding(28.dp),
            showDate = clock.showDate,
            alignEnd = right || clock.pos == com.il90.salongallery.net.ClockPos.CENTER,
            size = clock.size,
        )
    }
    if (text.content.isNotBlank()) {
        val align = when (text.pos) {
            TextPos.TOP -> Alignment.TopCenter
            TextPos.CENTER -> Alignment.Center
            TextPos.BOTTOM -> Alignment.BottomCenter
        }
        Text(
            text.content,
            modifier = Modifier.align(align).safeDrawingPadding().padding(horizontal = 24.dp, vertical = 44.dp),
            style = TextStyle(
                fontFamily = overlayFont(text.font),
                fontSize = overlaySize(text.size),
                fontWeight = FontWeight.Bold,
                color = overlayColor(text.color),
                textAlign = TextAlign.Center,
                textDirection = androidx.compose.ui.text.style.TextDirection.Content,
                shadow = Shadow(Color.Black.copy(alpha = 0.7f), Offset(0f, 4f), 16f),
            ),
        )
    }
    if (weather.on && weatherNow.ok) {
        val align: Alignment = when (weather.pos) {
            com.il90.salongallery.net.ClockPos.TOP_START -> androidx.compose.ui.AbsoluteAlignment.TopLeft
            com.il90.salongallery.net.ClockPos.TOP_END -> androidx.compose.ui.AbsoluteAlignment.TopRight
            com.il90.salongallery.net.ClockPos.BOTTOM_START -> androidx.compose.ui.AbsoluteAlignment.BottomLeft
            com.il90.salongallery.net.ClockPos.BOTTOM_END -> androidx.compose.ui.AbsoluteAlignment.BottomRight
            com.il90.salongallery.net.ClockPos.CENTER -> Alignment.Center
        }
        // When the clock and weather share a corner, lift the weather clear of the clock so they
        // stack neatly instead of overlapping.
        val sameCorner = clock.on && weather.pos == clock.pos
        val lift = if (sameCorner) clockStackHeight(clock.size) else 0.dp
        val isTop = weather.pos == com.il90.salongallery.net.ClockPos.TOP_START || weather.pos == com.il90.salongallery.net.ClockPos.TOP_END
        WeatherBadge(
            weather, weatherNow,
            Modifier.align(align).safeDrawingPadding().padding(
                start = 28.dp, end = 28.dp,
                top = 28.dp + (if (sameCorner && isTop) lift else 0.dp),
                bottom = 28.dp + (if (sameCorner && !isTop) lift else 0.dp),
            ),
        )
    }
}

/** Roughly how tall the clock block is, so a co-located weather badge can clear it. */
private fun clockStackHeight(size: String): androidx.compose.ui.unit.Dp = when (size) {
    "s" -> 54.dp
    "l" -> 104.dp
    else -> 74.dp
}

/** The weather overlay, in one of several styles: pill, minimal, card or stacked. */
@Composable
private fun BoxScope.WeatherBadge(cfg: com.il90.salongallery.net.WeatherConfig, now: com.il90.salongallery.net.WeatherNow, modifier: Modifier) {
    val temp = "${now.temp}°${cfg.units.uppercase()}"
    val glyph = weatherGlyph(now.code)
    val shadow = Shadow(Color.Black.copy(0.6f), Offset(0f, 2f), 10f)
    fun tempStyle(sz: Int) = TextStyle(fontFamily = com.il90.salongallery.ui.theme.Display, fontSize = sz.sp, fontWeight = FontWeight(500), color = Color.White, shadow = shadow)
    fun placeStyle(sz: Int) = TextStyle(fontFamily = com.il90.salongallery.ui.theme.Body, fontSize = sz.sp, fontWeight = FontWeight(500), color = Color.White.copy(0.9f), letterSpacing = 0.4.sp, shadow = shadow)
    when (cfg.style) {
        "minimal" -> Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(glyph, style = TextStyle(fontSize = 30.sp))
            Text(temp, style = tempStyle(30))
        }
        "stacked" -> Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(glyph, style = TextStyle(fontSize = 40.sp))
            Text(temp, style = tempStyle(30))
            if (cfg.place.isNotBlank()) Text(cfg.place, style = placeStyle(14))
        }
        "card" -> Row(
            modifier.clip(RoundedCornerShape(18.dp))
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.32f))))
                .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(18.dp))
                .padding(horizontal = 22.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(glyph, style = TextStyle(fontSize = 42.sp))
            Column {
                Text(temp, style = tempStyle(36))
                if (cfg.place.isNotBlank()) Text(cfg.place, style = placeStyle(15))
            }
        }
        else -> Row(   // "pill"
            modifier.clip(RoundedCornerShape(50)).background(Color.Black.copy(alpha = 0.42f))
                .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(50))
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(glyph, style = TextStyle(fontSize = 34.sp))
            Column {
                Text(temp, style = tempStyle(34))
                if (cfg.place.isNotBlank()) Text(cfg.place, style = placeStyle(15))
            }
        }
    }
}

/** An emoji for a WMO weather code (Open-Meteo's current.weather_code). */
private fun weatherGlyph(code: Int): String = when (code) {
    0 -> "☀️"
    1, 2 -> "🌤️"
    3 -> "☁️"
    45, 48 -> "🌫️"
    in 51..57 -> "🌦️"
    in 61..67 -> "🌧️"
    in 71..77 -> "❄️"
    in 80..82 -> "🌧️"
    in 85..86 -> "🌨️"
    in 95..99 -> "⛈️"
    else -> "🌡️"
}

/** The font a text overlay uses. */
private fun overlayFont(f: String): androidx.compose.ui.text.font.FontFamily = when (f.lowercase()) {
    "modern" -> com.il90.salongallery.ui.theme.Display
    "mono" -> androidx.compose.ui.text.font.FontFamily.Monospace
    "elegant" -> com.il90.salongallery.ui.theme.Body
    "rounded" -> androidx.compose.ui.text.font.FontFamily.SansSerif
    else -> com.il90.salongallery.ui.theme.ContentFont
}

/** A quiet rotating headline banner, configurable (position / image / source / summary). */
@Composable
internal fun BoxScope.RssTicker(items: List<com.il90.salongallery.net.RssItem>, config: com.il90.salongallery.net.RssConfig) {
    var idx by remember(items) { mutableIntStateOf(0) }
    LaunchedEffect(items) { while (items.isNotEmpty()) { delay(8000); idx = (idx + 1) % items.size } }
    val item = items[idx.coerceIn(0, items.lastIndex)]
    val top = config.pos == "top"
    val grad = if (top) listOf(Color.Black.copy(alpha = 0.78f), Color.Transparent)
        else listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f))
    Box(
        Modifier.align(if (top) Alignment.TopCenter else Alignment.BottomCenter).fillMaxWidth()
            .background(Brush.verticalGradient(grad))
            .safeDrawingPadding().padding(horizontal = 30.dp, vertical = 20.dp),
    ) {
        AnimatedContent(
            targetState = item,
            transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(500)) },
            label = "rss",
        ) { it ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (config.showImage && it.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = it.imageUrl, contentDescription = null, contentScale = ContentScale.Crop,
                        modifier = Modifier.size(72.dp).clip(RoundedCornerShape(10.dp)),
                    )
                    Spacer(Modifier.width(16.dp))
                } else {
                    Box(Modifier.size(7.dp).clip(RoundedCornerShape(50)).background(NeonCyan))
                    Spacer(Modifier.width(12.dp))
                }
                Column {
                    if (config.showSource) Text(
                        it.source.uppercase(),
                        style = TextStyle(fontFamily = com.il90.salongallery.ui.theme.Body, fontSize = 11.sp, fontWeight = FontWeight(700), color = NeonCyan, letterSpacing = 1.sp),
                    )
                    Text(
                        it.title,
                        style = TextStyle(
                            fontFamily = com.il90.salongallery.ui.theme.ContentFont, fontSize = 22.sp, fontWeight = FontWeight(600),
                            color = Color.White, lineHeight = 26.sp,
                            textDirection = androidx.compose.ui.text.style.TextDirection.Content,
                            shadow = Shadow(Color.Black.copy(0.7f), Offset(0f, 2f), 12f),
                        ),
                        maxLines = 2,
                    )
                    if (config.showSummary && it.summary.isNotBlank()) Text(
                        it.summary,
                        style = TextStyle(
                            fontFamily = com.il90.salongallery.ui.theme.ContentFont, fontSize = 15.sp, fontWeight = FontWeight(400),
                            color = Color.White.copy(0.82f), lineHeight = 19.sp,
                            textDirection = androidx.compose.ui.text.style.TextDirection.Content,
                            shadow = Shadow(Color.Black.copy(0.7f), Offset(0f, 2f), 10f),
                        ),
                        maxLines = 2,
                    )
                }
            }
        }
    }
}

@Composable
private fun ClockView(
    style: com.il90.salongallery.net.ClockStyle,
    modifier: Modifier,
    showDate: Boolean = true,
    alignEnd: Boolean = false,
    tint: Color = Color.White,
    size: String = "m",
) {
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) { while (true) { now = System.currentTimeMillis(); delay(1000) } }
    val time = remember(now / 60000) { java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(now)) }
    val date = remember(now / 3600000) { java.text.SimpleDateFormat("EEE, d MMM", java.util.Locale.getDefault()).format(java.util.Date(now)) }
    val shadow = Shadow(Color.Black.copy(0.6f), Offset(0f, 3f), 18f)
    val dateShadow = Shadow(Color.Black.copy(0.6f), Offset(0f, 2f), 12f)
    val k = when (size.lowercase()) { "s" -> 0.62f; "l" -> 1.6f; else -> 1f }
    @Composable fun timeText(ts: TextStyle) = Text(time, style = ts)
    Column(modifier, horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        when (style) {
            com.il90.salongallery.net.ClockStyle.ANALOG ->
                AnalogClock(now, tint, Modifier.size(156.dp * k))
            com.il90.salongallery.net.ClockStyle.MINIMAL ->
                timeText(TextStyle(fontFamily = com.il90.salongallery.ui.theme.ContentFont, fontSize = 74.sp * k, fontWeight = FontWeight(200), color = tint, letterSpacing = 2.sp, shadow = shadow))
            com.il90.salongallery.net.ClockStyle.MONO ->
                timeText(TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 56.sp * k, fontWeight = FontWeight(600), color = tint, letterSpacing = 1.sp, shadow = shadow))
            com.il90.salongallery.net.ClockStyle.BOLD ->
                timeText(TextStyle(fontFamily = com.il90.salongallery.ui.theme.Display, fontSize = 92.sp * k, fontWeight = FontWeight(800), color = tint, shadow = shadow))
            com.il90.salongallery.net.ClockStyle.LED ->
                timeText(TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 70.sp * k, fontWeight = FontWeight(700), color = Color(0xFF6FE8C7), letterSpacing = 3.sp, shadow = Shadow(Color(0xFF2BBfa0).copy(0.9f), Offset(0f, 0f), 24f)))
            com.il90.salongallery.net.ClockStyle.CARD ->
                Box(Modifier.clip(RoundedCornerShape(18.dp)).background(Color.Black.copy(0.4f)).border(1.dp, Color.White.copy(0.14f), RoundedCornerShape(18.dp)).padding(horizontal = 22.dp * k.coerceAtMost(1.4f), vertical = 12.dp * k.coerceAtMost(1.4f))) {
                    timeText(TextStyle(fontFamily = com.il90.salongallery.ui.theme.Display, fontSize = 56.sp * k, fontWeight = FontWeight(500), color = tint))
                }
            else ->
                timeText(TextStyle(fontFamily = com.il90.salongallery.ui.theme.Display, fontSize = 58.sp * k, fontWeight = FontWeight(400), color = tint, shadow = shadow))
        }
        if (showDate) {
            if (style == com.il90.salongallery.net.ClockStyle.ANALOG) Spacer(Modifier.height(10.dp))
            Text(date, style = TextStyle(fontFamily = com.il90.salongallery.ui.theme.Body, fontSize = 18.sp * k.coerceAtMost(1.3f), fontWeight = FontWeight(500), color = tint.copy(0.92f), letterSpacing = 0.5.sp, shadow = dateShadow))
        }
    }
}

@Composable
private fun AnalogClock(timeMs: Long, tint: Color, modifier: Modifier) {
    val cal = remember(timeMs / 1000) { java.util.Calendar.getInstance().apply { timeInMillis = timeMs } }
    val h = cal.get(java.util.Calendar.HOUR)
    val m = cal.get(java.util.Calendar.MINUTE)
    val s = cal.get(java.util.Calendar.SECOND)
    androidx.compose.foundation.Canvas(modifier) {
        val r = size.minDimension / 2f
        val c = Offset(size.width / 2f, size.height / 2f)
        drawCircle(Color.Black.copy(0.35f), r, c)
        drawCircle(tint.copy(0.9f), r, c, style = androidx.compose.ui.graphics.drawscope.Stroke(width = r * 0.045f))
        for (i in 0 until 12) {
            val a = Math.toRadians(i * 30.0)
            val outer = c + Offset((r * 0.86f * Math.sin(a)).toFloat(), (-r * 0.86f * Math.cos(a)).toFloat())
            val inner = c + Offset((r * 0.73f * Math.sin(a)).toFloat(), (-r * 0.73f * Math.cos(a)).toFloat())
            drawLine(tint.copy(0.8f), inner, outer, strokeWidth = r * 0.03f)
        }
        fun hand(angleDeg: Double, len: Float, w: Float, color: Color) {
            val a = Math.toRadians(angleDeg)
            val end = c + Offset((len * Math.sin(a)).toFloat(), (-len * Math.cos(a)).toFloat())
            drawLine(color, c, end, strokeWidth = w, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        }
        hand((h % 12 + m / 60.0) * 30.0, r * 0.5f, r * 0.065f, tint)
        hand((m + s / 60.0) * 6.0, r * 0.72f, r * 0.045f, tint)
        hand(s * 6.0, r * 0.8f, r * 0.02f, Color(0xFFE0A857))
        drawCircle(tint, r * 0.05f, c)
    }
}

private fun overlaySize(s: String) = when (s.lowercase()) { "s" -> 32.sp; "l" -> 82.sp; else -> 54.sp }
private fun overlayColor(c: String) = when (c.lowercase()) {
    "black" -> Color(0xFF000000)
    "gold" -> Color(0xFFD9BE8B)
    "cyan" -> Color(0xFF22D3EE)
    "violet" -> Color(0xFFA78BFA)
    else -> Color.White
}

/**
 * A text field tuned for Android TV: focusing it with the D-pad only highlights it (no
 * keyboard); pressing OK opens the keyboard to edit. This lets the D-pad flow through the
 * settings without a keyboard popping up on every field.
 */
@Composable
private fun TvTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    colors: TextFieldColors,
    modifier: Modifier = Modifier,
    password: Boolean = false,
) {
    var editing by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = !editing,
        singleLine = true,
        textStyle = TextStyle(fontFamily = com.il90.salongallery.ui.theme.ContentFont),
        placeholder = { Text(placeholder, color = TextTertiary) },
        keyboardOptions = if (password) KeyboardOptions(keyboardType = KeyboardType.NumberPassword) else KeyboardOptions.Default,
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardActions = KeyboardActions(onDone = { editing = false; keyboard?.hide() }),
        colors = colors,
        modifier = modifier
            .onFocusChanged { if (!it.isFocused) editing = false }
            .onPreviewKeyEvent { e ->
                if (!editing && e.type == KeyEventType.KeyUp &&
                    (e.key == Key.DirectionCenter || e.key == Key.Enter || e.key == Key.NumPadEnter)) {
                    editing = true; keyboard?.show(); true
                } else false
            },
    )
}

/**
 * Open the device's screensaver settings. `ACTION_DREAM_SETTINGS` is missing on many Android TV /
 * Google TV builds, so try the known TV component too, and if nothing opens, fall back to the main
 * Settings and tell the user where to go — never fail silently.
 */
private fun openScreensaverSettings(context: android.content.Context) {
    val direct = listOf(
        android.content.Intent(android.provider.Settings.ACTION_DREAM_SETTINGS),
        android.content.Intent().setClassName(
            "com.android.tv.settings",
            "com.android.tv.settings.device.display.daydream.DaydreamActivity",
        ),
    )
    for (intent in direct) {
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        try { context.startActivity(intent); return } catch (_: Exception) {}
    }
    // Couldn't jump straight to the screensaver page — open main Settings and guide the user.
    android.widget.Toast.makeText(
        context,
        context.getString(R.string.screensaver_manual),
        android.widget.Toast.LENGTH_LONG,
    ).show()
    try {
        context.startActivity(
            android.content.Intent(android.provider.Settings.ACTION_SETTINGS)
                .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    } catch (_: Exception) {}
}

@Composable
private fun NetChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        Modifier.clip(RoundedCornerShape(50)).background(Color(0xFF14141F)).padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Icon(icon, null, tint = NeonCyan, modifier = Modifier.size(15.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
    }
}

/** (ssid, ip) for the current Wi-Fi connection, for display on the waiting screen. */
private fun networkInfo(context: android.content.Context): Pair<String?, String?> {
    fun clean(raw: String?): String? = raw?.trim('"')?.takeIf { it.isNotBlank() && !it.contains("unknown", true) && it != "0x" && it != "<unknown ssid>" }
    val ssid = runCatching {
        // Android 12+ prefers the network-capabilities path; fall back to the (deprecated) WifiManager.
        val cm = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
        val fromCaps = if (android.os.Build.VERSION.SDK_INT >= 31) {
            val caps = cm?.getNetworkCapabilities(cm.activeNetwork)
            (caps?.transportInfo as? android.net.wifi.WifiInfo)?.ssid?.let { clean(it) }
        } else null
        fromCaps ?: run {
            val wm = context.applicationContext.getSystemService(android.content.Context.WIFI_SERVICE) as? android.net.wifi.WifiManager
            @Suppress("DEPRECATION") clean(wm?.connectionInfo?.ssid)
        }
    }.getOrNull()
    val ip = runCatching {
        java.net.NetworkInterface.getNetworkInterfaces().toList().flatMap { it.inetAddresses.toList() }
            .firstOrNull { !it.isLoopbackAddress && it is java.net.Inet4Address }?.hostAddress
    }.getOrNull()
    return ssid to ip
}

/** A warm full-screen "Receiving photos…" cover shown while a batch streams in, so the wall never
 *  flashes black mid-upload. */
@Composable
private fun ReceivingOverlay(count: Int) {
    SalonBackground {
        Column(
            Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LogoChip(size = 76, icon = Icons.Outlined.PhotoLibrary)
            Spacer(Modifier.height(22.dp))
            CircularProgressIndicator(color = NeonCyan, strokeWidth = 3.dp, modifier = Modifier.size(34.dp))
            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.screen_receiving), style = MaterialTheme.typography.headlineSmall, color = TextPrimary, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.screen_receiving_count, count), style = MaterialTheme.typography.titleMedium, color = NeonCyan)
        }
    }
}

@Composable
private fun WaitingToPair(
    deviceName: String,
    running: Boolean,
    ssid: String? = null,
    address: String? = null,
    version: String = "",
    availableVersion: String? = null,
    checking: Boolean = false,
    onCheckUpdate: (() -> Unit)? = null,
    onChangeRole: (() -> Unit)? = null,
) {
    val focus = remember { androidx.compose.ui.focus.FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    SalonBackground {
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp)
                // OK / center on the TV remote checks for an app update (and shows the version below).
                .focusRequester(focus).focusable()
                .onKeyEvent { ev ->
                    if (ev.type == KeyEventType.KeyDown &&
                        (ev.key == Key.DirectionCenter || ev.key == Key.Enter || ev.key == Key.Menu)
                    ) { onCheckUpdate?.invoke(); true } else false
                },
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
            if (ssid != null || address != null) {
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    ssid?.let { NetChip(Icons.Outlined.Wifi, it) }
                    address?.let { NetChip(Icons.Outlined.Lan, it) }
                }
            }
            Spacer(Modifier.weight(1f))
            // Escape hatch: this device is a Display with no on-screen controls, so if it has no
            // Remote paired (or was set to Display by mistake) this is the only way back to the role
            // chooser without clearing app data. Only on the waiting screen — never over the art.
            onChangeRole?.let {
                TextButton(onClick = it) {
                    Icon(Icons.Outlined.SwapHoriz, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.change_role), style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                }
            }
            Spacer(Modifier.height(6.dp))
            // Version + one-tap update (press OK on the remote to re-check).
            val verText = when {
                checking -> stringResource(R.string.update_checking)
                availableVersion != null -> stringResource(R.string.update_available, availableVersion)
                else -> stringResource(R.string.screen_version, version)
            }
            Text(
                verText,
                style = MaterialTheme.typography.labelMedium,
                color = if (availableVersion != null) NeonCyan else TextSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.screen_update_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary.copy(alpha = 0.5f), textAlign = TextAlign.Center,
            )
        }
    }
}
