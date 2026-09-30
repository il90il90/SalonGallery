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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tv
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
import androidx.compose.ui.geometry.Offset
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
import com.meylon.salongallery.net.PhotoFit
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
    val intervalMs by session.intervalMs.collectAsStateWithLifecycle()
    val shuffle by session.shuffle.collectAsStateWithLifecycle()
    val effect by session.effect.collectAsStateWithLifecycle()
    val photoFit by session.photoFit.collectAsStateWithLifecycle()
    val textOverlay by session.textOverlay.collectAsStateWithLifecycle()
    val clockOn by session.clockOn.collectAsStateWithLifecycle()
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

    var showSettings by remember { mutableStateOf(false) }
    var showPinPrompt by remember { mutableStateOf(false) }
    // Android TV: keep D-pad focus on the display so the remote's OK button opens settings.
    val rootFocus = remember { androidx.compose.ui.focus.FocusRequester() }
    LaunchedEffect(showSettings, showPinPrompt) {
        if (!showSettings && !showPinPrompt) runCatching { rootFocus.requestFocus() }
    }

    // Auto-sleep schedule: re-evaluate every 30s.
    var sleeping by remember { mutableStateOf(session.prefs.isSleepingNow()) }
    LaunchedEffect(Unit) {
        while (true) { sleeping = session.prefs.isSleepingNow(); delay(30_000) }
    }

    fun openSettings() { if (session.prefs.hasPin) showPinPrompt = true else showSettings = true }

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
    LaunchedEffect(volume) { musicExo.volume = volume; exo.volume = volume }
    LaunchedEffect(musicShuffle) { musicExo.shuffleModeEnabled = musicShuffle }
    LaunchedEffect(musicNext) { if (musicNext > 0 && musicExo.mediaItemCount > 0) musicExo.seekToNext() }
    LaunchedEffect(musicPrev) { if (musicPrev > 0 && musicExo.mediaItemCount > 0) musicExo.seekToPrevious() }

    // Brightness is applied as a software dimming scrim (see below) so it works like real
    // picture brightness on every device, including Android TV where window brightness is ignored.
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

    val files = remember(libraryVersion) { session.activeFiles() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(rootFocus)
            .focusable()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { if (sleeping) sleeping = false else openSettings() },
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
                        transformOf = { session.transformFor(it.name) },
                        onNext = { session.currentIndex.value = it },
                    )
                }

            else -> WaitingToPair(deviceName = deviceName, running = running)
        }

        if (mode != DisplayMode.WAITING) OverlayLayer(textOverlay, clockOn)

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
                Box(Modifier.graphicsLayer { alpha = 0.28f }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ClockText(Modifier)
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

    if (showSettings) {
        val w = remember { context.resources.displayMetrics.widthPixels }
        val h = remember { context.resources.displayMetrics.heightPixels }
        val stat = remember { runCatching { android.os.StatFs(context.filesDir.path) }.getOrNull() }
        SettingsSheet(
            actions = actions,
            onDismiss = { showSettings = false },
            extra = {
                Spacer(Modifier.height(16.dp))
                ScreenAdminContent(session)
                Spacer(Modifier.height(20.dp))
                ScreenInfoContent(
                    widthPx = w, heightPx = h,
                    freeBytes = stat?.availableBytes ?: 0L,
                    totalBytes = stat?.totalBytes ?: 0L,
                    photoCount = files.size,
                )
            },
        )
    }

    if (showPinPrompt) {
        PinPromptDialog(
            correctPin = session.prefs.pin,
            onSuccess = { showPinPrompt = false; showSettings = true },
            onDismiss = { showPinPrompt = false },
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
    transformOf: (File) -> PhotoTransform,
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
        val kb = if (effect == SlideEffect.KENBURNS) {
            val a = remember(i) { Animatable(1f) }
            LaunchedEffect(i) { a.animateTo(1.14f, tween(intervalMs.toInt(), easing = LinearEasing)) }
            a
        } else null
        PhotoContent(file, fit, transformOf(file)) { kb?.value ?: 1f }
    }
}

/** Renders one photo, applying its studio [transform] and an optional Ken-Burns [kb] zoom. */
@Composable
fun PhotoContent(file: File, fit: PhotoFit, transform: PhotoTransform, kb: () -> Float = { 1f }) {
    val cropMod = Modifier.fillMaxSize().graphicsLayer {
        val s = transform.scale * kb()
        scaleX = s; scaleY = s
        translationX = transform.offX * size.width
        translationY = transform.offY * size.height
    }
    when (fit) {
        PhotoFit.FILL -> AsyncImage(
            model = file, contentDescription = null, contentScale = ContentScale.Crop, modifier = cropMod,
        )
        PhotoFit.FIT -> AsyncImage(
            model = file, contentDescription = null, contentScale = ContentScale.Fit, modifier = cropMod,
        )
        PhotoFit.BLUR -> Box(Modifier.fillMaxSize()) {
            AsyncImage(
                model = file, contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().blur(28.dp).graphicsLayer { scaleX = 1.1f; scaleY = 1.1f },
            )
            AsyncImage(
                model = file, contentDescription = null, contentScale = ContentScale.Fit, modifier = cropMod,
            )
        }
    }
}

@Composable
private fun BoxScope.OverlayLayer(text: TextOverlay, clockOn: Boolean) {
    if (clockOn) ClockText(Modifier.align(Alignment.TopStart).safeDrawingPadding().padding(28.dp))
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
                fontSize = overlaySize(text.size),
                fontWeight = FontWeight.Bold,
                color = overlayColor(text.color),
                textAlign = TextAlign.Center,
                shadow = Shadow(Color.Black.copy(alpha = 0.7f), Offset(0f, 4f), 16f),
            ),
        )
    }
}

