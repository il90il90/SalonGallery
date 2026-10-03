package com.il90.salongallery.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.outlined.RotateRight
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.DragIndicator
import androidx.compose.material.icons.outlined.FilterFrames
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Slideshow
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.blur
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.il90.salongallery.R
import com.il90.salongallery.net.AlbumInfo
import com.il90.salongallery.net.ArtGallery
import com.il90.salongallery.net.ArtPiece
import com.il90.salongallery.net.ArtSource
import com.il90.salongallery.net.GooglePhotos
import com.il90.salongallery.net.DiscoveredScreen
import com.il90.salongallery.net.FreeTrack
import com.il90.salongallery.net.LibraryList
import com.il90.salongallery.net.MusicState
import com.il90.salongallery.net.PhotoSender
import com.il90.salongallery.net.RemoteSession
import com.il90.salongallery.net.ScreenInfo
import com.il90.salongallery.ui.components.AccentGradient
import com.il90.salongallery.ui.components.GradientButton
import com.il90.salongallery.ui.components.OutlineButton
import com.il90.salongallery.ui.components.SalonBackground
import com.il90.salongallery.ui.components.SectionLabel
import com.il90.salongallery.ui.theme.ElecBorder
import com.il90.salongallery.ui.theme.ElecSurface
import com.il90.salongallery.ui.theme.ElecSurfaceElevated
import com.il90.salongallery.ui.theme.GoodGreen
import com.il90.salongallery.ui.theme.NeonBlue
import com.il90.salongallery.ui.theme.NeonCyan
import com.il90.salongallery.ui.theme.NeonTeal
import com.il90.salongallery.ui.theme.NeonViolet
import com.il90.salongallery.ui.theme.NeonVioletLight
import com.il90.salongallery.ui.theme.TintBlue
import com.il90.salongallery.ui.theme.TintClay
import com.il90.salongallery.ui.theme.TintPlum
import com.il90.salongallery.ui.theme.TintSage
import com.il90.salongallery.ui.theme.TextPrimary
import com.il90.salongallery.ui.theme.TextSecondary
import com.il90.salongallery.ui.theme.TextTertiary
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.il90.salongallery.upload.UploadManager
import com.il90.salongallery.upload.UploadQuality
import kotlinx.coroutines.flow.collect

@Composable
fun RemoteModeScreen(actions: AppActions) {
    val context = LocalContext.current
    val remoteScope = rememberCoroutineScope()
    val session = remember { RemoteSession(context) }
    // Capture the real navigation-bar inset here (edge-to-edge main window); full-screen
    // Dialogs don't reliably receive insets, so we thread this down to pad their bottoms.
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    DisposableEffect(Unit) {
        session.start()
        onDispose { session.stop() }
    }

    val screens by session.screens.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf<DiscoveredScreen?>(null) }

    // Rejoin the last screen on launch instead of making the user pick (or re-type an IP) again.
    val prefs = remember { com.il90.salongallery.data.RolePreferences(context) }
    val lastScreen by prefs.lastScreen.collectAsStateWithLifecycle(initialValue = null)
    var autoJoined by remember { mutableStateOf(false) }
    // Remember whatever we're controlling (including an address the watchdog recovered).
    LaunchedEffect(selected) {
        selected?.let {
            prefs.setLastScreen(it)
            // Keep an eye on this screen in the background and notify if it drops offline.
            runCatching { com.il90.salongallery.ScreenWatchWorker.ensureScheduled(context) }
        }
    }
    LaunchedEffect(lastScreen) {
        val last = lastScreen ?: return@LaunchedEffect
        // Keep trying until we're in: the screen is often still booting (or Wi-Fi still settling)
        // when the Remote opens, so a single probe would give up and leave the user on "Searching…".
        // Stops as soon as something is selected (by us or by the user) or the user backed out.
        while (selected == null && !autoJoined) {
            // Prefer the live discovery entry (fresh address); otherwise the saved address if it answers.
            val found = session.screens.value.firstOrNull { it.key == last.key || it.name == last.name }
            val target = found ?: last.takeIf { PhotoSender.getInfo(it.host, it.port, timeoutMs = 2500) != null }
            if (target != null && selected == null && !autoJoined) { autoJoined = true; selected = target; break }
            kotlinx.coroutines.delay(3000)
        }
    }
    var showSettings by remember { mutableStateOf(false) }
    var showManual by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf<ScreenInfo?>(null) }
    var connected by remember { mutableStateOf(true) }
    var lostScreen by remember { mutableStateOf(false) }  // disconnected AND can't re-find it
    var screenHasPin by remember { mutableStateOf(false) }
    var settingsUnlocked by remember { mutableStateOf(false) }
    var showPinGate by remember { mutableStateOf(false) }

    // Fetch whether the selected screen has a PIN (gates opening its settings from the Remote). Retry
    // until it answers so a flaky moment can't leave us thinking there's no PIN (fail-safe, not open).
    var pinKnown by remember(selected?.key) { mutableStateOf(false) }
    LaunchedEffect(selected?.key) {
        settingsUnlocked = false; pinKnown = false
        val s = selected ?: return@LaunchedEffect
        while (!pinKnown) {
            val hp = PhotoSender.getSettings(s.host, s.port)?.hasPin
            if (hp != null) { screenHasPin = hp; pinKnown = true } else kotlinx.coroutines.delay(1500)
        }
    }
    fun openSettings() {
        // Re-check freshly on each open (so a PIN just set/removed is honoured); if the check can't be
        // reached, fall back to the last known value rather than bypassing the gate.
        remoteScope.launch {
            val s = selected
            val hp = if (s != null) (PhotoSender.getSettings(s.host, s.port)?.hasPin ?: screenHasPin) else screenHasPin
            screenHasPin = hp
            if (hp && !settingsUnlocked) showPinGate = true else showSettings = true
        }
    }

    // The Display's library version as last seen on /ping; every change re-lists the Remote's photos.
    var libVersion by remember { mutableStateOf(0L) }
    LaunchedEffect(selected?.host, selected?.port) {
        val s = selected
        info = if (s != null) PhotoSender.getInfo(s.host, s.port) else null
        info?.let { libVersion = it.libVersion }
    }

    // Connection watchdog: the Display's HTTP port changes when its app restarts, so a cached
    // host:port goes stale and commands silently fail. Ping the screen; on failure re-resolve it
    // from live NSD (same service key, fresh address) and surface the status so the Remote never
    // pretends it is connected when it is not.
    LaunchedEffect(selected?.key) {
        if (selected == null) { connected = true; lostScreen = false; return@LaunchedEffect }
        var fails = 0
        while (true) {
            val s = selected ?: break
            // One /ping is both the liveness check and the change detector: it carries the
            // Display's library version, so an add/delete/clear from anywhere (this Remote, the TV
            // remote, an import) refreshes what we show instead of the UI guessing after each action.
            val pinged = PhotoSender.getInfo(s.host, s.port, timeoutMs = 2500)
            val ok = pinged != null
            if (pinged != null) {
                fails = 0
                connected = true; lostScreen = false
                info = pinged
                if (pinged.libVersion != libVersion) libVersion = pinged.libVersion
            } else {
                fails++
                // Recover the address: (a) the fixed port at the same host (the Display restarted
                // and now listens there), or (b) a fresh NSD entry (same service, new address).
                val viaFixed = if (s.port != com.il90.salongallery.net.PhotoServer.FIXED_PORT &&
                    PhotoSender.ping(s.host, com.il90.salongallery.net.PhotoServer.FIXED_PORT) != null)
                    s.copy(port = com.il90.salongallery.net.PhotoServer.FIXED_PORT) else null
                val fresh = session.screens.value.firstOrNull { it.key == s.key || it.name == s.name }
                    ?.takeIf { it.host != s.host || it.port != s.port }
                val candidate = viaFixed ?: fresh
                if (candidate != null) {
                    selected = candidate
                    kotlinx.coroutines.delay(200)
                    continue  // retry immediately against the recovered address
                }
                // Stay in a reassuring "reconnecting" state for a long time; only declare the
                // screen truly offline after a sustained outage (~30s), never on a brief blip.
                if (fails >= 2) connected = false
                if (fails >= 24) lostScreen = true
            }
            kotlinx.coroutines.delay(if (ok) 3000 else 1200)
        }
    }

    SalonBackground {
        Box(Modifier.fillMaxSize().safeDrawingPadding()) {
            Column(
                Modifier
                    .align(Alignment.TopCenter)
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                TopBar(onSettings = { openSettings() })
                Spacer(Modifier.height(24.dp))

                val target = selected
                if (target == null) {
                    SectionLabel(stringResource(R.string.remote_overline))
                    Spacer(Modifier.height(10.dp))
                    Text(
                        stringResource(R.string.remote_screens_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                    )
                    Spacer(Modifier.height(24.dp))
                    if (screens.isEmpty()) Searching()
                    else {
                        // Ping each screen once for its app version so an old install is obvious. When two
                        // screens answer from one address (two installs on one device, each on its own
                        // port) the address alone looks like a duplicate — show the ports and say why.
                        var versions by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
                        LaunchedEffect(screens) {
                            screens.forEach { s ->
                                val k = "${s.host}:${s.port}"
                                if (k !in versions) {
                                    PhotoSender.getInfo(s.host, s.port, timeoutMs = 2500)?.let { versions = versions + (k to it.version) }
                                }
                            }
                        }
                        val sharedHosts = screens.groupBy { it.host }.filterValues { it.size > 1 }.keys
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            screens.forEach { s ->
                                ScreenRow(
                                    s,
                                    subtitle = if (s.host in sharedHosts) "${s.host}:${s.port}" else s.host,
                                    version = versions["${s.host}:${s.port}"],
                                ) { selected = s }
                            }
                        }
                        if (sharedHosts.isNotEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            Text(stringResource(R.string.remote_same_host_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.remote_connect_ip),
                        style = MaterialTheme.typography.labelLarge, color = NeonCyan,
                        modifier = Modifier.clip(RoundedCornerShape(50))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showManual = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                    )
                } else {
                    ControlPanel(
                        screen = target,
                        connected = connected,
                        lostScreen = lostScreen,
                        libVersion = libVersion,
                        // Leaving on purpose: forget the screen so we don't jump straight back into it.
                        onBack = { selected = null; info = null; autoJoined = true; remoteScope.launch { prefs.setLastScreen(null) } },
                        onInfoRefresh = { scope -> scope.launch { info = PhotoSender.getInfo(target.host, target.port) } },
                        bottomInset = navBottom,
                    )
                }
            }
        }
    }

    if (showManual) {
        ManualConnectDialog(
            onConnect = { host, port -> selected = DiscoveredScreen("manual", host, host, port); showManual = false },
            onDismiss = { showManual = false },
        )
    }

    if (showPinGate) {
        selected?.let { s ->
            ScreenPinGate(
                screen = s,
                onSuccess = { showPinGate = false; settingsUnlocked = true; showSettings = true },
                onDismiss = { showPinGate = false },
            )
        }
    }

    if (showSettings) {
        SettingsSheet(
            actions = actions,
            onDismiss = { showSettings = false },
            extra = {
                info?.let {
                    Spacer(Modifier.height(16.dp))
                    ScreenInfoContent(it.widthPx, it.heightPx, it.freeBytes, it.totalBytes, it.photoCount)
                }
                selected?.let { s ->
                    Spacer(Modifier.height(20.dp))
                    RemoteScreenAdmin(
                        screen = s,
                        onRoleReset = { showSettings = false; selected = null; info = null },
                    )
                    Spacer(Modifier.height(22.dp))
                    Text(
                        stringResource(R.string.remote_screensaver_hint),
                        style = MaterialTheme.typography.bodySmall, color = TextSecondary,
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlineButton(
                        text = stringResource(R.string.remote_screensaver),
                        leading = Icons.Outlined.Bedtime,
                        onClick = { remoteScope.launch { PhotoSender.openScreensaver(s.host, s.port) } },
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlineButton(
                        text = stringResource(R.string.clear_library),
                        leading = Icons.Outlined.DeleteSweep,
                        onClick = {
                            remoteScope.launch {
                                PhotoSender.clearLibrary(s.host, s.port)
                                info = PhotoSender.getInfo(s.host, s.port)
                            }
                        },
                    )
                }
            },
        )
    }
}

private fun formatBytes(b: Long): String = when {
    b <= 0L -> ""
    b >= 1024L * 1024L -> String.format(java.util.Locale.US, "%.1f MB", b / (1024.0 * 1024.0))
    b >= 1024L -> "${b / 1024L} KB"
    else -> "$b B"
}

@Composable
private fun ScreenPinGate(screen: DiscoveredScreen, onSuccess: () -> Unit, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var checking by remember { mutableStateOf(false) }
    val tf = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
    )
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier.clip(RoundedCornerShape(24.dp)).background(com.il90.salongallery.ui.theme.ElecSurface)
                .border(1.dp, ElecBorder, RoundedCornerShape(24.dp)).padding(24.dp).imePadding(),
        ) {
            Text(stringResource(R.string.admin_enter_pin), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = pin, onValueChange = { pin = it.filter { c -> c.isDigit() }.take(6); error = false },
                singleLine = true, modifier = Modifier.fillMaxWidth(), colors = tf,
                placeholder = { Text(stringResource(R.string.admin_pin_hint), color = TextTertiary) },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
            )
            if (error) { Spacer(Modifier.height(8.dp)); Text(stringResource(R.string.admin_pin_wrong), style = MaterialTheme.typography.labelMedium, color = Color(0xFFF87171)) }
            Spacer(Modifier.height(16.dp))
            GradientButton(
                text = stringResource(R.string.admin_unlock), enabled = !checking && pin.length >= 4,
                onClick = {
                    checking = true
                    scope.launch {
                        if (PhotoSender.checkScreenPin(screen.host, screen.port, pin)) onSuccess()
                        else { error = true; pin = "" }
                        checking = false
                    }
                },
            )
            Spacer(Modifier.height(10.dp))
            OutlineButton(text = stringResource(R.string.cancel), onClick = onDismiss)
        }
    }
}

@Composable
private fun ManualConnectDialog(onConnect: (String, Int) -> Unit, onDismiss: () -> Unit) {
    var host by remember { mutableStateOf("") }
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier.clip(RoundedCornerShape(24.dp)).background(com.il90.salongallery.ui.theme.ElecSurface)
                .border(1.dp, ElecBorder, RoundedCornerShape(24.dp)).padding(24.dp).imePadding(),
        ) {
            Text(stringResource(R.string.remote_connect_ip), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.remote_connect_ip_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(16.dp))
            val tf = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
            )
            OutlinedTextField(
                value = host, onValueChange = { host = it }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("192.168.1.42", color = TextTertiary) }, label = { Text("IP address", color = TextSecondary) }, colors = tf,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
            )
            Spacer(Modifier.height(18.dp))
            GradientButton(
                text = stringResource(R.string.remote_connect),
                enabled = host.isNotBlank(),
                onClick = { onConnect(host.trim(), com.il90.salongallery.net.PhotoServer.FIXED_PORT) },
            )
            Spacer(Modifier.height(10.dp))
            OutlineButton(text = stringResource(R.string.cancel), onClick = onDismiss)
        }
    }
}

