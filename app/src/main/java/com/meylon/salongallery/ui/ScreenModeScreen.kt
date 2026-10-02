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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.Lan
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Shadow
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
import com.meylon.salongallery.R
import com.meylon.salongallery.net.DisplayMode
import com.meylon.salongallery.net.isVideoName
import com.meylon.salongallery.net.PhotoFit
import com.meylon.salongallery.net.PhotoFilter
import com.meylon.salongallery.net.PhotoTransform
import com.meylon.salongallery.net.ScreenOrientation
import com.meylon.salongallery.net.SlideEffect
import com.meylon.salongallery.net.ScreenSessionHolder
import com.meylon.salongallery.net.TextOverlay
import com.meylon.salongallery.net.TextPos
import com.meylon.salongallery.ui.components.GradientButton
import com.meylon.salongallery.ui.components.LogoChip
import com.meylon.salongallery.ui.components.OutlineButton
import com.meylon.salongallery.ui.components.SalonBackground
import com.meylon.salongallery.ui.components.SectionLabel
import com.meylon.salongallery.net.ScreenSession
import com.meylon.salongallery.net.DisplayPrefs
import com.meylon.salongallery.ui.theme.ElecBorder
import com.meylon.salongallery.ui.theme.ElecBg
import com.meylon.salongallery.ui.theme.ElecSurface
import com.meylon.salongallery.ui.theme.NeonCyan
import com.meylon.salongallery.ui.theme.TextPrimary
import com.meylon.salongallery.ui.theme.TextSecondary
import com.meylon.salongallery.ui.theme.TextTertiary
import kotlinx.coroutines.delay
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
    val photoFilter by session.photoFilter.collectAsStateWithLifecycle()
    val collage by session.collage.collectAsStateWithLifecycle()
    val textOverlay by session.textOverlay.collectAsStateWithLifecycle()
    val clock by session.clock.collectAsStateWithLifecycle()
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

    // Auto-sleep schedule: re-evaluate every 30s.
    var sleeping by remember { mutableStateOf(session.prefs.isSleepingNow()) }
    LaunchedEffect(Unit) {
        while (true) { sleeping = session.prefs.isSleepingNow(); delay(30_000) }
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
    var rssItems by remember { mutableStateOf<List<com.meylon.salongallery.net.RssItem>>(emptyList()) }
    LaunchedEffect(rssOn, rssFeeds) {
        if (!rssOn || rssFeeds.isEmpty()) { rssItems = emptyList(); return@LaunchedEffect }
        while (true) {
            rssItems = com.meylon.salongallery.net.RssFeed.fetch(rssFeeds)
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
            .background(Color.Black)
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
                        filter = photoFilter,
                        volume = volume,
                        collageOn = collage,
                        orientationMap = orientationMap,
                        transformOf = { session.transformFor(it.name) },
                        durationOf = { session.durationFor(it.name) },
                        onNext = { session.currentIndex.value = it },
                    )
                }

            else -> {
                val net = remember(running) { networkInfo(context) }
                WaitingToPair(
                    deviceName = session.effectiveName(),
                    running = running,
                    ssid = net.first,
                    address = net.second,
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
                    style = TextStyle(fontFamily = com.meylon.salongallery.ui.theme.ContentFont, fontSize = 18.sp, color = Color.White),
                )
            }
        }

        if (mode != DisplayMode.WAITING) OverlayLayer(textOverlay, clock)

        if (mode != DisplayMode.WAITING && rssOn && rssItems.isNotEmpty()) {
            RssTicker(rssItems, rssConfig)
        }

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
    filter: PhotoFilter,
    volume: Float,
    collageOn: Boolean,
    orientationMap: Map<String, Boolean>,
    transformOf: (File) -> PhotoTransform,
    durationOf: (File) -> Int,
    onNext: (Int) -> Unit,
) {
    if (files.isEmpty()) return
    val idx = currentIndex.coerceIn(0, files.size - 1)
    val currentIsVideo = isVideoName(files[idx].name)
    val screenLandscape = androidx.compose.ui.platform.LocalConfiguration.current.let { it.screenWidthDp >= it.screenHeightDp }

    // Which items (by index) form the slide starting at [from]: a collage of same-orientation
    // photos that would otherwise leave big side gaps, or just the single item.
    fun membersAt(from: Int): List<Int> {
        if (!collageOn || files.size < 2) return listOf(from)
        val f = files[from]
        if (isVideoName(f.name)) return listOf(from)
        val portrait = orientationMap[f.name] ?: return listOf(from)
        // Fillable when the photo's orientation is opposite the screen's (big side gaps).
        val fillable = portrait == screenLandscape
        if (!fillable) return listOf(from)
        val want = if (screenLandscape) 3 else 2
        val out = mutableListOf(from)
        var j = from
        while (out.size < want) {
            j = (j + 1) % files.size
            if (j == from) break
            val nf = files[j]
            if (!isVideoName(nf.name) && orientationMap[nf.name] == portrait) out.add(j) else break
        }
        return if (out.size >= 2) out else listOf(from)
    }

    fun advanceFrom(from: Int) {
        if (files.size <= 1) return
        val next = if (shuffle) (files.indices - from).randomOrNull() ?: from
        else (from + membersAt(from).size) % files.size
        onNext(next)
    }

    // Still photos advance on their own duration (or the slideshow default); videos advance when they end.
    LaunchedEffect(idx, shuffle, intervalMs, files.size, currentIsVideo, collageOn) {
        if (currentIsVideo || files.size <= 1) return@LaunchedEffect
        val sec = durationOf(files[idx])
        delay(if (sec > 0) sec * 1000L else intervalMs)
        advanceFrom(idx)
    }
    // Warm the next photo into Coil's cache so the transition doesn't flash before it decodes.
    val ctx = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(idx, files.size) {
        if (files.size <= 1) return@LaunchedEffect
        val next = files[(idx + 1) % files.size]
        if (!isVideoName(next.name)) {
            coil.Coil.imageLoader(ctx).enqueue(
                coil.request.ImageRequest.Builder(ctx).data(next).build()
            )
        }
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
        } else if (members.size >= 2) {
            CollageSlide(members.map { files[it] }, filter = eff, horizontal = screenLandscape)
        } else {
            val kb = if (effAt(i) == SlideEffect.KENBURNS) {
                val a = remember(i) { Animatable(1f) }
                LaunchedEffect(i) { a.animateTo(1.14f, tween(intervalMs.toInt(), easing = LinearEasing)) }
                a
            } else null
            PhotoContent(file, fit, transformOf(file), eff) { kb?.value ?: 1f }
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
@Composable
private fun CollageSlide(files: List<File>, filter: PhotoFilter, horizontal: Boolean) {
    val cf = lookFilter(filter)
    val mat = Color(0xFFEBE4D7)      // warm gallery mat
    val gap = 12.dp
    Box(Modifier.fillMaxSize().background(mat).padding(gap)) {
        if (horizontal) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                files.forEach { f -> CollageCell(f, cf, Modifier.weight(1f).fillMaxHeight()) }
            }
        } else {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(gap)) {
                files.forEach { f -> CollageCell(f, cf, Modifier.weight(1f).fillMaxWidth()) }
            }
        }
        if (filter == PhotoFilter.VIGNETTE) VignetteOverlay()
    }
}