@Composable
private fun ClockText(modifier: Modifier) {
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) { while (true) { now = System.currentTimeMillis(); delay(1000) } }
    val time = remember(now / 60000) { java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(now)) }
    val date = remember(now / 3600000) { java.text.SimpleDateFormat("EEE, d MMM", java.util.Locale.getDefault()).format(java.util.Date(now)) }
    Column(modifier) {
        Text(time, style = TextStyle(fontFamily = com.meylon.salongallery.ui.theme.Display, fontSize = 58.sp, fontWeight = FontWeight(400), color = Color.White, shadow = Shadow(Color.Black.copy(0.6f), Offset(0f, 3f), 18f)))
        Text(date, style = TextStyle(fontFamily = com.meylon.salongallery.ui.theme.Body, fontSize = 18.sp, fontWeight = FontWeight(500), color = Color.White.copy(0.92f), letterSpacing = 0.5.sp, shadow = Shadow(Color.Black.copy(0.6f), Offset(0f, 2f), 12f)))
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

@Composable
private fun ScreenAdminContent(session: ScreenSession) {
    // Android TV: focus the first control when the settings open so the D-pad can drive it.
    val firstFocus = remember { androidx.compose.ui.focus.FocusRequester() }
    LaunchedEffect(Unit) { runCatching { firstFocus.requestFocus() } }
    var name by remember { mutableStateOf(session.prefs.customName) }
    var savedName by remember { mutableStateOf(false) }
    var pin by remember { mutableStateOf("") }
    var hasPin by remember { mutableStateOf(session.prefs.hasPin) }
    var pinMsg by remember { mutableStateOf<String?>(null) }
    var schedOn by remember { mutableStateOf(session.prefs.scheduleEnabled) }
    var startMin by remember { mutableIntStateOf(session.prefs.sleepStartMin) }
    var endMin by remember { mutableIntStateOf(session.prefs.sleepEndMin) }

    val tfColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
    )

    Column(Modifier.fillMaxWidth()) {
        SectionLabel(stringResource(R.string.admin_section))
        Spacer(Modifier.height(16.dp))

        Text(stringResource(R.string.admin_name), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        TvTextField(
            value = name, onValueChange = { name = it; savedName = false },
            placeholder = stringResource(R.string.admin_name_hint),
            colors = tfColors,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        GradientButton(
            text = if (savedName) stringResource(R.string.admin_saved) else stringResource(R.string.admin_save_name),
            modifier = Modifier.focusRequester(firstFocus),
            onClick = { session.renameDevice(name); savedName = true },
        )

        Spacer(Modifier.height(22.dp))
        Text(stringResource(R.string.admin_pin), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(if (hasPin) R.string.admin_pin_on else R.string.admin_pin_off),
            style = MaterialTheme.typography.bodySmall, color = TextSecondary,
        )
        Spacer(Modifier.height(8.dp))
        TvTextField(
            value = pin, onValueChange = { pin = it.filter { c -> c.isDigit() }.take(6); pinMsg = null },
            placeholder = stringResource(R.string.admin_pin_hint),
            colors = tfColors,
            modifier = Modifier.fillMaxWidth(),
            password = true,
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.weight(1f)) {
                GradientButton(
                    text = stringResource(R.string.admin_save_pin),
                    onClick = {
                        if (pin.length >= 4) { session.prefs.pin = pin; hasPin = true; pin = ""; pinMsg = "PIN saved ✓" }
                        else pinMsg = "Use at least 4 digits"
                    },
                )
            }
            if (hasPin) Box(Modifier.weight(1f)) {
                OutlineButton(
                    text = stringResource(R.string.admin_remove_pin),
                    onClick = { session.prefs.pin = ""; hasPin = false; pin = ""; pinMsg = "PIN removed" },
                )
            }
        }
        pinMsg?.let { Spacer(Modifier.height(8.dp)); Text(it, style = MaterialTheme.typography.labelMedium, color = NeonCyan) }

        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.admin_schedule), style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
            Switch(checked = schedOn, onCheckedChange = { schedOn = it; session.prefs.scheduleEnabled = it })
        }
        if (schedOn) {
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.admin_schedule_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(12.dp))
            TimeStepper(stringResource(R.string.admin_sleep_at), startMin) { startMin = it; session.prefs.sleepStartMin = it }
            Spacer(Modifier.height(10.dp))
            TimeStepper(stringResource(R.string.admin_wake_at), endMin) { endMin = it; session.prefs.sleepEndMin = it }
        }
    }
}