/**
 * The screen's admin settings, edited entirely from the Remote: device name, auto-sleep
 * schedule, screen PIN, and a "return to setup" that drops the screen back to role selection.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RemoteScreenAdmin(screen: DiscoveredScreen, onRoleReset: () -> Unit) {
    val scope = rememberCoroutineScope()
    var loaded by remember(screen.host, screen.port) { mutableStateOf(false) }
    var name by remember(screen.host, screen.port) { mutableStateOf("") }
    var nameSaved by remember(screen.host, screen.port) { mutableStateOf(false) }
    var pin by remember(screen.host, screen.port) { mutableStateOf("") }
    var hasPin by remember(screen.host, screen.port) { mutableStateOf(false) }
    var pinMsg by remember(screen.host, screen.port) { mutableStateOf<String?>(null) }
    var schedOn by remember(screen.host, screen.port) { mutableStateOf(false) }
    var sleepStart by remember(screen.host, screen.port) { mutableIntStateOf(1380) }
    var sleepEnd by remember(screen.host, screen.port) { mutableIntStateOf(420) }
    var confirmReset by remember { mutableStateOf(false) }
    // Content schedule (business hours)
    var csOn by remember(screen.host, screen.port) { mutableStateOf(false) }
    var csOpen by remember(screen.host, screen.port) { mutableIntStateOf(540) }
    var csClose by remember(screen.host, screen.port) { mutableIntStateOf(1080) }
    var csOpenAlbum by remember(screen.host, screen.port) { mutableStateOf("all") }
    var csClosedAlbum by remember(screen.host, screen.port) { mutableStateOf("all") }
    var csAlbums by remember(screen.host, screen.port) { mutableStateOf<List<AlbumInfo>>(emptyList()) }
    fun pushContentSchedule() {
        scope.launch { PhotoSender.setContentSchedule(screen.host, screen.port, csOn, csOpen, csClose, csOpenAlbum, csClosedAlbum) }
    }

    // Load the screen's real name / PIN / schedule, retrying until it succeeds so a single flaky
    // request doesn't leave the fields blank (which looks like the settings were never saved).
    LaunchedEffect(screen.host, screen.port) {
        while (!loaded) {
            val s = PhotoSender.getSettings(screen.host, screen.port)
            if (s != null) {
                name = s.name; hasPin = s.hasPin; schedOn = s.schedOn
                sleepStart = s.sleepStart; sleepEnd = s.sleepEnd
                csOn = s.csOn; csOpen = s.csOpen; csClose = s.csClose
                csOpenAlbum = s.csOpenAlbum; csClosedAlbum = s.csClosedAlbum
                loaded = true
            } else kotlinx.coroutines.delay(1500)
        }
        PhotoSender.getAlbums(screen.host, screen.port)?.let { csAlbums = it.albums }
    }

    val tf = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
    )

    val nameReq = rememberRevealRequester()
    val pinReq = rememberRevealRequester()
    Column(Modifier.fillMaxWidth()) {
        SectionLabel(stringResource(R.string.admin_section))

        Spacer(Modifier.height(14.dp))
        Text(stringResource(R.string.admin_name), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = name, onValueChange = { name = it; nameSaved = false }, singleLine = true,
            modifier = Modifier.fillMaxWidth().revealOnFocus(nameReq), colors = tf,
            placeholder = { Text(stringResource(R.string.admin_name_hint), color = TextTertiary) },
        )
        Spacer(Modifier.height(10.dp))
        GradientButton(
            modifier = Modifier.bringIntoViewRequester(nameReq),
            text = if (nameSaved) stringResource(R.string.admin_saved) else stringResource(R.string.admin_save_name),
            enabled = loaded,
            onClick = { scope.launch { PhotoSender.setName(screen.host, screen.port, name.trim()); nameSaved = true } },
        )

        Spacer(Modifier.height(22.dp))
        Text(stringResource(R.string.admin_pin), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(if (hasPin) R.string.admin_pin_on else R.string.admin_pin_off),
            style = MaterialTheme.typography.bodySmall, color = TextSecondary,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = pin, onValueChange = { pin = it.filter { c -> c.isDigit() }.take(6); pinMsg = null },
            singleLine = true, modifier = Modifier.fillMaxWidth().revealOnFocus(pinReq), colors = tf,
            placeholder = { Text(stringResource(R.string.admin_pin_hint), color = TextTertiary) },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword,
            ),
        )
        Spacer(Modifier.height(10.dp))
        Row(Modifier.bringIntoViewRequester(pinReq), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.weight(1f)) {
                GradientButton(text = stringResource(R.string.admin_save_pin), enabled = loaded, onClick = {
                    if (pin.length >= 4) scope.launch {
                        PhotoSender.setScreenPin(screen.host, screen.port, pin); hasPin = true; pin = ""; pinMsg = "PIN saved ✓"
                    } else pinMsg = "Use at least 4 digits"
                })
            }
            if (hasPin) Box(Modifier.weight(1f)) {
                OutlineButton(text = stringResource(R.string.admin_remove_pin), onClick = {
                    scope.launch { PhotoSender.setScreenPin(screen.host, screen.port, ""); hasPin = false; pin = ""; pinMsg = "PIN removed" }
                })
            }
        }
        pinMsg?.let { Spacer(Modifier.height(8.dp)); Text(it, style = MaterialTheme.typography.labelMedium, color = NeonCyan) }

        // Auto-sleep schedule lives in its own card (a clock section), set apart from the plain
        // name/PIN fields above.
        Spacer(Modifier.height(22.dp))
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                .background(ElecSurfaceElevated).border(1.dp, ElecBorder, RoundedCornerShape(18.dp))
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Schedule, null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text(stringResource(R.string.admin_schedule), style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                Switch(checked = schedOn, onCheckedChange = {
                    schedOn = it; scope.launch { PhotoSender.setSchedule(screen.host, screen.port, it, sleepStart, sleepEnd) }
                })
            }
            if (schedOn) {
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.admin_schedule_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(12.dp))
                RemoteTimeRow(stringResource(R.string.admin_sleep_at), sleepStart) {
                    sleepStart = it; scope.launch { PhotoSender.setSchedule(screen.host, screen.port, schedOn, it, sleepEnd) }
                }
                Spacer(Modifier.height(10.dp))
                RemoteTimeRow(stringResource(R.string.admin_wake_at), sleepEnd) {
                    sleepEnd = it; scope.launch { PhotoSender.setSchedule(screen.host, screen.port, schedOn, sleepStart, it) }
                }
            }
        }

        // Content schedule (business hours): which album shows while open vs. closed.
        Spacer(Modifier.height(16.dp))
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                .background(ElecSurfaceElevated).border(1.dp, ElecBorder, RoundedCornerShape(18.dp))
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Storefront, null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text(stringResource(R.string.admin_content_schedule), style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                Switch(checked = csOn, onCheckedChange = { csOn = it; pushContentSchedule() })
            }
            if (csOn) {
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.admin_content_schedule_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(12.dp))
                RemoteTimeRow(stringResource(R.string.admin_open_at), csOpen) { csOpen = it; pushContentSchedule() }
                Spacer(Modifier.height(10.dp))
                RemoteTimeRow(stringResource(R.string.admin_close_at), csClose) { csClose = it; pushContentSchedule() }
                // Album while open
                Spacer(Modifier.height(14.dp))
                FieldLabel(stringResource(R.string.admin_album_open))
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AlbumChip(stringResource(R.string.album_all), csOpenAlbum == "all") { csOpenAlbum = "all"; pushContentSchedule() }
                    csAlbums.forEach { a -> AlbumChip(a.name, csOpenAlbum == a.id) { csOpenAlbum = a.id; pushContentSchedule() } }
                }
                // Album while closed
                Spacer(Modifier.height(12.dp))
                FieldLabel(stringResource(R.string.admin_album_closed))
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AlbumChip(stringResource(R.string.album_all), csClosedAlbum == "all") { csClosedAlbum = "all"; pushContentSchedule() }
                    csAlbums.forEach { a -> AlbumChip(a.name, csClosedAlbum == a.id) { csClosedAlbum = a.id; pushContentSchedule() } }
                }
            }
        }

        Spacer(Modifier.height(22.dp))
        Text(stringResource(R.string.remote_change_role_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        OutlineButton(text = stringResource(R.string.remote_change_role), leading = Icons.Outlined.SwapHoriz, onClick = { confirmReset = true })
    }

    if (confirmReset) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { confirmReset = false }) {
            Column(
                Modifier.clip(RoundedCornerShape(24.dp)).background(com.il90.salongallery.ui.theme.ElecSurface)
                    .border(1.dp, ElecBorder, RoundedCornerShape(24.dp)).padding(24.dp),
            ) {
                Text(stringResource(R.string.remote_change_role), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.remote_change_role_confirm), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Spacer(Modifier.height(18.dp))
                GradientButton(text = stringResource(R.string.remote_change_role), onClick = {
                    confirmReset = false
                    scope.launch { PhotoSender.resetRole(screen.host, screen.port); onRoleReset() }
                })
                Spacer(Modifier.height(10.dp))
                OutlineButton(text = stringResource(R.string.cancel), onClick = { confirmReset = false })
            }
        }
    }
}

@Composable
private fun RemoteTimeRow(label: String, minutes: Int, onChange: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(com.il90.salongallery.ui.theme.ElecSurface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
        RemoteStepBtn("−") { onChange((minutes - 30 + 1440) % 1440) }
        Text(
            com.il90.salongallery.net.DisplayPrefs.fmt(minutes),
            style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
            modifier = Modifier.width(74.dp), textAlign = TextAlign.Center,
        )
        RemoteStepBtn("+") { onChange((minutes + 30) % 1440) }
    }
}

@Composable
private fun RemoteStepBtn(symbol: String, onClick: () -> Unit) {
    Box(
        Modifier.size(40.dp).clip(RoundedCornerShape(50)).background(ElecBorder.copy(alpha = 0.25f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(symbol, style = androidx.compose.ui.text.TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = NeonCyan))
    }
}

@Composable
private fun ControlPanel(
    screen: DiscoveredScreen,
    onBack: () -> Unit,
    onInfoRefresh: (kotlinx.coroutines.CoroutineScope) -> Unit,
    bottomInset: androidx.compose.ui.unit.Dp = 0.dp,
    connected: Boolean = true,
    lostScreen: Boolean = false,
    libVersion: Long = 0L,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0 to 0) } // done to total

    // Keep the phone awake while uploading — otherwise the screen sleeps mid-transfer, the upload
    // stalls and the "Sending x/y" counter freezes.
    val keepOnView = androidx.compose.ui.platform.LocalView.current
    DisposableEffect(busy) {
        keepOnView.keepScreenOn = busy
        onDispose { keepOnView.keepScreenOn = false }
    }

    var frameId by remember { mutableIntStateOf(0) }
    var frameWidth by remember { mutableStateOf(1f) }
    var frameRandom by remember { mutableStateOf(false) }
    var framePool by remember { mutableStateOf(setOf(1, 3, 4, 8)) }
    var shuffle by remember { mutableStateOf(false) }
    var intervalMs by remember { mutableStateOf(30000L) }
    var orientation by remember { mutableStateOf("auto") }
    var effect by remember { mutableStateOf("none") }
    var effectPool by remember { mutableStateOf(setOf("fade", "slide", "zoom", "dissolve")) }
    var filter by remember { mutableStateOf("none") }
    var filterPool by remember { mutableStateOf(setOf("none", "mono", "sepia", "warm", "cool", "vignette")) }
    var fit by remember { mutableStateOf("fill") }
    var bgColor by remember { mutableStateOf("auto") }
    var collage by remember { mutableStateOf(false) }
    var layout by remember { mutableStateOf("single") }
    var layoutPool by remember { mutableStateOf(SPREAD_KEYS.toSet()) }
    var motion by remember { mutableStateOf("off") }
    var stagger by remember { mutableStateOf(false) }
    var smartGroup by remember { mutableStateOf(false) }
    var spreadMix by remember { mutableStateOf("always") }
    var mediaMenu by remember { mutableStateOf(false) }   // Photos / Videos / Both / folder chooser for "Add"
    var folderKindMenu by remember { mutableStateOf(false) }  // Photos / Videos / All for the chosen folder
    var folderKind by remember { mutableStateOf("all") }  // which types the chosen folder should send
    var motionSpeed by remember { mutableStateOf("slow") }
    var showFrames by remember { mutableStateOf(false) }
    var showEffects by remember { mutableStateOf(false) }
    var showSlideshow by remember { mutableStateOf(false) }
    var showLibrary by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }
    var showMusic by remember { mutableStateOf(false) }
    var showArt by remember { mutableStateOf(false) }
    var showGooglePhotos by remember { mutableStateOf(false) }
    var showRss by remember { mutableStateOf(false) }
    var lib by remember { mutableStateOf<LibraryList?>(null) }

    var screenBrightness by remember { mutableStateOf(1f) }
    var screenVolume by remember { mutableStateOf(1f) }
    var music by remember { mutableStateOf<MusicState?>(null) }
    // The exact slide on the wall (layout + member photos), polled so the hero mirrors a spread.
    var now by remember { mutableStateOf<com.il90.salongallery.net.NowInfo?>(null) }
    LaunchedEffect(screen.host, screen.port) {
        while (true) { now = PhotoSender.getNow(screen.host, screen.port) ?: now; kotlinx.coroutines.delay(2500) }
    }
    fun refreshMusic() { scope.launch { PhotoSender.getMusic(screen.host, screen.port)?.let { music = it } } }
    // Keep the mini-player in sync with the screen.
    LaunchedEffect(screen.host, screen.port) {
        while (true) { PhotoSender.getMusic(screen.host, screen.port)?.let { music = it }; kotlinx.coroutines.delay(4000) }
    }
    fun refreshLib() { scope.launch { PhotoSender.getList(screen.host, screen.port)?.let { lib = it } } }
    LaunchedEffect(screen.host, screen.port) {
        refreshLib()
        // Load the wall's real current settings on connect so every sheet opens showing what is
        // actually set (not fresh defaults) — otherwise changes look like they were never saved.
        PhotoSender.getInfo(screen.host, screen.port)?.let {
            screenBrightness = it.brightness; screenVolume = it.volume
            layout = it.layout; motion = it.motion; motionSpeed = it.motionSpeed
            fit = it.fit; bgColor = it.bg; spreadMix = it.spreadMix
            stagger = it.stagger; shuffle = it.shuffle; collage = it.collage
            orientation = it.orientation; intervalMs = it.intervalMs; smartGroup = it.smartGroup
        }
    }
    // Re-list whenever the Display reports its library changed (fixes "cleared photos still shown").
    LaunchedEffect(libVersion) { if (libVersion != 0L) refreshLib() }

    suspend fun readBytes(uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
        runCatching { context.contentResolver.openInputStream(uri)?.use { it.readBytes() } }.getOrNull()
    }
    fun displayName(uri: Uri): String {
        var name = "Track"
        runCatching {
            context.contentResolver.query(uri, null, null, null, null)?.use { c ->
                val idx = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (idx >= 0 && c.moveToFirst()) c.getString(idx)?.let { name = it }
            }
        }
        return name.substringBeforeLast('.')
    }

    // One picker for photos AND videos together — each item is routed to the right endpoint.
    // Picking doesn't upload yet: a destination sheet first asks which album these go to (an
    // existing one, a new one, or none), so the library stays organized instead of everything
    // landing in "All".
    var pendingMedia by remember { mutableStateOf<List<Uri>?>(null) }
    var destAlbums by remember { mutableStateOf<List<AlbumInfo>>(emptyList()) }
    // After an album is chosen (or none needed), we ask the quality once, then hand the batch to the
    // background upload service so it finishes even if the app is closed or the screen turns off.
    var pendingUpload by remember { mutableStateOf<Pair<List<Uri>, String?>?>(null) }
    fun uploadMedia(uris: List<Uri>, album: String?, quality: UploadQuality) {
        if (uris.isEmpty()) return
        status = null
        UploadManager.enqueue(context, screen.host, screen.port, album, quality, uris)
    }
    // Every upload path funnels here: show the quality chooser, then enqueue with the choice.
    fun beginUpload(uris: List<Uri>, album: String?) { pendingUpload = uris to album }

    // Mirror the background upload's live progress into the on-screen banner, and refresh the library
    // once a batch finishes. Because the service outlives this screen, re-opening the app resumes the
    // live count from wherever the upload has got to.
    LaunchedEffect(Unit) {
        var wasRunning = false
        UploadManager.progress.collect { up ->
            if (up.running) { busy = true; progress = up.done to up.total; status = null }
            else {
                busy = false
                if (wasRunning && up.total > 0) {
                    progress = 0 to 0
                    status = if (up.cancelled) context.getString(R.string.upload_stopped, up.done)
                             else "Added ${up.done} / ${up.total} ✓"
                    onInfoRefresh(scope); refreshLib()
                }
            }
            wasRunning = up.running
        }
    }
    // The Android Photo Picker — the familiar gallery grid, filtered to photos / videos / both. It
    // caps a selection at the system limit (usually 100); for sending a whole camera roll at once
    // the "Whole folder" option below has no cap.
    val mediaPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        // Load the album list BEFORE opening the sheet: it decides its initial layout (chips vs.
        // "name your first album") from that list, so showing it early made it think there were none.
        scope.launch {
            destAlbums = PhotoSender.getAlbums(screen.host, screen.port)?.albums ?: emptyList()
            pendingMedia = uris
        }
    }
    // Uncapped multi-select via the file picker (SAF): Android's Photo Picker limits a selection to
    // ~100, this one doesn't, so hundreds can be sent at once (from device storage and file providers).
    val manyFilesPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        scope.launch {
            destAlbums = PhotoSender.getAlbums(screen.host, screen.port)?.albums ?: emptyList()
            pendingMedia = uris
        }
    }
    // "Whole folder": pick a folder (e.g. DCIM/Camera) and send every photo and video in it, sub-folders
    // included — no selection to make and no cap at all.
    // Remembered sync folder (persisted), so "Sync folder" can re-scan it later for new files.
    val remotePrefs = remember { context.getSharedPreferences("salon_remote", android.content.Context.MODE_PRIVATE) }
    var syncFolder by remember { mutableStateOf(remotePrefs.getString("sync_folder", null)) }
    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { tree ->
        if (tree == null) return@rememberLauncherForActivityResult
        // Keep read access across app restarts so Sync works later without re-picking.
        runCatching { context.contentResolver.takePersistableUriPermission(tree, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        remotePrefs.edit().putString("sync_folder", tree.toString()).apply()
        syncFolder = tree.toString()
        busy = true; status = null
        scope.launch {
            val uris = withContext(Dispatchers.IO) { mediaInTree(context, tree, folderKind) }
            busy = false
            if (uris.isEmpty()) { status = context.getString(R.string.folder_empty); return@launch }
            destAlbums = PhotoSender.getAlbums(screen.host, screen.port)?.albums ?: emptyList()
            pendingMedia = uris
        }
    }
    // Re-scan the remembered folder; the Display skips files it already has, so only new ones land.
    fun syncFolderNow() {
        val saved = syncFolder ?: return
        busy = true; status = null
        scope.launch {
            val uris = withContext(Dispatchers.IO) { runCatching { mediaInTree(context, android.net.Uri.parse(saved), "all") }.getOrDefault(emptyList()) }
            if (uris.isEmpty()) { busy = false; status = context.getString(R.string.folder_empty); return@launch }
            busy = false
            // Ask quality, then upload straight into the active album (or All); dups dropped by Display.
            beginUpload(uris, null)
        }
    }
    val musicPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        busy = true; status = null; progress = 0 to uris.size
        scope.launch {
            var ok = 0
            uris.forEachIndexed { i, uri ->
                val bytes = readBytes(uri)
                if (bytes != null && PhotoSender.sendMusic(screen.host, screen.port, bytes, displayName(uri)) == null) ok++
                progress = (i + 1) to uris.size
            }
            busy = false; status = "Added $ok / ${uris.size} tracks ✓"
        }
    }

    Column(Modifier.fillMaxWidth()) {
        if (!connected) {
            ConnectionBanner(lostScreen = lostScreen, onChoose = onBack)
            Spacer(Modifier.height(14.dp))
        }
        // Link status rides at the very top, right under the Salon Gallery header.
        WallStatusLine(screenName = screen.name, connected = connected, lost = lostScreen)
        Spacer(Modifier.height(14.dp))
        val nowName = lib?.let { it.items.getOrNull(it.current) }
        NowShowingHero(
            screen = screen,
            current = nowName,
            now = now,
            rots = lib?.rots ?: emptyMap(),
            rot = nowName?.let { lib?.rots?.get(it) } ?: 0,
            onRotate = { nowName?.let { n -> scope.launch { PhotoSender.rotatePhoto(screen.host, screen.port, n); refreshLib() } } },
            onSwipe = { delta ->
                val items = lib?.items ?: emptyList()
                if (items.size > 1) {
                    val cur = lib?.current ?: 0
                    val ni = ((cur + delta) % items.size + items.size) % items.size
                    scope.launch { PhotoSender.showNow(screen.host, screen.port, items[ni]); refreshLib() }
                }
            },
        )

        // Upload / status banner right under the hero, where it's immediately visible.
        if (busy || status != null) {
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(ElecSurface).border(1.dp, ElecBorder, RoundedCornerShape(14.dp)).padding(vertical = 12.dp, horizontal = 16.dp), contentAlignment = Alignment.Center) {
                when {
                    // While sending: progress text on the left, a Stop button on the right so the owner
                    // can call off a long transfer (it empties the queue; the file in flight finishes).
                    busy -> Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (progress.second > 1) ProgressRow("Sending ${progress.first}/${progress.second}…")
                            else ProgressRow(stringResource(R.string.remote_sending))
                        }
                        Text(
                            stringResource(R.string.upload_stop),
                            style = MaterialTheme.typography.labelLarge, color = Color(0xFFF2B07A), fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clip(RoundedCornerShape(50))
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { UploadManager.cancel() }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                    status != null -> Text(
                        status!!, style = MaterialTheme.typography.labelMedium,
                        color = if (status!!.contains("✓") || status!!.contains("cleared")) GoodGreen else Color(0xFFC0503A),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CompactSlider(Modifier.weight(1f), Icons.Outlined.BrightnessMedium, NeonCyan, initial = screenBrightness) { v ->
                screenBrightness = v; scope.launch { PhotoSender.setBrightness(screen.host, screen.port, v) }
            }
            CompactSlider(Modifier.weight(1f), Icons.AutoMirrored.Outlined.VolumeUp, NeonBlue, initial = screenVolume) { v ->
                screenVolume = v; scope.launch { PhotoSender.setVolume(screen.host, screen.port, v) }
            }
        }

        music?.let { m ->
            if (m.tracks.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                MiniPlayer(
                    state = m,
                    onToggle = { scope.launch { PhotoSender.musicControl(screen.host, screen.port, "toggle"); refreshMusic() } },
                    onNext = { scope.launch { PhotoSender.musicControl(screen.host, screen.port, "next"); refreshMusic() } },
                    onPrev = { scope.launch { PhotoSender.musicControl(screen.host, screen.port, "prev"); refreshMusic() } },
                    onOpen = { showMusic = true },
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(stringResource(R.string.library_title), style = MaterialTheme.typography.titleLarge, color = TextPrimary, modifier = Modifier.weight(1f))
            Text(
                stringResource(R.string.home_see_all),
                style = MaterialTheme.typography.labelLarge, color = NeonCyan,
                modifier = Modifier.clip(RoundedCornerShape(50))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showLibrary = true }
                    .padding(horizontal = 6.dp, vertical = 4.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        LibraryStrip(
            screen = screen,
            items = lib?.items ?: emptyList(),
            current = lib?.current ?: -1,
            rots = lib?.rots ?: emptyMap(),
            onShow = { name -> scope.launch { PhotoSender.showNow(screen.host, screen.port, name); refreshLib() } },
        )

        Spacer(Modifier.height(24.dp))
        SectionLabel(stringResource(R.string.home_add))
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.weight(1f)) {
                HomeAction(Modifier.fillMaxWidth(), Icons.Outlined.AddPhotoAlternate, stringResource(R.string.home_photos), NeonCyan, TintClay, !busy) {
                    mediaMenu = true
                }
                // One "Add" entry point: pick photos, videos, both (gallery), or a whole folder.
                AddSourceMenu(
                    mediaMenu, { mediaMenu = false },
                    onFiles = { kind -> mediaMenu = false; mediaPicker.launch(PickVisualMediaRequest(visualTypeFor(kind))) },
                    onFolder = { mediaMenu = false; folderKindMenu = true },
                    onGooglePhotos = { mediaMenu = false; showGooglePhotos = true },
                    hasSyncFolder = syncFolder != null,
                    onSync = { mediaMenu = false; syncFolderNow() },
                    onManyFiles = { mediaMenu = false; manyFilesPicker.launch(arrayOf("image/*", "video/*")) },
                )
                // Second step for "Whole folder": which kinds to pull from it.
                MediaKindMenu(folderKindMenu, { folderKindMenu = false }) { kind ->
                    folderKindMenu = false; folderKind = kind; folderPicker.launch(null)
                }
            }
            HomeAction(Modifier.weight(1f), Icons.Outlined.Explore, stringResource(R.string.home_art), NeonTeal, TintSage, !busy) { showArt = true }
            HomeAction(Modifier.weight(1f), Icons.Outlined.MusicNote, stringResource(R.string.home_music), NeonViolet, TintPlum, !busy) { showMusic = true }
        }

        Spacer(Modifier.height(20.dp))
        SectionLabel(stringResource(R.string.home_style))
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HomeAction(Modifier.weight(1f), Icons.Outlined.FilterFrames, stringResource(R.string.tile_frame), NeonCyan, TintClay, true) { showFrames = true }
            HomeAction(Modifier.weight(1f), Icons.Outlined.AutoAwesome, stringResource(R.string.effects_title), NeonTeal, TintSage, true) { showEffects = true }
            HomeAction(Modifier.weight(1f), Icons.Outlined.Slideshow, stringResource(R.string.tile_slideshow), NeonBlue, TintBlue, true) { showSlideshow = true }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HomeAction(Modifier.weight(1f), Icons.Outlined.Layers, stringResource(R.string.home_overlays), NeonViolet, TintPlum, true) { showText = true }
            HomeAction(Modifier.weight(1f), Icons.Outlined.RssFeed, stringResource(R.string.rss_title), NeonCyan, TintClay, true) { showRss = true }
        }

        Spacer(Modifier.height(18.dp))
        OutlineButton(text = stringResource(R.string.remote_disconnect), onClick = onBack)
        Spacer(Modifier.height(24.dp))
    }

    if (showFrames) {
        FrameSheet(
            current = frameId, width = frameWidth, random = frameRandom, pool = framePool,
            onPick = { id -> frameId = id; frameRandom = false; scope.launch { PhotoSender.setFrame(screen.host, screen.port, id) } },
            onWidth = { w -> frameWidth = w; scope.launch { PhotoSender.setFrameWidth(screen.host, screen.port, w) } },
            onRandom = { on -> frameRandom = on; scope.launch { PhotoSender.setFrameRandom(screen.host, screen.port, on, framePool.toList()) } },
            onPool = { p -> framePool = p; scope.launch { PhotoSender.setFrameRandom(screen.host, screen.port, frameRandom, p.toList()) } },
            onDismiss = { showFrames = false },
        )
    }
    if (showEffects) {
        EffectsSheet(
            effect = effect, filter = filter, pool = effectPool, filterPool = filterPool,
            onEffect = { effect = it; scope.launch { PhotoSender.setEffect(screen.host, screen.port, it) } },
            onFilter = { filter = it; scope.launch { PhotoSender.setFilter(screen.host, screen.port, it) } },
            onPool = { effectPool = it; scope.launch { PhotoSender.setEffectPool(screen.host, screen.port, it.toList()) } },
            onFilterPool = { filterPool = it; scope.launch { PhotoSender.setFilterPool(screen.host, screen.port, it.toList()) } },
            onDismiss = { showEffects = false },
        )
    }
    if (showSlideshow) {
        SlideshowSheet(
            shuffle = shuffle, intervalMs = intervalMs, orientation = orientation, fit = fit, collage = collage,
            layout = layout,
            onLayout = { layout = it; scope.launch { PhotoSender.setLayout(screen.host, screen.port, it) } },
            layoutPool = layoutPool,
            onLayoutPool = { layoutPool = it; scope.launch { PhotoSender.setLayoutPool(screen.host, screen.port, it.toList()) } },
            motion = motion, motionSpeed = motionSpeed,
            stagger = stagger,
            spreadMix = spreadMix,
            smartGroup = smartGroup,
            onSmartGroup = { smartGroup = it; scope.launch { PhotoSender.setSmartGroup(screen.host, screen.port, it) } },
            onSpreadMix = { spreadMix = it; scope.launch { PhotoSender.setSpreadMix(screen.host, screen.port, it) } },
            onStagger = { stagger = it; scope.launch { PhotoSender.setStagger(screen.host, screen.port, it) } },
            onMotion = { motion = it; scope.launch { PhotoSender.setMotion(screen.host, screen.port, it, motionSpeed) } },
            onMotionSpeed = { motionSpeed = it; scope.launch { PhotoSender.setMotion(screen.host, screen.port, motion, it) } },
            onShuffle = { shuffle = it; scope.launch { PhotoSender.setSlideshow(screen.host, screen.port, intervalMs, it) } },
            onInterval = { intervalMs = it; scope.launch { PhotoSender.setSlideshow(screen.host, screen.port, it, shuffle) } },
            onOrientation = { orientation = it; scope.launch { PhotoSender.setOrientation(screen.host, screen.port, it) } },
            onFit = { fit = it; scope.launch { PhotoSender.setFit(screen.host, screen.port, it) } },
            bgColor = bgColor,
            onBg = { bgColor = it; scope.launch { PhotoSender.setBackground(screen.host, screen.port, it) } },
            onCollage = { collage = it; scope.launch { PhotoSender.setCollage(screen.host, screen.port, it) } },
            onDismiss = { showSlideshow = false },
        )
    }
    if (showLibrary) {
        LibraryManager(screen = screen, bottomInset = bottomInset, libVersion = libVersion, onClose = { showLibrary = false; onInfoRefresh(scope); refreshLib() })
    }
    // "Where should these go?" — shown right after the media picker returns.
    val errCreateAlbum = stringResource(R.string.add_dest_err_create)
    pendingMedia?.let { uris ->
        DestinationSheet(
            count = uris.size, albums = destAlbums,
            onPick = { id -> pendingMedia = null; beginUpload(uris, id) },
            onCreate = { name ->
                pendingMedia = null
                scope.launch {
                    // If the album can't be created, say so — never quietly dump the photos into All.
                    val id = PhotoSender.createAlbum(screen.host, screen.port, name)
                    if (id == null) status = errCreateAlbum else beginUpload(uris, id)
                }
            },
            onDismiss = { pendingMedia = null },
        )
    }
    // After the destination is known, ask the upload quality once (default = last choice), then upload.
    pendingUpload?.let { (uris, album) ->
        QualityChooser(
            count = uris.size,
            initial = runCatching { UploadQuality.valueOf(remotePrefs.getString("upload_quality", "") ?: "") }.getOrDefault(UploadQuality.OPTIMIZED),
            onPick = { q ->
                remotePrefs.edit().putString("upload_quality", q.name).apply()
                pendingUpload = null
                uploadMedia(uris, album, q)
            },
            onDismiss = { pendingUpload = null },
        )
    }
    if (showText) {
        TextSheet(
            screen = screen,
            onText = { content, pos, size, color, font ->
                scope.launch { PhotoSender.setText(screen.host, screen.port, content, pos, size, color, font) }
            },
            onClock = { on, pos, date, style, size ->
                scope.launch { PhotoSender.setClock(screen.host, screen.port, on, pos, date, style, size) }
            },
            onWeather = { on, place, lat, lon, units, pos, style ->
                scope.launch { PhotoSender.setWeather(screen.host, screen.port, on, place, lat, lon, units, pos, style) }
            },
            onClear = {
                scope.launch {
                    PhotoSender.setText(screen.host, screen.port, "", "bottom", "m", "white")
                    PhotoSender.setClock(screen.host, screen.port, false)
                    PhotoSender.setWeather(screen.host, screen.port, false)
                }
            },
            onDismiss = { showText = false },
        )
    }
    if (showMusic) {
        MusicSheet(
            screen = screen,
            onAddFromPhone = { musicPicker.launch("audio/*") },
            onDismiss = { showMusic = false },
        )
    }
    if (showGooglePhotos) {
        GooglePhotosSheet(screen = screen, onDismiss = { showGooglePhotos = false; onInfoRefresh(scope); refreshLib() })
    }
    if (showArt) {
        ArtSheet(screen = screen, onDismiss = { showArt = false; onInfoRefresh(scope); refreshLib() })
    }
    if (showRss) {
        RssSheet(screen = screen, onDismiss = { showRss = false })
    }
}

@Composable
private fun ConnectionBanner(lostScreen: Boolean, onChoose: () -> Unit) {
    val amber = Color(0xFFE0A857)
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(amber.copy(alpha = 0.12f)).border(1.dp, amber.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(amber))
            Text(
                stringResource(if (lostScreen) R.string.conn_offline else R.string.conn_reconnecting),
                style = MaterialTheme.typography.titleSmall, color = TextPrimary, modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(if (lostScreen) R.string.conn_offline_hint else R.string.conn_reconnecting_hint),
            style = MaterialTheme.typography.bodySmall, color = TextSecondary,
        )
        if (lostScreen) {
            Spacer(Modifier.height(10.dp))
            OutlineButton(text = stringResource(R.string.conn_choose), onClick = onChoose)
        }
    }
}

/**
 * The "On the wall · <screen> · Connected" link-status row. Lives at the very top of the control
 * panel (right under the Salon Gallery header) so the connection state is the first thing seen.
 */