@Composable
private fun CollageCell(f: File, cf: androidx.compose.ui.graphics.ColorFilter?, modifier: Modifier) {
    Box(modifier.clipToBounds()) {
        AsyncImage(
            model = f, contentDescription = null, contentScale = ContentScale.Crop, colorFilter = cf,
            modifier = Modifier.fillMaxSize(),
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
    DisposableEffect(file.path) {
        val l = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED && !loop) finishOnce()
            }
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

/** Renders one photo, applying its studio [transform], a [filter] look and an optional Ken-Burns [kb] zoom. */
@Composable
fun PhotoContent(file: File, fit: PhotoFit, transform: PhotoTransform, filter: PhotoFilter = PhotoFilter.NONE, kb: () -> Float = { 1f }) {
    val cf = lookFilter(filter)
    val cropMod = Modifier.fillMaxSize().graphicsLayer {
        val s = transform.scale * kb()
        scaleX = s; scaleY = s
        translationX = transform.offX * size.width
        translationY = transform.offY * size.height
    }
    // Black backing (never the cream mat) so a not-yet-decoded photo shows black, not a white
    // flash; clipToBounds so the per-photo zoom / Ken-Burns never spills over the frame.
    Box(Modifier.fillMaxSize().clipToBounds().background(Color.Black)) {
        when (fit) {
            PhotoFit.FILL -> AsyncImage(
                model = file, contentDescription = null, contentScale = ContentScale.Crop, colorFilter = cf, modifier = cropMod,
            )
            PhotoFit.FIT -> AsyncImage(
                model = file, contentDescription = null, contentScale = ContentScale.Fit, colorFilter = cf, modifier = cropMod,
            )
            PhotoFit.BLUR -> Box(Modifier.fillMaxSize()) {
                AsyncImage(
                    model = file, contentDescription = null, contentScale = ContentScale.Crop, colorFilter = cf,
                    modifier = Modifier.fillMaxSize().blur(28.dp).graphicsLayer { scaleX = 1.1f; scaleY = 1.1f },
                )
                AsyncImage(
                    model = file, contentDescription = null, contentScale = ContentScale.Fit, colorFilter = cf, modifier = cropMod,
                )
            }
        }
        if (filter == PhotoFilter.VIGNETTE) VignetteOverlay()
    }
}

@Composable
internal fun BoxScope.OverlayLayer(text: TextOverlay, clock: com.meylon.salongallery.net.ClockConfig) {
    if (clock.on) {
        // Absolute corners (not start/end) so the arrows match on an RTL/Hebrew screen too.
        val align: Alignment = when (clock.pos) {
            com.meylon.salongallery.net.ClockPos.TOP_START -> androidx.compose.ui.AbsoluteAlignment.TopLeft
            com.meylon.salongallery.net.ClockPos.TOP_END -> androidx.compose.ui.AbsoluteAlignment.TopRight
            com.meylon.salongallery.net.ClockPos.BOTTOM_START -> androidx.compose.ui.AbsoluteAlignment.BottomLeft
            com.meylon.salongallery.net.ClockPos.BOTTOM_END -> androidx.compose.ui.AbsoluteAlignment.BottomRight
            com.meylon.salongallery.net.ClockPos.CENTER -> Alignment.Center
        }
        val right = clock.pos == com.meylon.salongallery.net.ClockPos.TOP_END || clock.pos == com.meylon.salongallery.net.ClockPos.BOTTOM_END
        ClockView(
            style = clock.style,
            modifier = Modifier.align(align).safeDrawingPadding().padding(28.dp),
            showDate = clock.showDate,
            alignEnd = right || clock.pos == com.meylon.salongallery.net.ClockPos.CENTER,
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
                fontFamily = com.meylon.salongallery.ui.theme.ContentFont,
                fontSize = overlaySize(text.size),
                fontWeight = FontWeight.Bold,
                color = overlayColor(text.color),
                textAlign = TextAlign.Center,
                textDirection = androidx.compose.ui.text.style.TextDirection.Content,
                shadow = Shadow(Color.Black.copy(alpha = 0.7f), Offset(0f, 4f), 16f),
            ),
        )
    }
}

/** A quiet rotating headline banner, configurable (position / image / source / summary). */
@Composable
internal fun BoxScope.RssTicker(items: List<com.meylon.salongallery.net.RssItem>, config: com.meylon.salongallery.net.RssConfig) {
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
                        style = TextStyle(fontFamily = com.meylon.salongallery.ui.theme.Body, fontSize = 11.sp, fontWeight = FontWeight(700), color = NeonCyan, letterSpacing = 1.sp),
                    )
                    Text(
                        it.title,
                        style = TextStyle(
                            fontFamily = com.meylon.salongallery.ui.theme.ContentFont, fontSize = 22.sp, fontWeight = FontWeight(600),
                            color = Color.White, lineHeight = 26.sp,
                            textDirection = androidx.compose.ui.text.style.TextDirection.Content,
                            shadow = Shadow(Color.Black.copy(0.7f), Offset(0f, 2f), 12f),
                        ),
                        maxLines = 2,
                    )
                    if (config.showSummary && it.summary.isNotBlank()) Text(
                        it.summary,
                        style = TextStyle(
                            fontFamily = com.meylon.salongallery.ui.theme.ContentFont, fontSize = 15.sp, fontWeight = FontWeight(400),
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
    style: com.meylon.salongallery.net.ClockStyle,
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
    Column(modifier, horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        when (style) {
            com.meylon.salongallery.net.ClockStyle.ANALOG ->
                AnalogClock(now, tint, Modifier.size(156.dp * k))
            com.meylon.salongallery.net.ClockStyle.MINIMAL ->
                Text(time, style = TextStyle(fontFamily = com.meylon.salongallery.ui.theme.ContentFont, fontSize = 74.sp * k, fontWeight = FontWeight(200), color = tint, letterSpacing = 2.sp, shadow = shadow))
            com.meylon.salongallery.net.ClockStyle.MONO ->
                Text(time, style = TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 56.sp * k, fontWeight = FontWeight(600), color = tint, letterSpacing = 1.sp, shadow = shadow))
            else ->
                Text(time, style = TextStyle(fontFamily = com.meylon.salongallery.ui.theme.Display, fontSize = 58.sp * k, fontWeight = FontWeight(400), color = tint, shadow = shadow))
        }
        if (showDate) {
            if (style == com.meylon.salongallery.net.ClockStyle.ANALOG) Spacer(Modifier.height(10.dp))
            Text(date, style = TextStyle(fontFamily = com.meylon.salongallery.ui.theme.Body, fontSize = 18.sp * k.coerceAtMost(1.3f), fontWeight = FontWeight(500), color = tint.copy(0.92f), letterSpacing = 0.5.sp, shadow = dateShadow))
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
        textStyle = TextStyle(fontFamily = com.meylon.salongallery.ui.theme.ContentFont),
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
    val ssid = runCatching {
        val wm = context.applicationContext.getSystemService(android.content.Context.WIFI_SERVICE) as? android.net.wifi.WifiManager
        wm?.connectionInfo?.ssid?.trim('"')?.takeIf { it.isNotBlank() && !it.contains("unknown", true) && it != "0x" }
    }.getOrNull()
    val ip = runCatching {
        java.net.NetworkInterface.getNetworkInterfaces().toList().flatMap { it.inetAddresses.toList() }
            .firstOrNull { !it.isLoopbackAddress && it is java.net.Inet4Address }?.hostAddress
    }.getOrNull()
    return ssid to ip
}

@Composable
private fun WaitingToPair(
    deviceName: String,
    running: Boolean,
    ssid: String? = null,
    address: String? = null,
    onChangeRole: (() -> Unit)? = null,
) {
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
            if (ssid != null || address != null) {
                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    ssid?.let { NetChip(Icons.Outlined.Wifi, it) }
                    address?.let { NetChip(Icons.Outlined.Lan, it) }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.screen_remote_hint),
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary, textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
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
        }
    }
}