@Composable
private fun TimeStepper(label: String, minutes: Int, onChange: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(ElecSurface).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
        IconButton(onClick = { onChange((minutes - 30 + 1440) % 1440) }) {
            Icon(Icons.Outlined.Remove, contentDescription = "earlier", tint = NeonCyan)
        }
        Text(
            DisplayPrefs.fmt(minutes),
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
            modifier = Modifier.width(74.dp), textAlign = TextAlign.Center,
        )
        IconButton(onClick = { onChange((minutes + 30) % 1440) }) {
            Icon(Icons.Outlined.Add, contentDescription = "later", tint = NeonCyan)
        }
    }
}

@Composable
private fun PinPromptDialog(correctPin: String, onSuccess: () -> Unit, onDismiss: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier.clip(RoundedCornerShape(24.dp)).background(ElecBg).border(1.dp, ElecBorder, RoundedCornerShape(24.dp)).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.admin_enter_pin), style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(Modifier.height(16.dp))
            TvTextField(
                value = pin, onValueChange = { pin = it.filter { c -> c.isDigit() }.take(6); error = false },
                placeholder = stringResource(R.string.admin_pin_hint),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
                ),
                password = true,
            )
            if (error) { Spacer(Modifier.height(8.dp)); Text(stringResource(R.string.admin_pin_wrong), style = MaterialTheme.typography.labelMedium, color = Color(0xFFF87171)) }
            Spacer(Modifier.height(16.dp))
            GradientButton(text = stringResource(R.string.admin_unlock), onClick = { if (pin == correctPin) onSuccess() else { error = true; pin = "" } })
            Spacer(Modifier.height(10.dp))
            OutlineButton(text = stringResource(R.string.cancel), onClick = { onDismiss() })
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