@Composable
private fun WallStatusLine(screenName: String, connected: Boolean, lost: Boolean) {
    // Status colour tracks the real link state — it must never say "Connected" while the
    // watchdog is showing the reconnecting banner.
    val statusColor = when { lost -> Color(0xFFF87171); !connected -> Color(0xFFF2B07A); else -> GoodGreen }
    val statusText = stringResource(when { lost -> R.string.remote_offline; !connected -> R.string.remote_reconnecting; else -> R.string.remote_connected })
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(9.dp).clip(CircleShape).background(statusColor))
        Spacer(Modifier.width(9.dp))
        Text(stringResource(R.string.home_on_wall) + " ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(screenName, style = MaterialTheme.typography.bodyMedium, fontFamily = com.il90.salongallery.ui.theme.ContentFont, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 1)
        Spacer(Modifier.weight(1f))
        Text(statusText, style = MaterialTheme.typography.labelMedium, color = statusColor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun NowShowingHero(
    screen: DiscoveredScreen, current: String?,
    now: com.il90.salongallery.net.NowInfo? = null, rots: Map<String, Int> = emptyMap(),
    rot: Int = 0, onRotate: () -> Unit = {}, onSwipe: (Int) -> Unit = {},
) {
    // A spread is on the wall when the current slide has more than one photo. Only when there is
    // actually something on the wall (current != null) — otherwise a just-cleared library would keep
    // showing the previous spread from cache while the empty-state text sits on top of it.
    val mirror = now?.takeIf { it.members.size > 1 && it.style != "single" && current != null }
    Column(Modifier.fillMaxWidth()) {
        Box(
            Modifier.fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(24.dp), spotColor = Color(0xCC000000), ambientColor = Color(0x66000000))
                .clip(RoundedCornerShape(24.dp))
                .background(ElecSurface)
                .border(1.dp, ElecBorder, RoundedCornerShape(24.dp))
                .padding(6.dp),
        ) {
            Box(
                Modifier.fillMaxWidth().height(224.dp).clip(RoundedCornerShape(17.dp)).background(ElecSurfaceElevated)
                    // Swipe the "now showing" photo to flip it on the wall too.
                    .pointerInput(current) {
                        var dx = 0f
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (dx <= -40f) onSwipe(1) else if (dx >= 40f) onSwipe(-1)
                                dx = 0f
                            },
                            onHorizontalDrag = { _, amount -> dx += amount },
                        )
                    },
            ) {
                if (mirror != null) {
                    MirrorSpread(screen, mirror, rots)
                } else if (current != null) {
                    // Preview turns the same way the wall does; swap the laid-out size for a quarter
                    // turn so the rotated image still fills the card.
                    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val swapped = rot == 90 || rot == 270
                        AsyncImage(
                            model = PhotoSender.fullUrl(screen.host, screen.port, current),
                            contentDescription = null, contentScale = ContentScale.Crop,
                            // requiredSize: a plain size() is clamped to the parent's constraints, which would
                            // collapse the swapped (tall) layout back to a square before the turn.
                            modifier = Modifier
                                .requiredSize(if (swapped) maxHeight else maxWidth, if (swapped) maxWidth else maxHeight)
                                .graphicsLayer { rotationZ = rot.toFloat() },
                        )
                    }
                }
                Box(
                    Modifier.align(Alignment.BottomStart).fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xAA140E06))))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Text(
                        if (current != null) stringResource(R.string.home_now_showing) else stringResource(R.string.library_empty),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (current != null) Color.White.copy(alpha = 0.94f) else TextSecondary,
                    )
                }
                // Quarter-turn the photo that's on the wall right now (single photo only).
                if (current != null && mirror == null) {
                    Box(
                        Modifier.align(Alignment.TopEnd).padding(10.dp).size(38.dp).clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onRotate() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.RotateRight, contentDescription = stringResource(R.string.rotate), tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

/**
 * Mirrors the wall's current spread inside the "Now showing" card: the same photos in the same
 * arrangement. The planned layouts (Grid, Frames, Gallery wall, …) use the exact same [placeSpread]
 * geometry the wall does, with the wall's own per-slide seed, so they match; Mosaic/Scatter/auto-fill
 * are shown as a close equivalent. Each photo keeps its display rotation, like on the wall.
 */
@Composable
private fun MirrorSpread(screen: DiscoveredScreen, now: com.il90.salongallery.net.NowInfo, rots: Map<String, Int>) {
    @Suppress("NAME_SHADOWING") val rots = rots
    BoxWithConstraints(Modifier.fillMaxSize().clipToBounds().background(Color(0xFF14100B))) {
        val w = maxWidth.value; val h = maxHeight.value
        val n = now.members.size
        val items = remember(now.style, n, w, h, now.seed) { mirrorPlacements(now.style, n, w, h, now.seed) }
        items.forEachIndexed { k, p ->
            val name = now.members.getOrNull(k) ?: return@forEachIndexed
            // Prefer the rotation the wall itself reported for this photo; fall back to the library map.
            val rotStoredAuth = now.rots.getOrNull(k) ?: rots[name] ?: 0
            val circle = p.style == PrintStyle.CIRCLE
            val bordered = p.style != PrintStyle.CELL
            val pad = if (bordered) minOf(p.w, p.h) * 0.05f else 0f
            Box(
                Modifier.align(AbsoluteAlignment.TopLeft)
                    .absoluteOffset(p.x.dp, p.y.dp)
                    .size(p.w.dp, p.h.dp)
                    .graphicsLayer { rotationZ = p.rot }
                    .then(if (circle) Modifier.clip(CircleShape) else Modifier)
                    .then(if (bordered) Modifier.background(Color(0xFFF5F2EC)) else Modifier)
                    .padding(pad.dp),
            ) {
                val rotStored = rotStoredAuth
                val swapped = rotStored == 90 || rotStored == 270
                BoxWithConstraints(Modifier.fillMaxSize().then(if (circle) Modifier.clip(CircleShape) else Modifier).clipToBounds(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = PhotoSender.thumbUrl(screen.host, screen.port, name),
                        contentDescription = null, contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .requiredSize(if (swapped) maxHeight else maxWidth, if (swapped) maxWidth else maxHeight)
                            .graphicsLayer { rotationZ = rotStored.toFloat() },
                    )
                }
            }
        }
    }
}

/** Placements to mirror a wall spread: exact for the planned layouts, a close stand-in for the rest. */
private fun mirrorPlacements(style: String, n: Int, w: Float, h: Float, seed: Int): List<Placement> = when (style) {
    "scatter" -> placeSpread(SpreadStyle.POLAROID, n.coerceIn(3, 5), w, h, seed).items
    "mosaic", "collage" -> placeSpread(SpreadStyle.GRID, n.coerceIn(3, 5), w, h, seed).items
    else -> runCatching { placeSpread(SpreadStyle.valueOf(style.uppercase()), n, w, h, seed).items }
        .getOrElse { placeSpread(SpreadStyle.GRID, n.coerceIn(3, 5), w, h, seed).items }
}

/** Horizontal strip of library thumbnails; tap to show one now, or open the manager. */
@Composable
private fun LibraryStrip(
    screen: DiscoveredScreen, items: List<String>, current: Int,
    rots: Map<String, Int> = emptyMap(), onShow: (String) -> Unit,
) {
    if (items.isEmpty()) {
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(ElecSurface)
                .border(1.dp, ElecBorder, RoundedCornerShape(16.dp)).padding(vertical = 30.dp),
            contentAlignment = Alignment.Center,
        ) { Text(stringResource(R.string.library_empty), style = MaterialTheme.typography.bodyMedium, color = TextSecondary) }
        return
    }
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items.take(12).forEachIndexed { i, name ->
            Box(
                Modifier.width(94.dp).height(118.dp).clip(RoundedCornerShape(15.dp)).background(ElecSurfaceElevated)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onShow(name) },
                contentAlignment = Alignment.Center,
            ) {
                // Match the wall: a quarter-turned photo is laid out 118×94, then rotated to fill 94×118.
                val rot = rots[name] ?: 0
                val swapped = rot == 90 || rot == 270
                AsyncImage(
                    model = PhotoSender.thumbUrl(screen.host, screen.port, name),
                    contentDescription = null, contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .requiredSize(if (swapped) 118.dp else 94.dp, if (swapped) 94.dp else 118.dp)
                        .graphicsLayer { rotationZ = rot.toFloat() },
                )
                if (i == current) {
                    Text(
                        stringResource(R.string.badge_now),
                        style = MaterialTheme.typography.labelSmall, color = Color.White,
                        modifier = Modifier.align(Alignment.TopStart).padding(7.dp).clip(RoundedCornerShape(50)).background(GoodGreen).padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
        }
        // No trailing "All ›" tile — "See all ›" in the section header already opens the manager.
    }
}

/** A soft light action tile: tinted icon chip + label. */
@Composable
private fun HomeAction(modifier: Modifier, icon: ImageVector, label: String, tint: Color, tintBg: Color, enabled: Boolean, onClick: () -> Unit) {
    Column(
        modifier.clip(RoundedCornerShape(18.dp)).background(ElecSurface).border(1.dp, ElecBorder, RoundedCornerShape(18.dp))
            .clickable(enabled = enabled, interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(tintBg), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(21.dp))
        }
        Spacer(Modifier.height(8.dp))
        // Reserve two lines so labels like "Photos & video" wrap inside the tile and all tiles stay equal height.
        Box(Modifier.fillMaxWidth().height(34.dp).padding(horizontal = 4.dp), contentAlignment = Alignment.Center) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, lineHeight = 15.sp),
                color = TextPrimary, maxLines = 2, textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun LibraryManager(screen: DiscoveredScreen, bottomInset: androidx.compose.ui.unit.Dp = 0.dp, libVersion: Long = 0L, onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf<List<String>>(emptyList()) }
    var current by remember { mutableIntStateOf(0) }
    // Every photo in the collage currently on the wall (not just the anchor), so the list can mark
    // them all "NOW". Empty when the wall is showing a single photo.
    var nowMembers by remember { mutableStateOf<Set<String>>(emptySet()) }
    LaunchedEffect(Unit) {
        while (true) {
            val ni = runCatching { PhotoSender.getNow(screen.host, screen.port) }.getOrNull()
            nowMembers = if (ni != null && ni.members.size > 1 && ni.style != "single") ni.members.toSet() else emptySet()
            kotlinx.coroutines.delay(2500)
        }
    }
    var pinned by remember { mutableStateOf<Set<String>>(emptySet()) }
    var durations by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var byteMap by remember { mutableStateOf<Map<String, Long>>(emptyMap()) }
    var dimMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var albums by remember { mutableStateOf<List<AlbumInfo>>(emptyList()) }
    var activeId by remember { mutableStateOf("all") }
    var loading by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    // Keep the phone awake while uploading from the library manager too.
    val libKeepOnView = androidx.compose.ui.platform.LocalView.current
    DisposableEffect(busy) {
        libKeepOnView.keepScreenOn = busy
        onDispose { libKeepOnView.keepScreenOn = false }
    }
    var showNew by remember { mutableStateOf(false) }
    var addTarget by remember { mutableStateOf<List<String>?>(null) }
    var studioPhoto by remember { mutableStateOf<String?>(null) }
    var durationTarget by remember { mutableStateOf<String?>(null) }
    var rotMap by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    // Multi-select: long-press a row to start, tap rows to toggle, then act on all of them at once.
    var selecting by remember { mutableStateOf(false) }
    val picked = remember { mutableStateListOf<String>() }
    var confirmDelete by remember { mutableStateOf(false) }
    var confirmAlbumDelete by remember { mutableStateOf(false) }
    fun exitSelect() { selecting = false; picked.clear() }
    fun toggle(name: String) {
        if (name in picked) picked.remove(name) else picked.add(name)
        if (picked.isEmpty()) selecting = false
    }

    fun refresh() {
        scope.launch {
            val l = PhotoSender.getList(screen.host, screen.port)
            if (l != null) {
                items = l.items; current = l.current; activeId = l.albumId; pinned = l.pinned
                durations = l.durations; byteMap = l.bytes; dimMap = l.dims; rotMap = l.rots
                // Drop selections for photos that no longer exist (deleted elsewhere / album switched).
                picked.retainAll { it in l.items }
                if (picked.isEmpty()) selecting = false
            }
            val a = PhotoSender.getAlbums(screen.host, screen.port)
            if (a != null) albums = a.albums
            loading = false
        }
    }
    LaunchedEffect(Unit) { refresh() }
    // Follow the Display: if photos are added/removed/cleared while this is open, re-list.
    LaunchedEffect(libVersion) { if (libVersion != 0L) refresh() }

    var pendingMedia by remember { mutableStateOf<List<Uri>?>(null) }
    var pendingUpload by remember { mutableStateOf<Pair<List<Uri>, String?>?>(null) }
    val libPrefs = remember { context.getSharedPreferences("salon_remote", android.content.Context.MODE_PRIVATE) }
    // Hand the batch to the background upload service so it finishes even if the app is closed.
    fun uploadMedia(uris: List<Uri>, album: String?, quality: UploadQuality) {
        if (uris.isEmpty()) return
        UploadManager.enqueue(context, screen.host, screen.port, album, quality, uris)
    }
    fun beginUpload(uris: List<Uri>, album: String?) { pendingUpload = uris to album }
    // Reflect upload progress and refresh the album list each time a batch finishes.
    LaunchedEffect(Unit) {
        var wasRunning = false
        UploadManager.progress.collect { up ->
            busy = up.running
            if (wasRunning && !up.running) refresh()
            wasRunning = up.running
        }
    }
    // The gallery Photo Picker for adding into an album.
    val addPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        // Inside an album, add straight into it; from "All", ask where these should go.
        if (activeId != "all") beginUpload(uris, activeId) else pendingMedia = uris
    }

    // Media-type filter: "all" (default) | "photo" | "video". Purely a view over `items` — the
    // library order, reorder and server calls always use the full list.
    var mediaFilter by remember { mutableStateOf("all") }
    var addMenu by remember { mutableStateOf(false) }   // All / Photos / Videos chooser for the album "+"
    val shown = remember(items, mediaFilter) {
        when (mediaFilter) {
            "photo" -> items.filterNot { it.startsWith("v_") }
            "video" -> items.filter { it.startsWith("v_") }
            else -> items
        }
    }
    val nPhotos = items.count { !it.startsWith("v_") }
    val nVideos = items.size - nPhotos

    val lazyState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyState) { from, to ->
        // Dragging within a filtered view: move the dragged item next to its visible neighbour
        // in the FULL list, so the library order stays consistent with what the user saw.
        val dragged = shown.getOrNull(from.index) ?: return@rememberReorderableLazyListState
        val anchor = shown.getOrNull(to.index) ?: return@rememberReorderableLazyListState
        items = items.toMutableList().apply {
            remove(dragged)
            val at = indexOf(anchor).let { if (to.index > from.index) it + 1 else it }
            add(at.coerceIn(0, size), dragged)
        }
    }
    val isAll = activeId == "all"

    // Back while selecting just leaves selection mode; a second back closes the manager.
    Dialog(onDismissRequest = { if (selecting) exitSelect() else onClose() }, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(Modifier.fillMaxSize().background(com.il90.salongallery.ui.theme.ElecBg)) {
            SalonBackground {
                Column(Modifier.fillMaxSize().safeDrawingPadding().padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp + bottomInset)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (selecting) {
                            // Selection header: count + bulk actions (select all · add to album · delete).
                            RoundIconBtn(Icons.Outlined.Close, desc = stringResource(R.string.cancel)) { exitSelect() }
                            Spacer(Modifier.size(14.dp))
                            Text(stringResource(R.string.selected_count, picked.size), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                            Spacer(Modifier.weight(1f))
                            // Select-all acts on what's visible, so "Videos → select all → delete" can't touch photos.
                            RoundIconBtn(Icons.Outlined.SelectAll, accent = shown.isNotEmpty() && picked.containsAll(shown), desc = stringResource(R.string.select_all)) {
                                if (picked.containsAll(shown)) picked.removeAll(shown) else { picked.removeAll(shown); picked.addAll(shown) }
                            }
                            Spacer(Modifier.size(8.dp))
                            RoundIconBtn(Icons.Outlined.Folder, desc = stringResource(R.string.album_add_to)) {
                                if (picked.isNotEmpty()) addTarget = picked.toList()
                            }
                            Spacer(Modifier.size(8.dp))
                            val red = Color(0xFFF87171)
                            Box(
                                Modifier.size(42.dp).clip(RoundedCornerShape(50)).border(1.dp, red.copy(alpha = 0.6f), RoundedCornerShape(50))
                                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                        if (picked.isNotEmpty()) confirmDelete = true
                                    },
                                contentAlignment = Alignment.Center,
                            ) { Icon(Icons.Outlined.DeleteSweep, stringResource(R.string.delete), tint = red, modifier = Modifier.size(22.dp)) }
                        } else {
                            RoundIconBtn(Icons.AutoMirrored.Outlined.ArrowBack) { onClose() }
                            Spacer(Modifier.size(14.dp))
                            Text(
                                "${stringResource(R.string.library_title)} · " + if (mediaFilter == "all") "${items.size}" else "${shown.size}/${items.size}",
                                style = MaterialTheme.typography.headlineSmall, color = TextPrimary,
                            )
                            Spacer(Modifier.weight(1f))
                            // Jump straight to the photo currently on the wall — no scrolling to hunt for it.
                            val nowName = items.getOrNull(current)
                            val nowInShown = nowName?.let { shown.indexOf(it) } ?: -1
                            if (nowInShown >= 0) {
                                RoundIconBtn(Icons.Outlined.Tv, desc = stringResource(R.string.library_jump_now)) {
                                    scope.launch { lazyState.animateScrollToItem(nowInShown) }
                                }
                                Spacer(Modifier.size(8.dp))
                            }
                            if (busy) CircularProgressIndicator(color = NeonCyan, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                            else Box {
                                RoundIconBtn(Icons.Outlined.Add, accent = true) { addMenu = true }
                                MediaKindMenu(addMenu, { addMenu = false }) { kind -> addMenu = false; addPicker.launch(PickVisualMediaRequest(visualTypeFor(kind))) }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    // Album chips
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AlbumChip(stringResource(R.string.album_all), isAll) {
                            scope.launch { PhotoSender.setActiveAlbum(screen.host, screen.port, "all"); refresh() }
                        }
                        albums.forEach { al ->
                            // Wrap the count + size in an LTR isolate (U+2066 … U+2069) so a
                            // Hebrew album name can't scramble the order into "118.4 · 187 … MB".
                            // Chars are built from code points at runtime (not string literals)
                            // so the source stays ASCII — lint rejects literal/escaped bidi
                            // control chars in string constants as BidiSpoofing.
                            val lri = String(Character.toChars(0x2066))
                            val pdi = String(Character.toChars(0x2069))
                            val sizePart = formatBytes(al.bytes).takeIf { it.isNotBlank() }?.let { " · $it" } ?: ""
                            val meta = lri + al.count + sizePart + pdi
                            AlbumChip("${al.name} · $meta", activeId == al.id) {
                                scope.launch { PhotoSender.setActiveAlbum(screen.host, screen.port, al.id); refresh() }
                            }
                        }
                        NewAlbumChip { showNew = true }
                    }
                    // Photos / videos / everything — only worth showing once both kinds exist.
                    if (nPhotos > 0 && nVideos > 0) {
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            AlbumChip(stringResource(R.string.filter_all), mediaFilter == "all") { mediaFilter = "all" }
                            AlbumChip("${stringResource(R.string.filter_photos)} · $nPhotos", mediaFilter == "photo") { mediaFilter = "photo" }
                            AlbumChip("${stringResource(R.string.filter_videos)} · $nVideos", mediaFilter == "video") { mediaFilter = "video" }
                        }
                    }
                    if (!isAll) {
                        Spacer(Modifier.height(6.dp))
                        // Delete-this-album is now a small red trash icon (not a text link); it confirms
                        // first because a stray tap here removes the album AND the photos inside it.
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SmallBtn(Icons.Outlined.Delete, Color(0xFFF87171), desc = stringResource(R.string.album_delete)) {
                                confirmAlbumDelete = true
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(stringResource(R.string.library_hint), style = MaterialTheme.typography.labelMedium, color = TextTertiary)
                    if (!selecting && shown.size > 1) {
                        Text(stringResource(R.string.library_hint_select), style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                    }
                    Spacer(Modifier.height(12.dp))
                    when {
                        loading -> Box(Modifier.fillMaxWidth().padding(40.dp), Alignment.Center) { CircularProgressIndicator(color = NeonCyan) }
                        items.isEmpty() -> Text(
                            stringResource(if (isAll) R.string.library_empty else R.string.album_empty),
                            style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
                        )
                        shown.isEmpty() -> Text(
                            stringResource(if (mediaFilter == "video") R.string.filter_none_videos else R.string.filter_none_photos),
                            style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
                        )
                        else -> LazyColumn(state = lazyState, modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            itemsIndexed(shown, key = { _, n -> n }) { _, name ->
                                ReorderableItem(reorderState, key = name) { isDragging ->
                                    // Position in the FULL library — "#n", NOW/NEXT and delete all refer to it.
                                    val i = items.indexOf(name)
                                    // "NOW" marks the anchor AND every other photo in the collage on screen.
                                    val isNow = i == current || name in nowMembers
                                    val isNext = items.size > 1 && i == (current + 1) % items.size && !isNow
                                    val isPinned = name in pinned
                                    val isVideo = name.startsWith("v_")
                                    val isPicked = name in picked
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .border(
                                                if (isNow || isDragging || isPicked) 1.5.dp else 1.dp,
                                                if (isPicked) NeonCyan else if (isDragging) NeonViolet else if (isNow) NeonCyan else ElecBorder,
                                                RoundedCornerShape(16.dp),
                                            )
                                            .background(if (isPicked) NeonCyan.copy(alpha = 0.10f) else ElecSurface)
                                            // Long-press starts multi-select; while selecting, a tap toggles the row.
                                            .combinedClickable(
                                                interactionSource = remember { MutableInteractionSource() }, indication = null,
                                                onClick = { if (selecting) toggle(name) },
                                                onLongClick = { selecting = true; if (name !in picked) picked.add(name) },
                                            )
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        if (selecting) {
                                            Icon(
                                                if (isPicked) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                                contentDescription = null,
                                                tint = if (isPicked) NeonCyan else TextTertiary, modifier = Modifier.size(22.dp),
                                            )
                                            Spacer(Modifier.size(10.dp))
                                        }
                                        Box(
                                            Modifier.size(58.dp).clip(RoundedCornerShape(10.dp)).background(ElecSurfaceElevated)
                                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                                    if (selecting) toggle(name)
                                                    else scope.launch { PhotoSender.showNow(screen.host, screen.port, name); refresh() }
                                                },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            // Square thumb, so a quarter turn still fills it.
                                            val rot = rotMap[name] ?: 0
                                            AsyncImage(
                                                model = PhotoSender.thumbUrl(screen.host, screen.port, name),
                                                contentDescription = null, contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize().graphicsLayer { rotationZ = rot.toFloat() },
                                            )
                                            if (isVideo) Box(
                                                Modifier.size(24.dp).clip(RoundedCornerShape(50)).background(Color.Black.copy(alpha = 0.5f)),
                                                contentAlignment = Alignment.Center,
                                            ) { Icon(Icons.Outlined.PlayArrow, contentDescription = "video", tint = Color.White, modifier = Modifier.size(16.dp)) }
                                        }
                                        Spacer(Modifier.size(12.dp))
                                        Column(Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text("#${i + 1}", style = MaterialTheme.typography.labelMedium, color = TextTertiary)
                                                Text(
                                                    stringResource(if (isVideo) R.string.media_video else R.string.media_photo),
                                                    style = MaterialTheme.typography.labelMedium, color = if (isVideo) NeonBlue else TextSecondary,
                                                )
                                                if (isPinned) Icon(Icons.Outlined.PushPin, contentDescription = "pinned", tint = NeonCyan, modifier = Modifier.size(15.dp))
                                            }
                                            run {
                                                val dim = dimMap[name]
                                                val b = byteMap[name] ?: 0L
                                                val sizeLabel = formatBytes(b)
                                                val meta = listOfNotNull(dim?.takeIf { it.isNotBlank() }, sizeLabel.takeIf { it.isNotBlank() }).joinToString("  ·  ")
                                                if (meta.isNotBlank()) {
                                                    Spacer(Modifier.height(2.dp))
                                                    Text(meta, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                                                }
                                            }
                                            Spacer(Modifier.height(3.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                if (isNow) Badge(stringResource(R.string.badge_now), NeonCyan)
                                                else if (isNext) Badge(stringResource(R.string.badge_next), NeonViolet)
                                                val sec = durations[name] ?: 0
                                                val durLabel = when {
                                                    sec > 0 -> if (sec >= 60) "${sec / 60}m" else "${sec}s"
                                                    isVideo -> stringResource(R.string.duration_full)
                                                    else -> stringResource(R.string.duration_default_short)
                                                }
                                                Row(
                                                    Modifier.clip(RoundedCornerShape(50)).border(1.dp, ElecBorder, RoundedCornerShape(50))
                                                        // In select mode every control on the row must toggle selection, not open its own sheet.
                                                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                                            if (selecting) toggle(name) else durationTarget = name
                                                        }
                                                        .padding(horizontal = 9.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                ) {
                                                    Icon(Icons.Outlined.Timer, null, tint = TextSecondary, modifier = Modifier.size(13.dp))
                                                    Text(durLabel, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                                }
                                            }
                                        }
                                        if (!selecting) {
                                            Icon(
                                                Icons.Outlined.DragIndicator, contentDescription = "Drag to reorder",
                                                tint = TextSecondary,
                                                modifier = Modifier
                                                    .size(38.dp).padding(8.dp)
                                                    .draggableHandle(
                                                        onDragStopped = { scope.launch { PhotoSender.reorder(screen.host, screen.port, items) } },
                                                    ),
                                            )
                                            RowOverflow(
                                                isAll = isAll,
                                                isPinned = isPinned,
                                                onEdit = { studioPhoto = name },
                                                onRotate = { scope.launch { PhotoSender.rotatePhoto(screen.host, screen.port, name); refresh() } },
                                                onDuration = { durationTarget = name },
                                                onPin = { scope.launch { PhotoSender.setPinned(screen.host, screen.port, name, !isPinned); refresh() } },
                                                onAddAlbum = { addTarget = listOf(name) },
                                                onRemoveFromAlbum = {
                                                    items = items.filterIndexed { j, _ -> j != i }
                                                    scope.launch {
                                                        PhotoSender.removeFromAlbum(screen.host, screen.port, activeId, name)
                                                        refresh()
                                                    }
                                                },
                                                // Always a real delete from the device — in an album too, so
                                                // "deleted" photos don't linger in All / on the wall.
                                                onDelete = {
                                                    items = items.filterIndexed { j, _ -> j != i }
                                                    scope.launch {
                                                        PhotoSender.deletePhoto(screen.host, screen.port, name)
                                                        refresh()
                                                    }
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pendingMedia?.let { uris ->
        DestinationSheet(
            count = uris.size, albums = albums,
            onPick = { id -> pendingMedia = null; beginUpload(uris, id) },
            onCreate = { name ->
                pendingMedia = null
                scope.launch {
                    val id = PhotoSender.createAlbum(screen.host, screen.port, name)
                    if (id != null) beginUpload(uris, id) else refresh()
                }
            },
            onDismiss = { pendingMedia = null },
        )
    }
    pendingUpload?.let { (uris, album) ->
        QualityChooser(
            count = uris.size,
            initial = runCatching { UploadQuality.valueOf(libPrefs.getString("upload_quality", "") ?: "") }.getOrDefault(UploadQuality.OPTIMIZED),
            onPick = { q ->
                libPrefs.edit().putString("upload_quality", q.name).apply()
                pendingUpload = null
                uploadMedia(uris, album, q)
            },
            onDismiss = { pendingUpload = null },
        )
    }
    if (showNew) {
        NewAlbumDialog(
            onCreate = { nm ->
                showNew = false
                scope.launch {
                    val id = PhotoSender.createAlbum(screen.host, screen.port, nm)
                    if (id != null) PhotoSender.setActiveAlbum(screen.host, screen.port, id)
                    refresh()
                }
            },
            onDismiss = { showNew = false },
        )
    }
    addTarget?.let { photos ->
        AddToAlbumSheet(
            albums = albums,
            onPick = { id ->
                addTarget = null
                scope.launch {
                    photos.forEach { PhotoSender.addToAlbum(screen.host, screen.port, id, it) }
                    exitSelect(); refresh()
                }
            },
            onNew = { addTarget = null; showNew = true },
            onDismiss = { addTarget = null },
        )
    }
    if (confirmDelete) {
        val n = picked.size
        val red = Color(0xFFF87171)
        fun doDelete() {
            confirmDelete = false
            val names = picked.toList()
            items = items.filter { it !in names }   // optimistic, so the list reacts instantly
            scope.launch { PhotoSender.deletePhotos(screen.host, screen.port, names); exitSelect(); refresh() }
        }
        fun doRemoveFromAlbum() {
            confirmDelete = false
            val names = picked.toList()
            items = items.filter { it !in names }
            scope.launch { names.forEach { PhotoSender.removeFromAlbum(screen.host, screen.port, activeId, it) }; exitSelect(); refresh() }
        }
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            containerColor = com.il90.salongallery.ui.theme.ElecSurface,
            title = { Text(stringResource(R.string.delete), color = TextPrimary) },
            text = {
                Text(
                    stringResource(if (isAll) R.string.delete_selected_confirm else R.string.delete_permanent_confirm, n),
                    color = TextSecondary,
                )
            },
            confirmButton = {
                // In an album, deleting and un-filing are different actions — offer both, delete first.
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = { doDelete() }) { Text(stringResource(R.string.delete), color = red) }
                    if (!isAll) TextButton(onClick = { doRemoveFromAlbum() }) {
                        Text(stringResource(R.string.album_remove_from), color = NeonBlue)
                    }
                }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text(stringResource(R.string.cancel), color = TextSecondary) } },
        )
    }
    if (confirmAlbumDelete) {
        val count = albums.find { it.id == activeId }?.count ?: items.size
        AlertDialog(
            onDismissRequest = { confirmAlbumDelete = false },
            containerColor = com.il90.salongallery.ui.theme.ElecSurface,
            title = { Text(stringResource(R.string.album_delete_title), color = TextPrimary) },
            text = { Text(stringResource(R.string.album_delete_confirm, count), color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    confirmAlbumDelete = false
                    val id = activeId
                    scope.launch {
                        PhotoSender.deleteAlbum(screen.host, screen.port, id)
                        PhotoSender.setActiveAlbum(screen.host, screen.port, "all")
                        refresh()
                    }
                }) { Text(stringResource(R.string.delete), color = Color(0xFFF87171)) }
            },
            dismissButton = { TextButton(onClick = { confirmAlbumDelete = false }) { Text(stringResource(R.string.cancel), color = TextSecondary) } },
        )
    }
    studioPhoto?.let { photo ->
        StudioDialog(
            screen = screen,
            photo = photo,
            bottomInset = bottomInset,
            onShowNow = { scope.launch { PhotoSender.showNow(screen.host, screen.port, photo); refresh() } },
            onClose = { studioPhoto = null; refresh() },
        )
    }
    durationTarget?.let { name ->
        DurationDialog(screen = screen, name = name, onClose = { durationTarget = null; refresh() })
    }
}

@Composable
private fun RowOverflow(
    isAll: Boolean, isPinned: Boolean,
    onEdit: () -> Unit, onRotate: () -> Unit, onDuration: () -> Unit, onPin: () -> Unit,
    onAddAlbum: () -> Unit, onRemoveFromAlbum: () -> Unit, onDelete: () -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Box {
        SmallBtn(Icons.Outlined.MoreVert, TextSecondary) { open = true }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }, containerColor = com.il90.salongallery.ui.theme.ElecSurface) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.studio_edit), color = TextPrimary) },
                leadingIcon = { Icon(Icons.Outlined.Tune, null, tint = NeonCyan) },
                onClick = { open = false; onEdit() },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.rotate), color = TextPrimary) },
                leadingIcon = { Icon(Icons.Outlined.RotateRight, null, tint = NeonCyan) },
                onClick = { open = false; onRotate() },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.duration_title), color = TextPrimary) },
                leadingIcon = { Icon(Icons.Outlined.Timer, null, tint = NeonBlue) },
                onClick = { open = false; onDuration() },
            )
            DropdownMenuItem(
                text = { Text(stringResource(if (isPinned) R.string.unpin else R.string.pin_to_front), color = TextPrimary) },
                leadingIcon = { Icon(Icons.Outlined.PushPin, null, tint = NeonCyan) },
                onClick = { open = false; onPin() },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.album_add_to), color = TextPrimary) },
                leadingIcon = { Icon(Icons.Outlined.Folder, null, tint = NeonTeal) },
                onClick = { open = false; onAddAlbum() },
            )
            // Inside an album: "Remove from album" only un-files the photo (it stays on the device),
            // while "Delete from device" really removes it everywhere. In "All" there is just Delete.
            if (!isAll) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.album_remove_from), color = TextPrimary) },
                    leadingIcon = { Icon(Icons.Outlined.Close, null, tint = NeonBlue) },
                    onClick = { open = false; onRemoveFromAlbum() },
                )
            }
            DropdownMenuItem(
                text = { Text(stringResource(if (isAll) R.string.delete else R.string.delete_permanent), color = Color(0xFFF87171)) },
                leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = Color(0xFFF87171)) },
                onClick = { open = false; onDelete() },
            )
        }
    }
}

/** Per-item display duration chooser. */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun DurationDialog(screen: DiscoveredScreen, name: String, onClose: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val isVideo = name.startsWith("v_")
    var sec by remember { mutableIntStateOf(0) }
    // The current slideshow default (seconds) that a "Default" photo waits — shown on the Default row.
    var defaultSec by remember { mutableIntStateOf(30) }
    LaunchedEffect(name) {
        sec = PhotoSender.getDuration(screen.host, screen.port, name)
        PhotoSender.getInfo(screen.host, screen.port)?.let { defaultSec = (it.intervalMs / 1000L).toInt().coerceAtLeast(1) }
    }
    fun fmt(s: Int): String = when {
        s <= 0 -> "0s"
        s % 3600 == 0 -> "${s / 3600}h"
        s >= 3600 && (s % 60 == 0) -> "${s / 3600}h ${(s % 3600) / 60}m"
        s % 60 == 0 -> "${s / 60}m"
        else -> "${s / 60}m ${s % 60}s"
    }
    // Pick a tasteful set of values per unit; everything is stored in SECONDS.
    val secondsValues = listOf(3, 5, 10, 15, 20, 30, 45)
    val minuteValues = listOf(1, 2, 3, 5, 10, 15, 20, 30, 45)
    val hourValues = listOf(1, 2, 3, 4, 6, 8, 12, 24)
    // Which tab to open on: match whatever this photo is already set to.
    var unit by remember(sec) {
        mutableIntStateOf(when { sec <= 0 -> 0; sec % 3600 == 0 -> 2; sec % 60 == 0 -> 1; else -> 0 })
    }
    val values = when (unit) { 2 -> hourValues; 1 -> minuteValues; else -> secondsValues }
    val mult = when (unit) { 2 -> 3600; 1 -> 60; else -> 1 }

    ModalBottomSheet(onDismissRequest = onClose, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text(stringResource(R.string.duration_title), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(if (isVideo) R.string.duration_hint_video else R.string.duration_hint_photo),
                style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
            )
            if (!isVideo) {
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.duration_default_hint), style = MaterialTheme.typography.bodySmall, color = NeonCyan)
            }
            Spacer(Modifier.height(16.dp))

            // The "Default" (or "Full clip") row.
            DurationRow(
                label = if (isVideo) stringResource(R.string.duration_full) else "${stringResource(R.string.duration_default)} · ${fmt(defaultSec)}",
                sub = null, selected = sec == 0,
                onClick = { sec = 0; scope.launch { PhotoSender.setDuration(screen.host, screen.port, name, 0) } },
                onLongClick = null,
            )

            if (!isVideo) {
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.duration_unit), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                SegRow(
                    listOf(stringResource(R.string.unit_seconds), stringResource(R.string.unit_minutes), stringResource(R.string.unit_hours)),
                    unit,
                ) { unit = it }
                Spacer(Modifier.height(12.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                values.forEach { v ->
                    val secValue = v * mult
                    DurationRow(
                        label = fmt(secValue),
                        sub = if (secValue == defaultSec) stringResource(R.string.duration_is_default) else null,
                        selected = secValue == sec,
                        onClick = { sec = secValue; scope.launch { PhotoSender.setDuration(screen.host, screen.port, name, secValue) } },
                        // Long-press → make it the default for EVERY "Default" photo.
                        onLongClick = if (isVideo) null else {
                            {
                                defaultSec = secValue
                                scope.launch { PhotoSender.setDefaultDuration(screen.host, screen.port, secValue) }
                                android.widget.Toast.makeText(context, context.getString(R.string.duration_default_set, fmt(secValue)), android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

/** One selectable duration row, with tap (set this photo) and optional long-press (set the default). */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DurationRow(label: String, sub: String?, selected: Boolean, onClick: () -> Unit, onLongClick: (() -> Unit)?) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .border(if (selected) 1.5.dp else 1.dp, if (selected) NeonCyan else ElecBorder, RoundedCornerShape(14.dp))
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() }, indication = null,
                onClick = onClick, onLongClick = onLongClick,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            if (sub != null) Text(sub, style = MaterialTheme.typography.labelSmall, color = NeonCyan)
        }
        if (selected) Icon(Icons.Outlined.Check, null, tint = NeonCyan)
    }
}

@Composable
private fun StudioDialog(screen: DiscoveredScreen, photo: String, bottomInset: androidx.compose.ui.unit.Dp = 0.dp, onShowNow: () -> Unit, onClose: () -> Unit) {
    val scope = rememberCoroutineScope()
    var scale by remember { mutableFloatStateOf(1f) }
    var offX by remember { mutableFloatStateOf(0f) }
    var offY by remember { mutableFloatStateOf(0f) }
    var aspect by remember { mutableFloatStateOf(0.46f) }
    var boxW by remember { mutableIntStateOf(0) }
    var boxH by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        PhotoSender.getInfo(screen.host, screen.port)?.let { if (it.widthPx > 0 && it.heightPx > 0) aspect = it.widthPx.toFloat() / it.heightPx }
        PhotoSender.getTransform(screen.host, screen.port, photo)?.let { scale = it.scale; offX = it.x; offY = it.y }
    }

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = true)) {
        Box(Modifier.fillMaxSize().background(com.il90.salongallery.ui.theme.ElecBg)) {
            SalonBackground {
                // decorFitsSystemWindows=true lets Android inset the dialog for the system bars
                // natively, so the Save button reliably clears the nav bar on every device.
                Column(Modifier.fillMaxSize().navigationBarsPadding().safeDrawingPadding().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 40.dp + bottomInset)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RoundIconBtn(Icons.AutoMirrored.Outlined.ArrowBack) { onClose() }
                        Spacer(Modifier.size(14.dp))
                        Text(stringResource(R.string.studio_title), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(stringResource(R.string.studio_hint), style = MaterialTheme.typography.labelMedium, color = TextTertiary)

                    // Preview box in the exact screen aspect ratio, fitted within the
                    // available height so the action buttons always stay on screen
                    // (a portrait screen aspect would otherwise be taller than the phone).
                    Box(
                        Modifier.weight(1f).fillMaxWidth().padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        BoxWithConstraints {
                            val maxWpx = constraints.maxWidth.toFloat()
                            val maxHpx = constraints.maxHeight.toFloat()
                            val fitByWidthH = if (aspect > 0f) maxWpx / aspect else maxHpx
                            val useW: Float
                            val useH: Float
                            if (fitByWidthH <= maxHpx) { useW = maxWpx; useH = fitByWidthH }
                            else { useH = maxHpx; useW = maxHpx * aspect }
                            val density = LocalDensity.current
                            Box(
                                Modifier
                                    .width(with(density) { useW.toDp() })
                                    .height(with(density) { useH.toDp() })
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.Black)
                                    .border(1.dp, ElecBorder, RoundedCornerShape(14.dp))
                                    .onSizeChanged { boxW = it.width; boxH = it.height }
                                    .pointerInput(Unit) {
                                        detectTransformGestures { _, pan, zoom, _ ->
                                            scale = (scale * zoom).coerceIn(1f, 5f)
                                            if (boxW > 0) offX = (offX + pan.x / boxW).coerceIn(-0.5f, 0.5f)
                                            if (boxH > 0) offY = (offY + pan.y / boxH).coerceIn(-0.5f, 0.5f)
                                        }
                                    },
                            ) {
                                AsyncImage(
                                    model = PhotoSender.fullUrl(screen.host, screen.port, photo),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().graphicsLayer {
                                        scaleX = scale; scaleY = scale
                                        translationX = offX * size.width; translationY = offY * size.height
                                    },
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlineButton(
                            text = stringResource(R.string.studio_reset),
                            modifier = Modifier.weight(1f),
                            onClick = { scale = 1f; offX = 0f; offY = 0f },
                        )
                        OutlineButton(
                            text = stringResource(R.string.studio_shownow),
                            modifier = Modifier.weight(1f),
                            onClick = onShowNow,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    GradientButton(
                        text = stringResource(R.string.studio_save),
                        onClick = {
                            scope.launch {
                                PhotoSender.setTransform(screen.host, screen.port, photo, scale, offX, offY)
                                onClose()
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumChip(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.height(36.dp).clip(RoundedCornerShape(50))
            .then(if (active) Modifier.background(AccentGradient) else Modifier.border(1.dp, ElecBorder, RoundedCornerShape(50)))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = if (active) Color(0xFF07121F) else TextPrimary)
    }
}

/**
 * Keeps the submit button for a form visible while a text field above it is focused. The field
 * raises itself above the keyboard on its own; this additionally scrolls [req]'s target (put the
 * button in a `Modifier.bringIntoViewRequester(req)`) into view so the action isn't hidden behind
 * the IME. Attach `Modifier.revealOnFocus(req)` to each text field that feeds that button.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun rememberRevealRequester(): BringIntoViewRequester = remember { BringIntoViewRequester() }

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Modifier.revealOnFocus(req: BringIntoViewRequester): Modifier {
    val scope = rememberCoroutineScope()
    // The keyboard animates in over a few hundred ms; imePadding grows frame by frame as it does.
    // Re-issue bringIntoView across that window so the final scroll lands against the settled
    // layout — a single early call measures a half-raised keyboard and stops short, leaving the
    // button it should reveal clipped by the IME.
    return this.onFocusEvent {
        if (it.isFocused) scope.launch { repeat(4) { delay(180); req.bringIntoView() } }
    }
}

@Composable
private fun NewAlbumChip(onClick: () -> Unit) {
    Row(
        Modifier.height(36.dp).clip(RoundedCornerShape(50)).border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(50))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(Icons.Outlined.CreateNewFolder, null, tint = NeonCyan, modifier = Modifier.size(16.dp))
        Text(stringResource(R.string.album_new), style = MaterialTheme.typography.labelMedium, color = NeonCyan)
    }
}

@Composable
private fun NewAlbumDialog(onCreate: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = com.il90.salongallery.ui.theme.ElecSurface,
        title = { Text(stringResource(R.string.album_new), color = TextPrimary) },
        text = {
            OutlinedTextField(
                value = name, onValueChange = { name = it }, singleLine = true,
                placeholder = { Text(stringResource(R.string.album_name_hint), color = TextTertiary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                    cursorColor = NeonCyan,
                ),
            )
        },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onCreate(name.trim()) }) { Text(stringResource(R.string.album_create), color = NeonCyan) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel), color = TextSecondary) } },
    )
}

/**
 * After picking media: which album should it go to. The albums are chips (same look as the
 * Library's album bar) — tap one and the upload starts; "+ New album" reveals a name field.
 * With no albums yet the name field is open from the start, since that's the only useful move.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun DestinationSheet(
    count: Int, albums: List<AlbumInfo>,
    onPick: (String?) -> Unit, onCreate: (String) -> Unit, onDismiss: () -> Unit,
) {
    // Re-derive if the list flips between empty/non-empty while open (late fetch), so a stale
    // "no albums" guess can't pin the name field open once albums exist.
    var creating by remember(albums.isEmpty()) { mutableStateOf(albums.isEmpty()) }
    var newName by remember { mutableStateOf("") }
    val createReq = rememberRevealRequester()
    val nameFocus = remember { FocusRequester() }
    fun submit() { if (newName.isNotBlank()) onCreate(newName.trim()) }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).imePadding().padding(24.dp)) {
            Text(stringResource(R.string.add_dest_title, count), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(if (albums.isEmpty()) R.string.add_dest_none_hint else R.string.add_dest_pick_hint),
                style = MaterialTheme.typography.bodySmall, color = TextSecondary,
            )
            Spacer(Modifier.height(16.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                albums.forEach { al -> AlbumChip("${al.name} · ${al.count}", active = false) { onPick(al.id) } }
                AlbumChip(stringResource(R.string.add_dest_all), active = false) { onPick(null) }
                if (!creating) NewAlbumChip { creating = true }
            }
            androidx.compose.animation.AnimatedVisibility(visible = creating) {
                Column {
                    Spacer(Modifier.height(18.dp))
                    Text(stringResource(R.string.album_new), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newName, onValueChange = { newName = it }, singleLine = true,
                        modifier = Modifier.fillMaxWidth().focusRequester(nameFocus).revealOnFocus(createReq),
                        placeholder = { Text(stringResource(R.string.album_name_hint), color = TextTertiary) },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { submit() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
                        ),
                    )
                    Spacer(Modifier.height(10.dp))
                    GradientButton(
                        modifier = Modifier.bringIntoViewRequester(createReq),
                        text = stringResource(R.string.album_create), enabled = newName.isNotBlank(),
                        onClick = { submit() },
                    )
                }
            }
            // Put the cursor in the name field as soon as it appears (first album, or "+ New album").
            LaunchedEffect(creating) { if (creating) { delay(150); runCatching { nameFocus.requestFocus() } } }
            Spacer(Modifier.height(12.dp))
        }
    }
}

/**
 * Asks how photos should be sent — "Optimized" (resized for the wall; recommended) or "Original"
 * (full resolution). Shown on every upload path. Videos are always sent at original quality, so this
 * only affects photos. [initial] is the remembered last choice, shown as the highlighted default.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QualityChooser(count: Int, initial: UploadQuality, onPick: (UploadQuality) -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text(stringResource(R.string.quality_title), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.quality_subtitle, count), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(16.dp))
            QualityOption(
                title = stringResource(R.string.quality_optimized_title),
                desc = stringResource(R.string.quality_optimized_desc),
                badge = stringResource(R.string.quality_recommended),
                highlighted = initial == UploadQuality.OPTIMIZED,
            ) { onPick(UploadQuality.OPTIMIZED) }
            Spacer(Modifier.height(10.dp))
            QualityOption(
                title = stringResource(R.string.quality_original_title),
                desc = stringResource(R.string.quality_original_desc),
                badge = null,
                highlighted = initial == UploadQuality.ORIGINAL,
            ) { onPick(UploadQuality.ORIGINAL) }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun QualityOption(title: String, desc: String, badge: String?, highlighted: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(if (highlighted) 2.dp else 1.dp, if (highlighted) NeonCyan else ElecBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                if (badge != null) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier.clip(RoundedCornerShape(6.dp)).background(NeonCyan.copy(alpha = 0.18f)).padding(horizontal = 8.dp, vertical = 2.dp),
                    ) { Text(badge, style = MaterialTheme.typography.labelSmall, color = NeonCyan) }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddToAlbumSheet(albums: List<AlbumInfo>, onPick: (String) -> Unit, onNew: () -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().padding(24.dp)) {
            Text(stringResource(R.string.album_add_to), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(14.dp))
            if (albums.isEmpty()) {
                Text(stringResource(R.string.album_none_yet), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Spacer(Modifier.height(12.dp))
            }
            albums.forEach { al ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(14.dp))
                        .border(1.dp, ElecBorder, RoundedCornerShape(14.dp))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onPick(al.id) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Folder, null, tint = NeonTeal, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.size(12.dp))
                    Text("${al.name} · ${al.count}", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onNew() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.CreateNewFolder, null, tint = NeonCyan, modifier = Modifier.size(22.dp))
                Spacer(Modifier.size(12.dp))
                Text(stringResource(R.string.album_new), style = MaterialTheme.typography.titleMedium, color = NeonCyan)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RoundIconBtn(icon: ImageVector, accent: Boolean = false, desc: String? = null, onClick: () -> Unit) {
    Box(
        Modifier.size(42.dp).clip(RoundedCornerShape(50))
            .border(1.dp, if (accent) NeonCyan.copy(alpha = 0.5f) else ElecBorder, RoundedCornerShape(50))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
        contentAlignment = Alignment.Center,
    ) { Icon(icon, desc, tint = if (accent) NeonCyan else TextPrimary, modifier = Modifier.size(22.dp)) }
}

@Composable
private fun Badge(text: String, color: Color) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = Modifier.padding(top = 3.dp)
            .clip(RoundedCornerShape(50)).border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

@Composable
private fun SmallBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, desc: String? = null, onClick: () -> Unit) {
    Box(
        Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
        contentAlignment = Alignment.Center,
    ) { Icon(icon, desc, tint = tint, modifier = Modifier.size(22.dp)) }
}

@Composable
private fun ProgressRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CircularProgressIndicator(color = NeonCyan, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
    }
}

/** A compact now-playing bar on the home, with transport controls for the screen's music. */
@Composable
private fun MiniPlayer(state: MusicState, onToggle: () -> Unit, onNext: () -> Unit, onPrev: () -> Unit, onOpen: () -> Unit) {
    val title = state.tracks.getOrNull(state.current)?.let { it.title.ifBlank { it.name } } ?: "—"
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(ElecSurface)
            .border(1.dp, NeonViolet.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onOpen() }
            .padding(start = 14.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Outlined.MusicNote, null, tint = NeonViolet, modifier = Modifier.size(20.dp))
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(stringResource(R.string.music_now_playing), style = MaterialTheme.typography.labelSmall, color = NeonViolet)
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 1, fontFamily = com.il90.salongallery.ui.theme.ContentFont)
        }
        SmallBtn(Icons.Outlined.SkipPrevious, TextSecondary, desc = "Previous") { onPrev() }
        SmallBtn(if (state.playing) Icons.Outlined.Pause else Icons.Outlined.PlayArrow, NeonViolet, desc = "Play/pause") { onToggle() }
        SmallBtn(Icons.Outlined.SkipNext, TextSecondary, desc = "Next") { onNext() }
    }
}

@Composable
private fun CompactSlider(modifier: Modifier, icon: ImageVector, accent: Color, initial: Float, onCommit: (Float) -> Unit) {
    // Re-seed from the live screen value whenever it arrives (no more snapping to the middle).
    var value by remember(initial) { mutableFloatStateOf(initial.coerceIn(0f, 1f)) }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ElecSurface)
            .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(16.dp))
            .padding(start = 12.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp))
        Slider(
            value = value, onValueChange = { value = it }, onValueChangeFinished = { onCommit(value) },
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent, inactiveTrackColor = ElecBorder),
        )
        Text("${(value * 100).roundToInt()}%", style = MaterialTheme.typography.labelMedium, color = TextSecondary, modifier = Modifier.width(38.dp), textAlign = TextAlign.End)
    }
}

@Composable
private fun WideButton(icon: ImageVector, label: String, accent: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .background(ElecSurface)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = accent, modifier = Modifier.size(22.dp)) }
        Spacer(Modifier.size(14.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun SliderTile(modifier: Modifier, icon: ImageVector, label: String, accent: Color, onCommit: (Float) -> Unit) {
    var value by remember { mutableFloatStateOf(0.5f) }
    Column(
        modifier = modifier
            .height(116.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(ElecSurface)
            .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(36.dp).clip(RoundedCornerShape(11.dp)).background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) { Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.weight(1f))
            Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        }
        Spacer(Modifier.weight(1f))
        Text(label, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Slider(
            value = value, onValueChange = { value = it }, onValueChangeFinished = { onCommit(value) },
            colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent, inactiveTrackColor = ElecBorder),
        )
    }
}

@Composable
private fun RssToggle(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RssSheet(screen: DiscoveredScreen, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var on by remember { mutableStateOf(false) }
    val feeds = remember { mutableStateListOf<String>() }
    var input by remember { mutableStateOf("") }
    var pos by remember { mutableStateOf("bottom") }
    var showImage by remember { mutableStateOf(false) }
    var showSource by remember { mutableStateOf(true) }
    var showSummary by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        PhotoSender.getRss(screen.host, screen.port)?.let { s ->
            on = s.on; feeds.clear(); feeds.addAll(s.feeds)
            pos = s.pos; showImage = s.showImage; showSource = s.showSource; showSummary = s.showSummary
        }
    }
    fun push() { scope.launch { PhotoSender.setRss(screen.host, screen.port, on, feeds.toList(), pos, showImage, showSource, showSummary) } }
    val suggestions = listOf(
        // Israeli feeds first.
        "Ynet" to "https://www.ynet.co.il/Integration/StoryRss2.xml",
        "מאקו (Mako)" to "https://rcs.mako.co.il/rss/news-military.xml",
        "וואלה (Walla)" to "https://rss.walla.co.il/feed/1?type=main",
        "כלכליסט" to "https://www.calcalist.co.il/GeneralRSS/0,16335,L-8,00.xml",
        "BBC News" to "https://feeds.bbci.co.uk/news/rss.xml",
        "The Verge" to "https://www.theverge.com/rss/index.xml",
        "NASA" to "https://www.nasa.gov/rss/dyn/breaking_news.rss",
        "Hacker News" to "https://hnrss.org/frontpage",
        "NYT" to "https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml",
    )
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).imePadding().padding(24.dp)) {
            Text(stringResource(R.string.rss_title), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.rss_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.rss_show), style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                Switch(checked = on, onCheckedChange = { on = it; push() })
            }

            if (on) {
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.rss_position), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                val positions = listOf("bottom", "top")
                SegRow(listOf("Bottom", "Top"), positions.indexOf(pos).coerceAtLeast(0)) { pos = positions[it]; push() }

                Spacer(Modifier.height(14.dp))
                RssToggle(stringResource(R.string.rss_show_image), showImage) { showImage = it; push() }
                RssToggle(stringResource(R.string.rss_show_source), showSource) { showSource = it; push() }
                RssToggle(stringResource(R.string.rss_show_summary), showSummary) { showSummary = it; push() }
            }

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = input, onValueChange = { input = it },
                    modifier = Modifier.weight(1f), singleLine = true,
                    placeholder = { Text(stringResource(R.string.rss_add_hint), color = TextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
                        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
                    ),
                )
                SmallBtn(Icons.Outlined.Add, NeonCyan, desc = "Add feed") {
                    val u = input.trim()
                    if (u.isNotBlank() && u !in feeds) { feeds.add(u); input = ""; push() }
                }
            }

            if (feeds.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                feeds.toList().forEach { url ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(12.dp))
                            .border(1.dp, ElecBorder, RoundedCornerShape(12.dp)).padding(start = 14.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(url, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, maxLines = 1, modifier = Modifier.weight(1f))
                        SmallBtn(Icons.Outlined.Close, Color(0xFFF87171), desc = "Remove feed") { feeds.remove(url); push() }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            SectionLabel(stringResource(R.string.rss_suggestions))
            Spacer(Modifier.height(10.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                suggestions.forEach { (name, url) ->
                    val added = url in feeds
                    Box(
                        Modifier.clip(RoundedCornerShape(50))
                            .border(1.dp, if (added) NeonCyan else ElecBorder, RoundedCornerShape(50))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                if (!added) { feeds.add(url); push() }
                            }
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                    ) {
                        Text((if (added) "✓ " else "+ ") + name, style = MaterialTheme.typography.labelMedium, color = if (added) NeonCyan else TextPrimary)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** All transitions as (key, label) for the Effects picker. "None" (no transition) is first and is the
 *  default. */
private val TRANSITIONS = listOf(
    "none" to "None", "fade" to "Fade", "dissolve" to "Dissolve", "slide" to "Slide ←", "slideright" to "Slide →",
    "slideup" to "Slide ↑", "slidedown" to "Slide ↓", "zoom" to "Zoom in", "zoomout" to "Zoom out",
    "reveal" to "Reveal", "grow" to "Grow", "swap" to "Swap", "drift" to "Drift",
    "cardstack" to "Stack", "kenburns" to "Ken Burns",
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun EffectsSheet(
    effect: String, filter: String, pool: Set<String>, filterPool: Set<String>,
    onEffect: (String) -> Unit, onFilter: (String) -> Unit, onPool: (Set<String>) -> Unit,
    onFilterPool: (Set<String>) -> Unit, onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text(stringResource(R.string.effects_title), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)

            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.effects_transition), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(10.dp))
            val isRandom = effect == "random"
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EffectChip("🎲 " + stringResource(R.string.effects_random), selected = isRandom, showCheck = false) {
                    onEffect(if (isRandom) "none" else "random")
                }
                TRANSITIONS.forEach { (key, label) ->
                    val sel = if (isRandom) key in pool else key == effect
                    EffectChip(label, selected = sel, showCheck = isRandom) {
                        if (isRandom) {
                            val np = if (key in pool) pool - key else pool + key
                            if (np.isNotEmpty()) onPool(np)
                        } else onEffect(key)
                    }
                }
            }
            if (isRandom) {
                Spacer(Modifier.height(10.dp))
                val allKeys = TRANSITIONS.map { it.first }.toSet()
                val allOn = pool.containsAll(allKeys)
                EffectChip(stringResource(if (allOn) R.string.pool_clear_all else R.string.pool_select_all), selected = allOn, showCheck = false) {
                    onPool(if (allOn) setOf(TRANSITIONS.first().first) else allKeys)
                }
                Spacer(Modifier.height(10.dp))
                Text(stringResource(R.string.effects_random_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.effects_look), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(10.dp))
            val lookRandom = filter == "random"
            EffectChip("🎲 " + stringResource(R.string.effects_random), selected = lookRandom, showCheck = false) {
                onFilter(if (lookRandom) "none" else "random")
            }
            if (lookRandom) {
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.look_random_pick_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            val allLooks = listOf("none", "mono", "sepia", "warm", "cool", "vignette", "vivid", "noir", "fade", "cinema", "golden", "dusk", "frost", "pop", "matte", "rose")
            if (lookRandom) {
                Spacer(Modifier.height(8.dp))
                val allOn = filterPool.containsAll(allLooks.toSet())
                EffectChip(stringResource(if (allOn) R.string.pool_clear_all else R.string.pool_select_all), selected = allOn, showCheck = false) {
                    onFilterPool(if (allOn) setOf(allLooks.first()) else allLooks.toSet())
                }
            }
            Spacer(Modifier.height(12.dp))
            val looks = allLooks
            val labels = listOf("Original", "Mono", "Sepia", "Warm", "Cool", "Vignette", "Vivid", "Noir", "Fade", "Cinema", "Golden", "Dusk", "Frost", "Pop", "Matte", "Rose")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                looks.forEachIndexed { i, key ->
                    val sel = if (lookRandom) key in filterPool else key == filter
                    LookChip(labels[i], key, sel, showCheck = lookRandom) {
                        if (lookRandom) {
                            val np = if (key in filterPool) filterPool - key else filterPool + key
                            if (np.isNotEmpty()) onFilterPool(np)
                        } else onFilter(key)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** A rounded chip with a centered label and an optional check (used for effects + frames random pool). */
@Composable
private fun EffectChip(label: String, selected: Boolean, showCheck: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.clip(RoundedCornerShape(50))
            .then(if (selected) Modifier.background(NeonCyan.copy(alpha = 0.16f)) else Modifier)
            .border(1.dp, if (selected) NeonCyan else ElecBorder, RoundedCornerShape(50))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (showCheck && selected) Icon(Icons.Outlined.Check, null, tint = NeonCyan, modifier = Modifier.size(15.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = if (selected) NeonCyan else TextPrimary, textAlign = TextAlign.Center)
    }
}

/** A small preview chip for a photo "look" (approximate; the real look renders on the wall). */
@Composable
private fun LookChip(label: String, key: String, selected: Boolean, showCheck: Boolean = false, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
    ) {
        Box(
            Modifier.size(74.dp).clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF8893A0), Color(0xFF454D59))))
                .border(if (selected) 2.dp else 1.dp, if (selected) NeonCyan else ElecBorder, RoundedCornerShape(14.dp)),
        ) {
            val tint = when (key) {
                "mono" -> Color(0xFF8A8A8A).copy(alpha = 0.5f)
                "noir" -> Color(0xFF000000).copy(alpha = 0.35f)
                "sepia" -> Color(0xFF6E4A1E).copy(alpha = 0.42f)
                "warm" -> Color(0xFFFF8A3D).copy(alpha = 0.26f)
                "golden" -> Color(0xFFFFB03A).copy(alpha = 0.3f)
                "cool", "frost" -> Color(0xFF3D7AFF).copy(alpha = 0.26f)
                "cinema" -> Color(0xFF1FA8A0).copy(alpha = 0.26f)
                "dusk" -> Color(0xFF9B5DE5).copy(alpha = 0.28f)
                "rose" -> Color(0xFFFF6FA5).copy(alpha = 0.24f)
                "vivid", "pop" -> Color(0xFFFF3D7F).copy(alpha = 0.2f)
                "fade", "matte" -> Color(0xFFEDE7DB).copy(alpha = 0.28f)
                else -> Color.Transparent
            }
            if (tint != Color.Transparent) Box(Modifier.matchParentSize().background(tint))
            if (key == "vignette") Box(
                Modifier.matchParentSize().background(
                    Brush.radialGradient(0.0f to Color.Transparent, 0.6f to Color.Transparent, 1.0f to Color.Black.copy(alpha = 0.6f))
                )
            )
            if (showCheck && selected) Box(
                Modifier.align(Alignment.TopEnd).padding(4.dp).size(20.dp).clip(RoundedCornerShape(50)).background(NeonCyan),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Outlined.Check, null, tint = Color(0xFF1A1510), modifier = Modifier.size(13.dp)) }
        }
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (selected) NeonCyan else TextSecondary, maxLines = 1)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrameSheet(
    current: Int, width: Float, random: Boolean, pool: Set<Int>,
    onPick: (Int) -> Unit, onWidth: (Float) -> Unit, onRandom: (Boolean) -> Unit, onPool: (Set<Int>) -> Unit, onDismiss: () -> Unit,
) {
    var w by remember { mutableStateOf(width) }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.tile_frame), style = MaterialTheme.typography.headlineSmall, color = TextPrimary, modifier = Modifier.weight(1f))
                Text(
                    stringResource(R.string.frame_recommended),
                    style = MaterialTheme.typography.labelLarge, color = NeonCyan,
                    modifier = Modifier.clip(RoundedCornerShape(50))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            onPick(RECOMMENDED_FRAME.id); w = 1f; onWidth(1f)
                        }.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            Spacer(Modifier.height(14.dp))
            val adaptiveId = remember { FRAMES.firstOrNull { it.adaptive }?.id ?: 50 }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                EffectChip("🎲 " + stringResource(R.string.effects_random), selected = random, showCheck = false) { onRandom(!random) }
                EffectChip("🎨 " + stringResource(R.string.frame_adaptive), selected = !random && current == adaptiveId, showCheck = false) {
                    if (random) onRandom(false); onPick(adaptiveId)
                }
            }
            if (random) {
                Spacer(Modifier.height(10.dp))
                // Pool is every real frame (id 0 = "None" is excluded from the random rotation).
                val allFrameIds = FRAMES.map { it.id }.filter { it != 0 }.toSet()
                val allOn = pool.containsAll(allFrameIds)
                EffectChip(stringResource(if (allOn) R.string.pool_clear_all else R.string.pool_select_all), selected = allOn, showCheck = false) {
                    onPool(if (allOn) setOf(allFrameIds.first()) else allFrameIds)
                }
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.frame_random_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            } else if (current == adaptiveId) {
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.frame_adaptive_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.frame_thickness), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Slider(
                value = w, onValueChange = { w = it }, onValueChangeFinished = { onWidth(w) },
                valueRange = 0.4f..2.2f,
                colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan, inactiveTrackColor = ElecBorder),
            )

            Spacer(Modifier.height(8.dp))
            FRAME_CATEGORIES.forEach { (cat, styles) ->
                SectionLabel(cat)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    styles.forEach { f ->
                        val sel = if (random) f.id in pool else f.id == current && !random
                        FramePreview(f, sel, w, showCheck = random && f.id != 0) {
                            if (random && f.id != 0) {
                                val np = if (f.id in pool) pool - f.id else pool + f.id
                                if (np.isNotEmpty()) onPool(np)
                            } else onPick(f.id)
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

/**
 * The expensive live frame render, split out and keyed only on (frameId, adaptive, width) so Compose
 * SKIPS it when only the selection changes — picking a frame then no longer re-renders the whole grid.
 */
@Composable
private fun FrameThumb(frameId: Int, adaptive: Boolean, width: Float) {
    FramedContent(frameId, width, adaptiveColor = if (adaptive) Color(0xFF6E8CA8) else null, modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF7C8898), Color(0xFF3E4650)))))
    }
}

/** A small live preview of a frame wrapped around a neutral sample, for the picker. */
@Composable
private fun FramePreview(f: FrameStyle, selected: Boolean, width: Float, showCheck: Boolean = false, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(86.dp)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
    ) {
        Box(
            Modifier.size(80.dp).clip(RoundedCornerShape(12.dp))
                .border(if (selected) 2.dp else 1.dp, if (selected) NeonCyan else ElecBorder, RoundedCornerShape(12.dp)),
        ) {
            FrameThumb(f.id, f.adaptive, width)
            if (showCheck && selected) Box(
                Modifier.align(Alignment.TopEnd).padding(4.dp).size(22.dp).clip(RoundedCornerShape(50)).background(NeonCyan),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Outlined.Check, null, tint = Color(0xFF1A1510), modifier = Modifier.size(15.dp)) }
        }
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            if (f.recommended) Text("★", style = MaterialTheme.typography.labelSmall, color = NeonCyan)
            Text(f.name, style = MaterialTheme.typography.labelSmall, color = if (selected) NeonCyan else TextSecondary, maxLines = 1)
        }
    }
}

/** Maps a kind (all | photo | video) to a Photo Picker media-type filter. */
private fun visualTypeFor(kind: String): ActivityResultContracts.PickVisualMedia.VisualMediaType = when (kind) {
    "photo" -> ActivityResultContracts.PickVisualMedia.ImageOnly
    "video" -> ActivityResultContracts.PickVisualMedia.VideoOnly
    else -> ActivityResultContracts.PickVisualMedia.ImageAndVideo
}

/**
 * The single "Add" menu: bring photos, videos or both from the gallery, or send a whole folder at
 * once. Merges what used to be two near-identical buttons into one clear choice. [onFiles] opens the
 * file picker for the chosen kind; [onFolder] opens the folder picker.
 */
@Composable
private fun AddSourceMenu(expanded: Boolean, onDismiss: () -> Unit, onFiles: (String) -> Unit, onFolder: () -> Unit, onGooglePhotos: () -> Unit = {}, hasSyncFolder: Boolean = false, onSync: () -> Unit = {}, onManyFiles: () -> Unit = {}) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss, containerColor = com.il90.salongallery.ui.theme.ElecSurface) {
        DropdownMenuItem(text = { Text(stringResource(R.string.add_photos), color = TextPrimary) }, onClick = { onFiles("photo") },
            leadingIcon = { Icon(Icons.Outlined.Image, null, tint = NeonCyan) })
        DropdownMenuItem(text = { Text(stringResource(R.string.add_videos), color = TextPrimary) }, onClick = { onFiles("video") },
            leadingIcon = { Icon(Icons.Outlined.PlayArrow, null, tint = NeonBlue) })
        // The gallery picker above is Android's Photo Picker, which caps a selection at ~100. This
        // uses the file picker instead, which has no such limit — for sending hundreds at once.
        DropdownMenuItem(text = { Text(stringResource(R.string.add_many_files), color = TextPrimary) }, onClick = { onManyFiles() },
            leadingIcon = { Icon(Icons.Outlined.PhotoLibrary, null, tint = NeonCyan) })
        androidx.compose.material3.HorizontalDivider(color = ElecBorder)
        DropdownMenuItem(text = { Text(stringResource(R.string.add_folder), color = TextPrimary) }, onClick = { onFolder() },
            leadingIcon = { Icon(Icons.Outlined.Folder, null, tint = NeonTeal) })
        DropdownMenuItem(text = { Text(stringResource(R.string.home_gphotos), color = TextPrimary) }, onClick = { onGooglePhotos() },
            leadingIcon = { Icon(Icons.Outlined.Link, null, tint = NeonBlue) })
    }
}

/**
 * A little All / Photos / Videos chooser shown when the user taps an "add" button, so they can
 * bring only photos, only videos, or both. [onPick] gets "all" | "photo" | "video".
 */
@Composable
private fun MediaKindMenu(expanded: Boolean, onDismiss: () -> Unit, onPick: (String) -> Unit) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss, containerColor = com.il90.salongallery.ui.theme.ElecSurface) {
        DropdownMenuItem(text = { Text(stringResource(R.string.filter_all), color = TextPrimary) }, onClick = { onPick("all") },
            leadingIcon = { Icon(Icons.Outlined.PhotoLibrary, null, tint = NeonCyan) })
        DropdownMenuItem(text = { Text(stringResource(R.string.filter_photos), color = TextPrimary) }, onClick = { onPick("photo") },
            leadingIcon = { Icon(Icons.Outlined.Image, null, tint = NeonCyan) })
        DropdownMenuItem(text = { Text(stringResource(R.string.filter_videos), color = TextPrimary) }, onClick = { onPick("video") },
            leadingIcon = { Icon(Icons.Outlined.PlayArrow, null, tint = NeonBlue) })
    }
}

/**
 * Photos and videos under a folder the user granted with OpenDocumentTree (sub-folders included),
 * oldest first by modification time so the wall keeps the camera roll's order. [kind] limits it to
 * all | photo | video.
 */
private fun mediaInTree(context: android.content.Context, tree: Uri, kind: String = "all"): List<Uri> {
    val cr = context.contentResolver
    val found = mutableListOf<Pair<Long, Uri>>()
    val dirs = ArrayDeque<String>().apply { add(android.provider.DocumentsContract.getTreeDocumentId(tree)) }
    val cols = arrayOf(
        android.provider.DocumentsContract.Document.COLUMN_DOCUMENT_ID,
        android.provider.DocumentsContract.Document.COLUMN_MIME_TYPE,
        android.provider.DocumentsContract.Document.COLUMN_LAST_MODIFIED,
    )
    while (dirs.isNotEmpty()) {
        val dir = dirs.removeLast()
        val children = android.provider.DocumentsContract.buildChildDocumentsUriUsingTree(tree, dir)
        runCatching {
            cr.query(children, cols, null, null, null)?.use { c ->
                while (c.moveToNext()) {
                    val id = c.getString(0) ?: continue
                    val mime = c.getString(1) ?: ""
                    when {
                        mime == android.provider.DocumentsContract.Document.MIME_TYPE_DIR -> dirs.add(id)
                        (kind != "video" && mime.startsWith("image/")) || (kind != "photo" && mime.startsWith("video/")) ->
                            found += (if (c.isNull(2)) 0L else c.getLong(2)) to android.provider.DocumentsContract.buildDocumentUriUsingTree(tree, id)
                    }
                }
            }
        }
    }
    return found.sortedBy { it.first }.map { it.second }
}

/** What the add-media pickers offer: every photo and video type. */
private val MEDIA_TYPES = arrayOf("image/*", "video/*")

/**
 * A tappable thumbnail that SHOWS what a layout looks like — little white prints arranged the way
 * the wall will arrange the photos — instead of just its name, so the picker reads at a glance.
 * Built from the exact same [placeSpread] geometry the TV uses, so the preview matches the result.
 */
@Composable
private fun LayoutPreviewTile(key: String, label: String, selected: Boolean, modifier: Modifier = Modifier, check: Boolean? = null, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
    ) {
        Box(
            Modifier.fillMaxWidth().aspectRatio(96f / 62f).clip(RoundedCornerShape(12.dp))
                .background(if (key == "single" || key == "mosaic") Color(0xFF2A2017) else Color(0xFF1C1712))
                .border(if (selected) 2.dp else 1.dp, if (selected) NeonCyan else ElecBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            // When picking which layouts "Mix" uses, each spread tile shows a check in its corner.
            if (check != null) {
                Box(
                    Modifier.align(Alignment.TopEnd).padding(5.dp).size(18.dp).clip(CircleShape)
                        .background(if (check) NeonCyan else Color(0x66000000))
                        .border(1.dp, if (check) NeonCyan else Color.White.copy(0.5f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (check) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                }
            }
            if (key == "auto") {
                // "Auto": a tall print beside two stacked ones — the wall fits the photos itself.
                androidx.compose.foundation.Canvas(Modifier.fillMaxSize().padding(7.dp)) {
                    val c = Color(0xFFF2EEE6)
                    val g = size.width * 0.05f
                    val leftW = size.width * 0.46f
                    drawRoundRect(c, androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Size(leftW, size.height), androidx.compose.ui.geometry.CornerRadius(3f))
                    val rx = leftW + g
                    val rw = size.width - rx
                    val rh = (size.height - g) / 2f
                    drawRoundRect(c, androidx.compose.ui.geometry.Offset(rx, 0f), androidx.compose.ui.geometry.Size(rw, rh), androidx.compose.ui.geometry.CornerRadius(3f))
                    drawRoundRect(c, androidx.compose.ui.geometry.Offset(rx, rh + g), androidx.compose.ui.geometry.Size(rw, rh), androidx.compose.ui.geometry.CornerRadius(3f))
                }
            } else if (key == "random") {
                // "Mix": a few overlapping mini shapes to say "a bit of each layout".
                androidx.compose.foundation.Canvas(Modifier.fillMaxSize().padding(6.dp)) {
                    val c = Color(0xFFF2EEE6)
                    drawRoundRect(c.copy(alpha = 0.55f), androidx.compose.ui.geometry.Offset(size.width * 0.08f, size.height * 0.28f), androidx.compose.ui.geometry.Size(size.width * 0.5f, size.height * 0.5f), androidx.compose.ui.geometry.CornerRadius(3f))
                    withTransform({ rotate(-12f, androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.5f)) }) {
                        drawRoundRect(c.copy(alpha = 0.8f), androidx.compose.ui.geometry.Offset(size.width * 0.3f, size.height * 0.14f), androidx.compose.ui.geometry.Size(size.width * 0.4f, size.height * 0.42f), androidx.compose.ui.geometry.CornerRadius(3f))
                    }
                    drawOval(c, androidx.compose.ui.geometry.Offset(size.width * 0.62f, size.height * 0.46f), androidx.compose.ui.geometry.Size(size.width * 0.3f, size.height * 0.4f))
                }
            } else {
                val vw = 100f; val vh = 64f
                val items = remember(key) { layoutPreviewItems(key, vw, vh) }
                androidx.compose.foundation.Canvas(Modifier.fillMaxSize().padding(5.dp)) {
                    val sx = size.width / vw; val sy = size.height / vh
                    items.forEach { p ->
                        val cx = p.cx * sx; val cy = p.cy * sy
                        val w = p.w * sx; val h = p.h * sy
                        withTransform({ rotate(p.rot, androidx.compose.ui.geometry.Offset(cx, cy)) }) {
                            val topLeft = androidx.compose.ui.geometry.Offset(cx - w / 2f, cy - h / 2f)
                            val sz = androidx.compose.ui.geometry.Size(w, h)
                            if (p.style == PrintStyle.CIRCLE) {
                                drawOval(Color(0xFFF2EEE6), topLeft, sz)
                            } else {
                                drawRoundRect(Color(0xFFF2EEE6), topLeft, sz, androidx.compose.ui.geometry.CornerRadius(2f))
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(5.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (selected) NeonCyan else TextSecondary, maxLines = 1, textAlign = TextAlign.Center)
    }
}

/** The shapes to sketch for a layout's preview tile (same geometry the wall renders). */
private fun layoutPreviewItems(key: String, w: Float, h: Float): List<Placement> = when (key) {
    "single" -> listOf(Placement(w * 0.06f, h * 0.08f, w * 0.88f, h * 0.84f, 0f, PrintStyle.CELL))
    "mosaic" -> {
        val g = minOf(w, h) * 0.05f
        val colW = w * 0.4f - g
        val cellH = (h - 3 * g) / 2f
        listOf(
            Placement(g, g, w * 0.6f - 1.5f * g, h - 2 * g, 0f, PrintStyle.CELL),
            Placement(w * 0.6f + 0.5f * g, g, colW, cellH, 0f, PrintStyle.CELL),
            Placement(w * 0.6f + 0.5f * g, g * 2 + cellH, colW, cellH, 0f, PrintStyle.CELL),
        )
    }
    "scatter" -> placeSpread(SpreadStyle.POLAROID, 5, w, h, 4).items
    else -> runCatching { placeSpread(SpreadStyle.valueOf(key.uppercase()), 5, w, h, 3).items }.getOrDefault(emptyList())
}

/** Slide layouts offered in the Slideshow sheet: key sent to the Display → chip label. */
private val LAYOUTS = listOf(
    // "auto" lets the Display choose the arrangement per slide to suit the photos; "random" (shown
    // as "Mix") mixes every layout — it mixes the layouts, it isn't disorderly.
    "auto" to "Auto", "random" to "Mix", "single" to "Single", "mosaic" to "Mosaic", "scatter" to "Scatter", "grid" to "Grid",
    "window" to "Window", "quilt" to "Quilt", "triptych" to "Triptych", "rows" to "Rows", "columns" to "Columns",
    "masonry" to "Masonry", "polaroid" to "Polaroids", "filmstrip" to "Film strip", "stack" to "Stack", "fan" to "Fan",
    "gallery" to "Gallery wall", "clothesline" to "Clothesline", "bubbles" to "Bubbles", "magazine" to "Magazine",
    "carousel" to "Carousel", "pyramid" to "Pyramid", "diamond" to "Diamond", "cross" to "Cross", "corners" to "Corners",
    "spiral" to "Spiral", "wave" to "Wave", "spotlight" to "Spotlight", "postcards" to "Postcards",
    "collage" to "Collage", "frames" to "Frames", "patchwork" to "Patchwork", "overlap" to "Overlap", "diagonal" to "Diagonal",
)

/** The spread layout keys (everything except Auto / Mix / Single) — the pool "Mix" can draw from. */
private val SPREAD_KEYS = LAYOUTS.map { it.first }.filter { it != "auto" && it != "random" && it != "single" }

/** An "animated collage": a spread layout paired with a whole-collage motion. Opt-in, default none. */
private data class AnimCollage(val label: String, val layout: String, val motion: String)
private val ANIMATED_COLLAGES = listOf(
    AnimCollage("Drifting grid", "grid", "drift"),
    AnimCollage("Breathing mosaic", "mosaic", "breathe"),
    AnimCollage("Floating windows", "window", "float"),
    AnimCollage("Swaying gallery", "gallery", "sway"),
    AnimCollage("Rolling carousel", "carousel", "slidex"),
    AnimCollage("Parallax quilt", "quilt", "parallax"),
    AnimCollage("Rising masonry", "masonry", "rise"),
    AnimCollage("Rocking postcards", "postcards", "rock"),
    AnimCollage("Glowing frames", "frames", "glow"),
    AnimCollage("Pulsing patchwork", "patchwork", "pulse"),
)

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun SlideshowSheet(
    shuffle: Boolean, intervalMs: Long, orientation: String, fit: String, collage: Boolean,
    layout: String = "single", onLayout: (String) -> Unit = {},
    layoutPool: Set<String> = emptySet(), onLayoutPool: (Set<String>) -> Unit = {},
    motion: String = "off", motionSpeed: String = "medium",
    stagger: Boolean = false, onStagger: (Boolean) -> Unit = {},
    smartGroup: Boolean = false, onSmartGroup: (Boolean) -> Unit = {},
    spreadMix: String = "always", onSpreadMix: (String) -> Unit = {},
    onMotion: (String) -> Unit = {}, onMotionSpeed: (String) -> Unit = {},
    onShuffle: (Boolean) -> Unit, onInterval: (Long) -> Unit, onOrientation: (String) -> Unit,
    onFit: (String) -> Unit, onCollage: (Boolean) -> Unit, onDismiss: () -> Unit,
    bgColor: String = "black", onBg: (String) -> Unit = {},
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp).padding(top = 8.dp, bottom = 28.dp)) {
            Text(stringResource(R.string.tile_slideshow), style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontFamily = com.il90.salongallery.ui.theme.Display)
            Text(stringResource(R.string.slideshow_sub), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Spacer(Modifier.height(18.dp))

            // LAYOUT — the most important choice, so it's first. Default is Single (one photo).
            SettingsCard(stringResource(R.string.slideshow_layout_eyebrow), stringResource(R.string.slideshow_layout_title), stringResource(R.string.slideshow_layout_hint)) {
                // When "Mix" is chosen, the spread tiles become a multi-select pool — tick the collages
                // Mix should draw from. Otherwise a tile just picks that one layout.
                val isMix = layout == "random"
                if (isMix) {
                    val allOn = SPREAD_KEYS.all { it in layoutPool }
                    Text(stringResource(R.string.slideshow_mix_pick), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    EffectChip(stringResource(if (allOn) R.string.pool_clear_all else R.string.pool_select_all), selected = allOn, showCheck = false) {
                        onLayoutPool(if (allOn) setOf(SPREAD_KEYS.first()) else SPREAD_KEYS.toSet())
                    }
                    Spacer(Modifier.height(12.dp))
                }
                val gap = 10.dp
                BoxWithConstraints {
                    val tileW = (maxWidth - gap * 2) / 3
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(gap), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        LAYOUTS.forEach { (key, label) ->
                            if (isMix && key in SPREAD_KEYS) {
                                val inPool = key in layoutPool
                                LayoutPreviewTile(key, label, selected = inPool, modifier = Modifier.width(tileW), check = inPool) {
                                    val np = if (inPool) layoutPool - key else layoutPool + key
                                    if (np.isNotEmpty()) onLayoutPool(np)   // keep at least one in the mix
                                }
                            } else {
                                LayoutPreviewTile(key, label, selected = key == layout, modifier = Modifier.width(tileW)) { onLayout(key) }
                            }
                        }
                    }
                }
            }

            // ANIMATED COLLAGES — a separate, opt-in set where the WHOLE collage gently moves. Default
            // none (off): a preset only applies when tapped, setting a spread layout + a whole-collage
            // motion together. Highlighted when the current layout+motion match a preset.
            SettingsCard(stringResource(R.string.slideshow_anim_eyebrow), stringResource(R.string.slideshow_anim_title), stringResource(R.string.slideshow_anim_hint)) {
                val gap = 10.dp
                BoxWithConstraints {
                    val tileW = (maxWidth - gap * 2) / 3
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(gap), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ANIMATED_COLLAGES.forEach { preset ->
                            val on = layout == preset.layout && motion == preset.motion
                            LayoutPreviewTile(preset.layout, preset.label, selected = on, modifier = Modifier.width(tileW)) {
                                onLayout(preset.layout); onMotion(preset.motion)
                            }
                        }
                    }
                }
            }

            // PHOTO FIT
            SettingsCard(stringResource(R.string.slideshow_fit_eyebrow), stringResource(R.string.slideshow_fit_title), stringResource(R.string.slideshow_fit_explain)) {
                val fits = listOf("fill", "fit", "blur")
                val labels = listOf("Fill", "Fit", "Blur")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    fits.forEachIndexed { i, key ->
                        OptionTile(labels[i], selected = fit == key, modifier = Modifier.weight(1f), onClick = { onFit(key) }) { FitIllustration(key) }
                    }
                }
            }

            // BACKGROUND
            SettingsCard(stringResource(R.string.slideshow_bg_eyebrow), stringResource(R.string.slideshow_bg_title), stringResource(R.string.slideshow_bg_explain)) {
                // "Auto" (first, the default) blends the background into each photo's own colour.
                val bgs = listOf("auto", "black", "charcoal", "slate", "warm", "white")
                // Short labels so none truncate ("Charcoal" → "Grey").
                val bgLabels = listOf("Auto", "Black", "Grey", "Slate", "Warm", "White")
                val bgSwatch = listOf(Color(0xFF000000), Color(0xFF000000), Color(0xFF14110E), Color(0xFF2B2F36), Color(0xFF1C140D), Color(0xFFF2EEE6))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    bgs.forEachIndexed { i, key ->
                        OptionTile(bgLabels[i], selected = bgColor == key, modifier = Modifier.weight(1f), onClick = { onBg(key) }) {
                            val swatchMod = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp))
                            if (key == "auto") Box(
                                swatchMod.background(
                                    Brush.linearGradient(listOf(Color(0xFF3A2E6E), Color(0xFF2B6E6E), Color(0xFF6E5A2B)))
                                ).border(1.dp, Color.White.copy(0.18f), RoundedCornerShape(8.dp))
                            ) else Box(swatchMod.background(bgSwatch[i]).border(1.dp, Color.White.copy(0.18f), RoundedCornerShape(8.dp)))
                        }
                    }
                }
            }


            // COLLAGE FREQUENCY (only meaningful for a multi-photo layout; Auto sets its own rhythm)
            if (layout != "single") {
                if (layout != "auto") {
                    SettingsCard(stringResource(R.string.slideshow_freq_eyebrow), stringResource(R.string.slideshow_freq_title), stringResource(R.string.slideshow_freq_explain)) {
                        val mixes = listOf("always", "often", "sometimes", "rarely")
                        SegRow(listOf("Always", "Often", "Sometimes", "Rarely"), mixes.indexOf(spreadMix).coerceAtLeast(0)) { onSpreadMix(mixes[it]) }
                    }
                }
                SettingsToggleCard(stringResource(R.string.slideshow_reveal_eyebrow), stringResource(R.string.slideshow_stagger), stringResource(R.string.slideshow_stagger_hint), stagger, onStagger) { RevealIllustration() }
                SettingsToggleCard(stringResource(R.string.slideshow_smart_eyebrow), stringResource(R.string.slideshow_smart_title), stringResource(R.string.slideshow_smart_hint), smartGroup, onSmartGroup)
            }

            // MOTION
            SettingsCard(stringResource(R.string.slideshow_motion_eyebrow), stringResource(R.string.slideshow_motion_title), stringResource(R.string.slideshow_motion_hint)) {
                // "None" (no motion) first — it's the default; then Mix and every effect. Each tile
                // previews its own motion live.
                val motions = listOf("off", "mix", "zoom", "drift", "breathe", "sway", "glide", "float", "swing", "pulse", "tilt", "tilty", "rise", "fall", "slidex", "wobble", "parallax", "glow", "rock")
                val labels = listOf("None", "Mix", "Zoom", "Drift", "Fade", "Sway", "Glide", "Float", "Swing", "Pulse", "Tilt", "Tilt Y", "Rise", "Fall", "Slide", "Wobble", "Parallax", "Glow", "Rock")
                val gap = 8.dp
                BoxWithConstraints {
                    val tileW = (maxWidth - gap * 3) / 4
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(gap), verticalArrangement = Arrangement.spacedBy(gap)) {
                        motions.forEachIndexed { i, key ->
                            OptionTile(labels[i], selected = motion == key, modifier = Modifier.width(tileW), onClick = { onMotion(key) }) { MotionIllustration(key) }
                        }
                    }
                }
                if (motion != "off") {
                    Spacer(Modifier.height(14.dp))
                    FieldLabel(stringResource(R.string.slideshow_motion_speed))
                    Spacer(Modifier.height(4.dp))
                    val speeds = listOf("slow", "medium", "fast")
                    StepSlider(
                        labels = listOf("Slow", "Medium", "Fast"),
                        index = speeds.indexOf(motionSpeed).coerceAtLeast(0),
                        onChange = { onMotionSpeed(speeds[it]) },
                    )
                }
            }

            // ORDER
            SettingsCard(stringResource(R.string.slideshow_order_eyebrow), stringResource(R.string.slideshow_order_title), stringResource(R.string.slideshow_order_explain)) {
                SegRow(listOf("↓  Sequential", "⤬  Shuffle"), if (shuffle) 1 else 0) { onShuffle(it == 1) }
            }

            // TIMING
            SettingsCard(stringResource(R.string.slideshow_timing_eyebrow), stringResource(R.string.slideshow_timing_title), stringResource(R.string.slideshow_timing_explain)) {
                val intervals = listOf(10000L, 30000L, 60000L, 300000L, 900000L, 3600000L)
                SegRow(listOf("10s", "30s", "1m", "5m", "15m", "1h"), intervals.indexOf(intervalMs).coerceAtLeast(0)) { onInterval(intervals[it]) }
            }

            // ORIENTATION
            SettingsCard(stringResource(R.string.slideshow_orient_eyebrow), stringResource(R.string.slideshow_orientation), stringResource(R.string.slideshow_orient_explain)) {
                val orients = listOf("auto", "portrait", "landscape")
                val labels = listOf("Auto", "Portrait", "Landscape")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    orients.forEachIndexed { i, key ->
                        OptionTile(labels[i], selected = orientation == key, modifier = Modifier.weight(1f), onClick = { onOrientation(key) }) { OrientIllustration(key) }
                    }
                }
            }

            // AUTO-FILL
            SettingsToggleCard(stringResource(R.string.slideshow_framing_eyebrow), stringResource(R.string.slideshow_autofill), stringResource(R.string.slideshow_autofill_hint), collage, onCollage)
        }
    }
}

/** A settings card: an accent eyebrow, a serif title, a plain-language explanation, then its control. */
@Composable
private fun SettingsCard(eyebrow: String, title: String, explain: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(bottom = 14.dp).clip(RoundedCornerShape(18.dp))
            .background(ElecSurface).border(1.dp, ElecBorder, RoundedCornerShape(18.dp)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(eyebrow.uppercase(), style = MaterialTheme.typography.labelSmall, color = AccentWarm, letterSpacing = 1.4.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(3.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontFamily = com.il90.salongallery.ui.theme.Display, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(6.dp))
        Text(explain, style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(14.dp))
        content()
    }
}

/** A card whose control is a single on/off switch. */
@Composable
private fun SettingsToggleCard(
    eyebrow: String, title: String, explain: String, checked: Boolean, onChange: (Boolean) -> Unit,
    illustration: @Composable (() -> Unit)? = null,
) {
    Box(
        Modifier.fillMaxWidth().padding(bottom = 14.dp).clip(RoundedCornerShape(18.dp))
            .background(ElecSurface).border(1.dp, ElecBorder, RoundedCornerShape(18.dp)).padding(16.dp),
    ) {
        // Text is centred across the full card width (to match the other cards); the switch stays
        // pinned to the end.
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.fillMaxWidth().padding(end = 52.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(eyebrow.uppercase(), style = MaterialTheme.typography.labelSmall, color = AccentWarm, letterSpacing = 1.4.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(3.dp))
                Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontFamily = com.il90.salongallery.ui.theme.Display, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(4.dp))
                Text(explain, style = MaterialTheme.typography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            if (illustration != null) {
                Spacer(Modifier.height(14.dp))
                illustration()
            }
        }
        Switch(checked = checked, onCheckedChange = onChange, modifier = Modifier.align(Alignment.TopEnd))
    }
}

/** A looping demo of a multi-photo slide whose tiles fade in one after another. */
@Composable
private fun RevealIllustration() {
    val t = androidx.compose.animation.core.rememberInfiniteTransition(label = "rev")
    val v by t.animateFloat(
        0f, 1f,
        androidx.compose.animation.core.infiniteRepeatable(
            androidx.compose.animation.core.tween(2600, easing = androidx.compose.animation.core.LinearEasing),
        ), label = "revv",
    )
    val count = 4
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(count) { i ->
            // Each tile's reveal window is staggered across the loop.
            val start = i / (count + 1f)
            val a = ((v - start) / 0.28f).coerceIn(0f, 1f)
            Box(
                Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(8.dp))
                    .graphicsLayer { alpha = 0.18f + 0.82f * a }
                    .background(Brush.linearGradient(listOf(CreamTile, Color(0xFFD8CAB4)))),
            )
        }
    }
}

/** A selectable tile with a small illustration above a label (used for fit / motion / orientation). */
@Composable
private fun OptionTile(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit, illustration: @Composable () -> Unit) {
    Column(
        modifier.clip(RoundedCornerShape(14.dp))
            .background(if (selected) NeonCyan.copy(alpha = 0.14f) else Color(0xFF241C15))
            .border(if (selected) 1.5.dp else 1.dp, if (selected) NeonCyan else ElecBorder, RoundedCornerShape(14.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.height(44.dp).fillMaxWidth(), contentAlignment = Alignment.Center) { illustration() }
        Spacer(Modifier.height(7.dp))
        // Compact + centred + 2 lines so narrow tiles (e.g. 5-across Background) never clip "Warm"/"White".
        Text(
            label, style = MaterialTheme.typography.labelSmall,
            color = if (selected) NeonCyan else TextPrimary,
            maxLines = 2, textAlign = TextAlign.Center, lineHeight = 13.sp,
        )
    }
}

private val CreamTile = Color(0xFFF2EEE6)
private val AccentWarm = Color(0xFFD8763A)

/** Fill / Fit / Blur illustrations. */
@Composable
private fun FitIllustration(key: String) {
    Box(Modifier.size(58.dp, 38.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFF0F0A07)), contentAlignment = Alignment.Center) {
        when (key) {
            "fit" -> Box(Modifier.fillMaxHeight().fillMaxWidth(0.6f).background(Brush.horizontalGradient(listOf(CreamTile, Color(0xFFD8CAB4)))))
            "blur" -> {
                Box(Modifier.matchParentSize().background(Brush.horizontalGradient(listOf(NeonCyan.copy(0.5f), Color(0xFFD8CAB4)))).blur(6.dp))
                Box(Modifier.fillMaxHeight().fillMaxWidth(0.58f).background(Brush.horizontalGradient(listOf(CreamTile, Color(0xFFD8CAB4)))))
            }
            else -> Box(Modifier.matchParentSize().background(Brush.horizontalGradient(listOf(CreamTile, Color(0xFFD8CAB4)))))
        }
    }
}

/** Off / Zoom / Drift / Fade / Mix — a tiny tile that actually performs the motion. */
@Composable
private fun MotionIllustration(key: String) {
    val t = androidx.compose.animation.core.rememberInfiniteTransition(label = "m")
    val v by t.animateFloat(
        0f, 1f,
        androidx.compose.animation.core.infiniteRepeatable(
            androidx.compose.animation.core.tween(1700, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            androidx.compose.animation.core.RepeatMode.Reverse,
        ), label = "mv",
    )
    Box(
        Modifier.size(32.dp).clip(RoundedCornerShape(7.dp)).background(Brush.linearGradient(listOf(CreamTile, Color(0xFFD8CAB4))))
            .graphicsLayer {
                when (key) {
                    "zoom" -> { val s = 0.82f + 0.3f * v; scaleX = s; scaleY = s }
                    "drift" -> { translationX = (v - 0.5f) * 20f }
                    "breathe" -> { alpha = 0.3f + 0.7f * v }
                    "sway" -> { rotationZ = (v - 0.5f) * 24f }
                    "glide" -> { val s = 0.9f + 0.2f * v; scaleX = s; scaleY = s; translationX = (v - 0.5f) * 16f; translationY = (v - 0.5f) * 10f }
                    "float" -> { translationY = (v - 0.5f) * 20f }
                    "swing" -> { transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0f); rotationZ = (v - 0.5f) * 28f }
                    "pulse" -> { val s = 0.84f + 0.3f * v; scaleX = s; scaleY = s }
                    "tilt" -> { cameraDistance = 12f * density; rotationY = (v - 0.5f) * 60f }
                    "tilty" -> { cameraDistance = 12f * density; rotationX = (v - 0.5f) * 60f }
                    "rise" -> { val s = 0.9f + 0.18f * v; scaleX = s; scaleY = s; translationY = -(v - 0.5f) * 16f }
                    "fall" -> { val s = 0.9f + 0.18f * v; scaleX = s; scaleY = s; translationY = (v - 0.5f) * 16f }
                    "slidex" -> { translationX = (v - 0.5f) * 26f }
                    "wobble" -> { val s = 0.9f + 0.1f * v; scaleX = s; scaleY = s; rotationZ = (v - 0.5f) * 18f }
                    "parallax" -> { val s = 1.0f - 0.16f * v; scaleX = s; scaleY = s; translationX = (v - 0.5f) * 18f }
                    "glow" -> { alpha = 0.55f + 0.45f * v }
                    "rock" -> { rotationZ = (v - 0.5f) * 34f }
                    "mix" -> { val s = 0.86f + 0.22f * v; scaleX = s; scaleY = s; alpha = 0.5f + 0.5f * v }
                    else -> {}
                }
            },
    )
}

/** Auto / Portrait / Landscape frame illustrations. */
@Composable
private fun OrientIllustration(key: String) {
    when (key) {
        "portrait" -> Box(Modifier.size(24.dp, 36.dp).clip(RoundedCornerShape(4.dp)).background(CreamTile))
        "landscape" -> Box(Modifier.size(40.dp, 26.dp).clip(RoundedCornerShape(4.dp)).background(CreamTile))
        else -> Box(Modifier.size(28.dp, 34.dp).clip(RoundedCornerShape(4.dp)).border(2.dp, CreamTile, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.ScreenRotation, null, tint = NeonCyan, modifier = Modifier.size(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun TextSheet(
    screen: DiscoveredScreen,
    onText: (String, String, String, String, String) -> Unit,
    onClock: (Boolean, String, Boolean, String, String) -> Unit,
    onWeather: (Boolean, String, Double, Double, String, String, String) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var content by remember { mutableStateOf("") }
    var posIdx by remember { mutableIntStateOf(2) }
    var sizeIdx by remember { mutableIntStateOf(1) }
    var colorIdx by remember { mutableIntStateOf(0) }
    var fontIdx by remember { mutableIntStateOf(0) }
    var clock by remember { mutableStateOf(false) }
    var clockPosIdx by remember { mutableIntStateOf(2) }   // bottom-left by default
    var clockDate by remember { mutableStateOf(true) }
    var clockStyle by remember { mutableStateOf("digital") }
    var clockSizeIdx by remember { mutableIntStateOf(1) }
    // Gate the auto-push effects until the Display's real state has loaded, so initialising the
    // fields from the server doesn't echo straight back as a "change".
    var loaded by remember { mutableStateOf(false) }
    var overlayTab by remember { mutableStateOf("text") }
    val positions = listOf("top", "center", "bottom")
    val sizes = listOf("s", "m", "l")
    val colors = listOf("white", "black", "gold", "cyan", "violet")
    val fonts = listOf("classic", "modern", "mono", "elegant", "rounded")
    val clockPositions = listOf("top_start", "top_end", "bottom_start", "bottom_end", "center")
    val clockStyles = listOf(
        "digital" to "Digital", "bold" to "Bold", "card" to "Card", "minimal" to "Minimal",
        "mono" to "Mono", "led" to "LED", "analog" to "Analog",
    )
    val clockSizes = listOf("s", "m", "l")

    // Weather
    var weatherOn by remember { mutableStateOf(false) }
    var weatherUnitsIdx by remember { mutableIntStateOf(0) }
    var weatherPosIdx by remember { mutableIntStateOf(2) }   // bottom-left by default
    val weatherStyles = listOf("pill", "minimal", "card", "stacked")
    var weatherStyleIdx by remember { mutableIntStateOf(0) }
    var weatherPlace by remember { mutableStateOf("") }
    var weatherLat by remember { mutableStateOf(0.0) }
    var weatherLon by remember { mutableStateOf(0.0) }
    var citySearch by remember { mutableStateOf("") }
    var cityResults by remember { mutableStateOf<List<PhotoSender.GeoPlace>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }
    val weatherUnits = listOf("c", "f")

    // Weather can be on even before a city is picked (the toggle persists); the Display just won't
    // fetch until a location is set. So push the real toggle state, not "on only if a city exists".
    fun pushWeather() = onWeather(weatherOn, weatherPlace, weatherLat, weatherLon, weatherUnits[weatherUnitsIdx], clockPositions[weatherPosIdx], weatherStyles[weatherStyleIdx])

    // Load the Display's real overlay state once when the sheet opens, so the toggles and fields show
    // what is actually on the wall instead of fresh defaults. Pushes are gated on `loaded` so this
    // initialisation doesn't bounce straight back to the server.
    LaunchedEffect(Unit) {
        PhotoSender.getOverlays(screen.host, screen.port)?.let { o ->
            content = o.textContent
            posIdx = positions.indexOf(o.textPos).coerceAtLeast(0)
            sizeIdx = sizes.indexOf(o.textSize).coerceAtLeast(0)
            colorIdx = colors.indexOf(o.textColor).coerceAtLeast(0)
            fontIdx = fonts.indexOf(o.textFont).coerceAtLeast(0)
            clock = o.clockOn
            clockPosIdx = clockPositions.indexOf(o.clockPos).coerceAtLeast(0)
            clockDate = o.clockDate
            clockStyle = o.clockStyle
            clockSizeIdx = clockSizes.indexOf(o.clockSize).coerceAtLeast(0)
            weatherOn = o.weatherOn
            weatherPlace = o.weatherPlace
            weatherLat = o.weatherLat
            weatherLon = o.weatherLon
            weatherUnitsIdx = weatherUnits.indexOf(o.weatherUnits).coerceAtLeast(0)
            weatherPosIdx = clockPositions.indexOf(o.weatherPos).coerceAtLeast(0)
            weatherStyleIdx = weatherStyles.indexOf(o.weatherStyle).coerceAtLeast(0)
        }
        loaded = true
    }

    LaunchedEffect(content, posIdx, sizeIdx, colorIdx, fontIdx, loaded) {
        if (!loaded) return@LaunchedEffect
        kotlinx.coroutines.delay(250)
        onText(content, positions[posIdx], sizes[sizeIdx], colors[colorIdx], fonts[fontIdx])
    }
    LaunchedEffect(clock, clockPosIdx, clockDate, clockStyle, clockSizeIdx, loaded) {
        if (!loaded) return@LaunchedEffect
        onClock(clock, clockPositions[clockPosIdx], clockDate, clockStyle, clockSizes[clockSizeIdx])
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).imePadding().padding(horizontal = 24.dp).padding(bottom = 8.dp)) {
            Text(stringResource(R.string.overlays_title), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.overlays_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)

            // One section at a time, so the screen stays tidy (Text / Clock / Weather).
            Spacer(Modifier.height(16.dp))
            val overlayTabs = listOf("text", "clock", "weather")
            SegRow(listOf(stringResource(R.string.overlays_text), stringResource(R.string.overlays_clock), stringResource(R.string.overlays_weather)), overlayTabs.indexOf(overlayTab).coerceAtLeast(0)) {
                overlayTab = overlayTabs[it]
            }

            // ---- TEXT ----
            if (overlayTab == "text") {
            Spacer(Modifier.height(20.dp))
            SheetSection(stringResource(R.string.overlays_text))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = content, onValueChange = { content = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = com.il90.salongallery.ui.theme.ContentFont),
                placeholder = { Text(stringResource(R.string.text_hint), color = TextTertiary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
                ),
            )
            Spacer(Modifier.height(14.dp))
            FieldLabel(stringResource(R.string.text_font))
            SegRow(listOf("Classic", "Modern", "Mono", "Elegant", "Round"), fontIdx) { fontIdx = it }
            Spacer(Modifier.height(14.dp))
            FieldLabel(stringResource(R.string.text_position))
            SegRow(listOf("Top", "Center", "Bottom"), posIdx) { posIdx = it }
            Spacer(Modifier.height(14.dp))
            FieldLabel(stringResource(R.string.text_size))
            StepSlider(listOf("Small", "Medium", "Large"), sizeIdx) { sizeIdx = it }
            Spacer(Modifier.height(14.dp))
            FieldLabel(stringResource(R.string.text_color))
            SegRow(listOf("White", "Black", "Gold", "Cyan", "Violet"), colorIdx) { colorIdx = it }
            }

            // ---- CLOCK ----
            if (overlayTab == "clock") {
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                SheetSection(stringResource(R.string.overlays_clock), Modifier.weight(1f))
                Switch(checked = clock, onCheckedChange = { clock = it })
            }
            if (clock) {
                Spacer(Modifier.height(12.dp))
                FieldLabel(stringResource(R.string.clock_style))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    clockStyles.forEach { (key, label) -> EffectChip(label, selected = key == clockStyle, showCheck = false) { clockStyle = key } }
                }
                Spacer(Modifier.height(12.dp))
                FieldLabel(stringResource(R.string.clock_size))
                StepSlider(listOf("Small", "Medium", "Large"), clockSizeIdx) { clockSizeIdx = it }
                Spacer(Modifier.height(12.dp))
                FieldLabel(stringResource(R.string.clock_position))
                SegRow(listOf("\u2196", "\u2197", "\u2199", "\u2198", "\u2022"), clockPosIdx) { clockPosIdx = it }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.clock_show_date), style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                    Switch(checked = clockDate, onCheckedChange = { clockDate = it })
                }
            }
            }

            // ---- WEATHER ----
            if (overlayTab == "weather") {
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                SheetSection(stringResource(R.string.overlays_weather), Modifier.weight(1f))
                Switch(checked = weatherOn, onCheckedChange = { weatherOn = it; pushWeather() })
            }
            if (weatherOn) {
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.weather_free_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(10.dp))
                if (weatherPlace.isNotBlank()) {
                    Text("\uD83D\uDCCD " + weatherPlace, style = MaterialTheme.typography.titleMedium, color = NeonCyan)
                    Spacer(Modifier.height(10.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = citySearch, onValueChange = { citySearch = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = com.il90.salongallery.ui.theme.ContentFont),
                        placeholder = { Text(stringResource(R.string.weather_city), color = TextTertiary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
                            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
                        ),
                    )
                    RoundIconBtn(Icons.Outlined.Search, accent = true) {
                        searching = true
                        scope.launch { cityResults = PhotoSender.geocode(citySearch); searching = false }
                    }
                }
                if (searching) { Spacer(Modifier.height(8.dp)); Text(stringResource(R.string.weather_searching), style = MaterialTheme.typography.bodySmall, color = TextSecondary) }
                cityResults.forEach { place ->
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(1.dp, ElecBorder, RoundedCornerShape(12.dp))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                weatherPlace = place.name; weatherLat = place.lat; weatherLon = place.lon
                                cityResults = emptyList(); citySearch = ""; pushWeather()
                            }.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Outlined.LocationOn, null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("${place.name}${if (place.country.isNotBlank()) ", ${place.country}" else ""}", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    }
                }
                Spacer(Modifier.height(12.dp))
                FieldLabel(stringResource(R.string.weather_style))
                SegRow(listOf("Pill", "Minimal", "Card", "Stacked"), weatherStyleIdx) { weatherStyleIdx = it; pushWeather() }
                Spacer(Modifier.height(12.dp))
                FieldLabel(stringResource(R.string.weather_units))
                SegRow(listOf("\u00B0C", "\u00B0F"), weatherUnitsIdx) { weatherUnitsIdx = it; pushWeather() }
                Spacer(Modifier.height(12.dp))
                FieldLabel(stringResource(R.string.weather_position))
                SegRow(listOf("\u2196", "\u2197", "\u2199", "\u2198", "\u2022"), weatherPosIdx) { weatherPosIdx = it; pushWeather() }
            }
            }

            Spacer(Modifier.height(22.dp))
            OutlineButton(text = stringResource(R.string.text_clear), onClick = {
                content = ""; clock = false; weatherOn = false; weatherPlace = ""; weatherLat = 0.0; onClear()
            })
            Spacer(Modifier.height(16.dp))
        }
    }
}

/** A bold section header inside a settings sheet. */
@Composable
private fun SheetSection(title: String, modifier: Modifier = Modifier) {
    Text(title, modifier = modifier, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontFamily = com.il90.salongallery.ui.theme.ContentFont, fontWeight = FontWeight.SemiBold)
}

/** A small field label with the standard spacing under it. */
@Composable
private fun FieldLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
    Spacer(Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MusicSheet(
    screen: DiscoveredScreen,
    onAddFromPhone: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf<MusicState?>(null) }
    var showFree by remember { mutableStateOf(false) }
    var refresh by remember { mutableIntStateOf(0) }

    LaunchedEffect(refresh) {
        state = PhotoSender.getMusic(screen.host, screen.port)
    }
    fun bump() { refresh++ }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.music_title), style = MaterialTheme.typography.headlineSmall, color = TextPrimary, modifier = Modifier.weight(1f))
                RoundIconBtn(Icons.Outlined.Add, accent = true, desc = "Add music from phone") { onAddFromPhone() }
            }
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.music_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)

            Spacer(Modifier.height(18.dp))
            // Transport controls
            val playing = state?.playing == true
            val shuffleOn = state?.shuffle == true
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                SmallBtn(Icons.Filled.Shuffle, if (shuffleOn) NeonCyan else TextSecondary, desc = "Shuffle") {
                    scope.launch { PhotoSender.musicControl(screen.host, screen.port, "shuffle"); bump() }
                }
                SmallBtn(Icons.Filled.SkipPrevious, TextPrimary, desc = "Previous track") {
                    scope.launch { PhotoSender.musicControl(screen.host, screen.port, "prev"); bump() }
                }
                Box(
                    Modifier.size(58.dp).clip(RoundedCornerShape(50)).background(AccentGradient)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            scope.launch { PhotoSender.musicControl(screen.host, screen.port, "toggle"); bump() }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow, null, tint = Color(0xFF07121F), modifier = Modifier.size(30.dp))
                }
                SmallBtn(Icons.Filled.SkipNext, TextPrimary, desc = "Next track") {
                    scope.launch { PhotoSender.musicControl(screen.host, screen.port, "next"); bump() }
                }
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.height(18.dp))
            val tracks = state?.tracks.orEmpty()
            if (tracks.isEmpty()) {
                Text(stringResource(R.string.music_empty), style = MaterialTheme.typography.bodyMedium, color = TextSecondary, modifier = Modifier.padding(vertical = 16.dp))
            } else {
                LazyColumn(Modifier.fillMaxWidth().heightIn(max = 260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(tracks, key = { _, t -> t.name }) { i, t ->
                        val isNow = i == (state?.current ?: -1) && playing
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                                .background(if (isNow) NeonCyan.copy(alpha = 0.10f) else ElecSurface)
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(if (isNow) Icons.Filled.PlayArrow else Icons.Outlined.MusicNote, null, tint = if (isNow) NeonCyan else TextSecondary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(t.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 1, modifier = Modifier.weight(1f))
                            SmallBtn(Icons.Outlined.Delete, Color(0xFFF87171), desc = "Delete ${t.title}") {
                                scope.launch { PhotoSender.musicDelete(screen.host, screen.port, t.name); bump() }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            WideButton(Icons.Outlined.LibraryMusic, stringResource(R.string.music_free), NeonVioletLight) { showFree = true }
            Spacer(Modifier.height(14.dp))
        }
    }

    if (showFree) {
        FreeMusicSheet(
            onDownload = { t ->
                scope.launch {
                    PhotoSender.musicDownload(screen.host, screen.port, t.url, t.title)
                    delay(1500); bump()
                }
            },
            onDismiss = { showFree = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
private fun FreeMusicSheet(onDownload: (FreeTrack) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val added = remember { mutableStateListOf<String>() }
    // A phone-local player so you can preview a track before sending it to the screen.
    val player = remember { androidx.media3.exoplayer.ExoPlayer.Builder(context).build() }
    DisposableEffect(Unit) { onDispose { player.release() } }
    var previewing by remember { mutableStateOf<String?>(null) }
    fun preview(t: FreeTrack) {
        if (previewing == t.title) { player.stop(); previewing = null }
        else {
            player.setMediaItem(androidx.media3.common.MediaItem.fromUri(t.url)); player.prepare(); player.play()
            previewing = t.title
        }
    }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
            Text(stringResource(R.string.music_free), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.music_free_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(16.dp))
            PhotoSender.freeMusic.forEach { t ->
                val done = added.contains(t.title)
                val isPreview = previewing == t.title
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 6.dp).clip(RoundedCornerShape(12.dp))
                        .background(ElecSurface).padding(start = 8.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SmallBtn(if (isPreview) Icons.Outlined.Pause else Icons.Outlined.PlayArrow, if (isPreview) NeonViolet else TextSecondary, desc = "Preview ${t.title}") { preview(t) }
                    Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                        Text(t.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 1)
                        Text(t.artist, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                    }
                    SmallBtn(Icons.Outlined.CloudDownload, if (done) GoodGreen else NeonCyan, desc = "Add ${t.title} to screen") {
                        if (!done) { added.add(t.title); onDownload(t) }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun GooglePhotosSheet(screen: DiscoveredScreen, onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val defaultAlbum = stringResource(R.string.gphotos_album_default)
    var link by remember { mutableStateOf("") }
    var albumName by remember { mutableStateOf(defaultAlbum) }
    var busy by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }
    var ok by remember { mutableStateOf(false) }
    val tf = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
    )
    // Resolve status strings here (composable scope) so the coroutine can use them.
    val errFetch = stringResource(R.string.gphotos_err_fetch)
    val errEmpty = stringResource(R.string.gphotos_err_empty)
    val doneFmt = stringResource(R.string.gphotos_done)
    val importReq = rememberRevealRequester()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).imePadding().padding(24.dp)) {
            Text(stringResource(R.string.gphotos_title), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.gphotos_hint), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

            // Step-by-step so the user knows exactly what to do.
            Spacer(Modifier.height(16.dp))
            listOf(
                R.string.gphotos_step1, R.string.gphotos_step2, R.string.gphotos_step3, R.string.gphotos_step4,
            ).forEachIndexed { i, s ->
                Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(Modifier.size(24.dp).clip(CircleShape).background(NeonCyan.copy(alpha = 0.16f)), contentAlignment = Alignment.Center) {
                        Text("${i + 1}", style = MaterialTheme.typography.labelMedium, color = NeonCyan)
                    }
                    Text(stringResource(s), style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = link, onValueChange = { link = it; status = null }, singleLine = true,
                modifier = Modifier.fillMaxWidth().revealOnFocus(importReq), colors = tf,
                placeholder = { Text("https://photos.app.goo.gl/…", color = TextTertiary) },
                label = { Text(stringResource(R.string.gphotos_link_label), color = TextSecondary) },
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = albumName, onValueChange = { albumName = it }, singleLine = true,
                modifier = Modifier.fillMaxWidth().revealOnFocus(importReq), colors = tf,
                placeholder = { Text(stringResource(R.string.gphotos_album_default), color = TextTertiary) },
                label = { Text(stringResource(R.string.gphotos_album_label), color = TextSecondary) },
            )
            status?.let {
                Spacer(Modifier.height(10.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, color = if (ok) GoodGreen else Color(0xFFF2B07A))
            }
            Spacer(Modifier.height(16.dp))
            GradientButton(
                modifier = Modifier.bringIntoViewRequester(importReq),
                text = if (busy) stringResource(R.string.gphotos_importing) else stringResource(R.string.gphotos_import),
                enabled = !busy && GooglePhotos.looksLikeShareLink(link),
                onClick = {
                    busy = true; status = null; ok = false
                    scope.launch {
                        val urls = GooglePhotos.fetchSharedAlbum(link.trim())
                        when {
                            urls == null -> { status = errFetch; ok = false }
                            urls.isEmpty() -> { status = errEmpty; ok = false }
                            else -> {
                                // Put the import in its own album so it's easy to find (and rename).
                                val name = albumName.trim().ifBlank { defaultAlbum }
                                val albumId = PhotoSender.createAlbum(screen.host, screen.port, name)
                                if (albumId != null) PhotoSender.setActiveAlbum(screen.host, screen.port, albumId)
                                var done = 0
                                urls.forEach { u ->
                                    if (PhotoSender.downloadPhoto(screen.host, screen.port, u)) done++
                                }
                                ok = done > 0
                                status = String.format(java.util.Locale.getDefault(), doneFmt, done) + " · " + name
                            }
                        }
                        busy = false
                    }
                },
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtSheet(screen: DiscoveredScreen, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var source by remember { mutableStateOf(ArtSource.entries.first()) }
    var activeCat by remember { mutableStateOf(ArtGallery.categoriesFor(ArtSource.entries.first()).first()) }
    var results by remember { mutableStateOf<List<ArtPiece>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }
    val added = remember { mutableStateListOf<String>() }
    // Reflect what's already on the wall so pieces show as selected.
    LaunchedEffect(Unit) {
        val urls = PhotoSender.getArtSources(screen.host, screen.port)
        added.clear(); added.addAll(urls)
    }

    // Coil needs a browser User-Agent + Referer to fetch the museum's IIIF images.
    val loader = remember {
        coil.ImageLoader.Builder(context).okHttpClient {
            okhttp3.OkHttpClient.Builder().addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("User-Agent", ArtGallery.BROWSE_UA)
                        .header("Referer", ArtGallery.REFERER)
                        .build()
                )
            }.build()
        }.build()
    }

    suspend fun run(q: String) {
        loading = true; error = false
        val r = ArtGallery.search(source, q)
        if (r == null) error = true else results = r
        loading = false
    }
    LaunchedEffect(activeCat, source) { run(if (query.isBlank()) activeCat else query) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true), containerColor = com.il90.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().imePadding().padding(horizontal = 24.dp).padding(bottom = 20.dp)) {
            Text(stringResource(R.string.art_title), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.art_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text(stringResource(R.string.art_search), color = TextTertiary) },
                trailingIcon = {
                    SmallBtn(Icons.Outlined.Search, NeonCyan, desc = "Search art") {
                        if (query.isNotBlank()) scope.launch { run(query) }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
                ),
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ArtSource.entries.forEach { src ->
                    val sel = src == source
                    Box(
                        Modifier.clip(RoundedCornerShape(50))
                            .then(if (sel) Modifier.background(NeonCyan.copy(alpha = 0.16f)) else Modifier)
                            .border(1.dp, if (sel) NeonCyan else ElecBorder, RoundedCornerShape(50))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                if (src != source) { source = src; query = ""; activeCat = ArtGallery.categoriesFor(src).first() }
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(src.label, style = MaterialTheme.typography.labelMedium, color = if (sel) NeonCyan else TextSecondary)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ArtGallery.categoriesFor(source).forEach { cat ->
                    val sel = cat == activeCat && query.isBlank()
                    Box(
                        Modifier.clip(RoundedCornerShape(50))
                            .then(if (sel) Modifier.background(AccentGradient) else Modifier.border(1.dp, ElecBorder, RoundedCornerShape(50)))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                query = ""; activeCat = cat
                            }
                            .padding(horizontal = 16.dp, vertical = 9.dp),
                    ) {
                        Text(cat, style = MaterialTheme.typography.labelMedium, color = if (sel) Color(0xFF07121F) else TextPrimary)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            when {
                loading -> Box(Modifier.fillMaxWidth().height(240.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonCyan, strokeWidth = 3.dp, modifier = Modifier.size(34.dp))
                }
                error -> Column(
                    Modifier.fillMaxWidth().height(180.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(stringResource(R.string.art_error), style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    OutlineButton(text = stringResource(R.string.art_retry), onClick = { scope.launch { run(if (query.isBlank()) activeCat else query) } })
                }
                results.isEmpty() -> Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.art_empty), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 460.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(results, key = { it.fullUrl }) { piece ->
                        val isAdded = added.contains(piece.fullUrl)
                        Box(
                            Modifier.aspectRatio(1f).clip(RoundedCornerShape(14.dp)).background(ElecSurface)
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                    if (isAdded) {
                                        added.remove(piece.fullUrl)
                                        scope.launch { PhotoSender.removeArt(screen.host, screen.port, piece.fullUrl) }
                                    } else {
                                        added.add(piece.fullUrl)
                                        scope.launch { PhotoSender.downloadPhoto(screen.host, screen.port, piece.fullUrl) }
                                    }
                                },
                        ) {
                            AsyncImage(
                                model = coil.request.ImageRequest.Builder(context).data(piece.thumbUrl).crossfade(true).build(),
                                imageLoader = loader,
                                contentDescription = piece.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                            if (isAdded) {
                                Box(Modifier.fillMaxSize().background(Color(0xE6000000).copy(alpha = 0.55f)), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(50)).background(AccentGradient), contentAlignment = Alignment.Center) {
                                            Icon(Icons.Outlined.Check, null, tint = Color(0xFF07121F), modifier = Modifier.size(24.dp))
                                        }
                                        Spacer(Modifier.height(6.dp))
                                        Text(stringResource(R.string.art_on_wall), style = MaterialTheme.typography.labelSmall, color = Color.White, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SegRow(labels: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.forEachIndexed { i, label ->
            val sel = i == selected
            Box(
                // Fixed height for every segment (uniform row), tall enough for a label to wrap to two
                // lines instead of being cut off — so "Classic"/"Sometimes" show in full.
                Modifier.weight(1f).height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .then(if (sel) Modifier.background(AccentGradient) else Modifier.border(1.dp, ElecBorder, RoundedCornerShape(12.dp)))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onSelect(i) }
                    .padding(horizontal = 3.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label, style = MaterialTheme.typography.labelSmall,
                    color = if (sel) Color(0xFF07121F) else TextPrimary,
                    textAlign = TextAlign.Center, maxLines = 2,
                    softWrap = true, lineHeight = 13.sp,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Clip,
                )
            }
        }
    }
}

/** A flexible slider that snaps to a small set of labelled steps (e.g. Slow·Medium·Fast, S·M·L). */
@Composable
private fun StepSlider(labels: List<String>, index: Int, onChange: (Int) -> Unit) {
    val n = labels.size
    var pos by remember(index) { mutableStateOf(index.toFloat()) }
    val sel = Math.round(pos).coerceIn(0, n - 1)
    Column(Modifier.fillMaxWidth()) {
        Slider(
            value = pos,
            onValueChange = { pos = it },
            onValueChangeFinished = { onChange(Math.round(pos).coerceIn(0, n - 1)) },
            valueRange = 0f..(n - 1).toFloat(),
            steps = (n - 2).coerceAtLeast(0),
            colors = SliderDefaults.colors(
                thumbColor = NeonCyan, activeTrackColor = NeonCyan,
                inactiveTrackColor = ElecBorder, activeTickColor = NeonCyan.copy(alpha = 0.5f), inactiveTickColor = ElecBorder,
            ),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            labels.forEachIndexed { i, l ->
                Text(l, style = MaterialTheme.typography.labelSmall, color = if (i == sel) NeonCyan else TextTertiary)
            }
        }
    }
}

@Composable
private fun Searching() {
    Column(Modifier.fillMaxWidth().padding(top = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = NeonCyan, strokeWidth = 3.dp, modifier = Modifier.size(34.dp))
        Spacer(Modifier.height(18.dp))
        Text(stringResource(R.string.remote_searching), style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(6.dp))
        Text(
            stringResource(R.string.remote_searching_hint),
            style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ScreenRow(screen: DiscoveredScreen, subtitle: String = screen.host, version: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, ElecBorder, RoundedCornerShape(18.dp))
            .background(ElecSurface)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(NeonCyan.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Outlined.Tv, null, tint = NeonTeal, modifier = Modifier.size(22.dp)) }
        Spacer(Modifier.size(14.dp))
        Column(Modifier.weight(1f)) {
            Text(screen.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        // Which build that screen runs — tells an old install from the current one at a glance.
        if (!version.isNullOrBlank()) {
            Text(
                "v$version", style = MaterialTheme.typography.labelMedium, color = TextTertiary,
                modifier = Modifier.clip(RoundedCornerShape(50)).border(1.dp, ElecBorder, RoundedCornerShape(50))
                    .padding(horizontal = 9.dp, vertical = 3.dp),
            )
        }
    }
}
