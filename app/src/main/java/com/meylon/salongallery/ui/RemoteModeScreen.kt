package com.meylon.salongallery.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.outlined.DeleteSweep
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
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.RssFeed
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Visibility
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
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
import com.meylon.salongallery.R
import com.meylon.salongallery.net.AlbumInfo
import com.meylon.salongallery.net.ArtGallery
import com.meylon.salongallery.net.ArtPiece
import com.meylon.salongallery.net.ArtSource
import com.meylon.salongallery.net.DiscoveredScreen
import com.meylon.salongallery.net.FreeTrack
import com.meylon.salongallery.net.LibraryList
import com.meylon.salongallery.net.MusicState
import com.meylon.salongallery.net.PhotoSender
import com.meylon.salongallery.net.RemoteSession
import com.meylon.salongallery.net.ScreenInfo
import com.meylon.salongallery.ui.components.AccentGradient
import com.meylon.salongallery.ui.components.GradientButton
import com.meylon.salongallery.ui.components.OutlineButton
import com.meylon.salongallery.ui.components.SalonBackground
import com.meylon.salongallery.ui.components.SectionLabel
import com.meylon.salongallery.ui.theme.ElecBorder
import com.meylon.salongallery.ui.theme.ElecSurface
import com.meylon.salongallery.ui.theme.ElecSurfaceElevated
import com.meylon.salongallery.ui.theme.GoodGreen
import com.meylon.salongallery.ui.theme.NeonBlue
import com.meylon.salongallery.ui.theme.NeonCyan
import com.meylon.salongallery.ui.theme.NeonTeal
import com.meylon.salongallery.ui.theme.NeonViolet
import com.meylon.salongallery.ui.theme.NeonVioletLight
import com.meylon.salongallery.ui.theme.TintBlue
import com.meylon.salongallery.ui.theme.TintClay
import com.meylon.salongallery.ui.theme.TintPlum
import com.meylon.salongallery.ui.theme.TintSage
import com.meylon.salongallery.ui.theme.TextPrimary
import com.meylon.salongallery.ui.theme.TextSecondary
import com.meylon.salongallery.ui.theme.TextTertiary
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    var showSettings by remember { mutableStateOf(false) }
    var showManual by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf<ScreenInfo?>(null) }

    LaunchedEffect(selected?.host, selected?.port) {
        val s = selected
        info = if (s != null) PhotoSender.getInfo(s.host, s.port) else null
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
                TopBar(onSettings = { showSettings = true })
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
                    else Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        screens.forEach { s -> ScreenRow(s) { selected = s } }
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
                        onBack = { selected = null; info = null },
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

@Composable
private fun ManualConnectDialog(onConnect: (String, Int) -> Unit, onDismiss: () -> Unit) {
    var host by remember { mutableStateOf("") }
    var port by remember { mutableStateOf("") }
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier.clip(RoundedCornerShape(24.dp)).background(com.meylon.salongallery.ui.theme.ElecSurface)
                .border(1.dp, ElecBorder, RoundedCornerShape(24.dp)).padding(24.dp),
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
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = port, onValueChange = { port = it.filter { c -> c.isDigit() } }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Port", color = TextTertiary) }, label = { Text("Port", color = TextSecondary) }, colors = tf,
            )
            Spacer(Modifier.height(18.dp))
            GradientButton(
                text = stringResource(R.string.remote_connect),
                enabled = host.isNotBlank() && port.toIntOrNull() != null,
                onClick = { port.toIntOrNull()?.let { onConnect(host.trim(), it) } },
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

    LaunchedEffect(screen.host, screen.port) {
        PhotoSender.getSettings(screen.host, screen.port)?.let {
            name = it.name; hasPin = it.hasPin; schedOn = it.schedOn
            sleepStart = it.sleepStart; sleepEnd = it.sleepEnd; loaded = true
        }
    }

    val tf = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
        focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
    )

    Column(Modifier.fillMaxWidth()) {
        SectionLabel(stringResource(R.string.admin_section))

        Spacer(Modifier.height(14.dp))
        Text(stringResource(R.string.admin_name), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = name, onValueChange = { name = it; nameSaved = false }, singleLine = true,
            modifier = Modifier.fillMaxWidth(), colors = tf,
            placeholder = { Text(stringResource(R.string.admin_name_hint), color = TextTertiary) },
        )
        Spacer(Modifier.height(10.dp))
        GradientButton(
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
            singleLine = true, modifier = Modifier.fillMaxWidth(), colors = tf,
            placeholder = { Text(stringResource(R.string.admin_pin_hint), color = TextTertiary) },
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword,
            ),
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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

        Spacer(Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
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

        Spacer(Modifier.height(22.dp))
        Text(stringResource(R.string.remote_change_role_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        OutlineButton(text = stringResource(R.string.remote_change_role), leading = Icons.Outlined.SwapHoriz, onClick = { confirmReset = true })
    }

    if (confirmReset) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { confirmReset = false }) {
            Column(
                Modifier.clip(RoundedCornerShape(24.dp)).background(com.meylon.salongallery.ui.theme.ElecSurface)
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
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(com.meylon.salongallery.ui.theme.ElecSurface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
        RemoteStepBtn("−") { onChange((minutes - 30 + 1440) % 1440) }
        Text(
            com.meylon.salongallery.net.DisplayPrefs.fmt(minutes),
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
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0 to 0) } // done to total

    var frameId by remember { mutableIntStateOf(0) }
    var frameWidth by remember { mutableStateOf(1f) }
    var frameRandom by remember { mutableStateOf(false) }
    var framePool by remember { mutableStateOf(setOf(1, 3, 4, 8)) }
    var shuffle by remember { mutableStateOf(false) }
    var intervalMs by remember { mutableStateOf(30000L) }
    var orientation by remember { mutableStateOf("auto") }
    var effect by remember { mutableStateOf("fade") }
    var effectPool by remember { mutableStateOf(setOf("fade", "slide", "zoom", "dissolve")) }
    var filter by remember { mutableStateOf("none") }
    var fit by remember { mutableStateOf("fill") }
    var collage by remember { mutableStateOf(false) }
    var showFrames by remember { mutableStateOf(false) }
    var showEffects by remember { mutableStateOf(false) }
    var showSlideshow by remember { mutableStateOf(false) }
    var showLibrary by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }
    var showMusic by remember { mutableStateOf(false) }
    var showArt by remember { mutableStateOf(false) }
    var showRss by remember { mutableStateOf(false) }
    var lib by remember { mutableStateOf<LibraryList?>(null) }

    var screenBrightness by remember { mutableStateOf(1f) }
    var screenVolume by remember { mutableStateOf(1f) }
    var music by remember { mutableStateOf<MusicState?>(null) }
    fun refreshMusic() { scope.launch { PhotoSender.getMusic(screen.host, screen.port)?.let { music = it } } }
    // Keep the mini-player in sync with the screen.
    LaunchedEffect(screen.host, screen.port) {
        while (true) { PhotoSender.getMusic(screen.host, screen.port)?.let { music = it }; kotlinx.coroutines.delay(4000) }
    }
    fun refreshLib() { scope.launch { PhotoSender.getList(screen.host, screen.port)?.let { lib = it } } }
    LaunchedEffect(screen.host, screen.port) {
        refreshLib()
        PhotoSender.getInfo(screen.host, screen.port)?.let { screenBrightness = it.brightness; screenVolume = it.volume }
    }

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
    val mediaPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        busy = true; status = null; progress = 0 to uris.size
        scope.launch {
            var ok = 0
            uris.forEachIndexed { i, uri ->
                val isVideo = context.contentResolver.getType(uri)?.startsWith("video") == true
                val bytes = readBytes(uri)
                if (bytes != null) {
                    val err = if (isVideo) PhotoSender.sendVideo(screen.host, screen.port, bytes)
                        else PhotoSender.sendPhoto(screen.host, screen.port, bytes)
                    if (err == null) ok++
                }
                progress = (i + 1) to uris.size
            }
            busy = false
            status = "Added $ok / ${uris.size} ✓"
            onInfoRefresh(scope); refreshLib()
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
        NowShowingHero(
            screen = screen,
            current = lib?.let { it.items.getOrNull(it.current) },
            screenName = screen.name,
        )

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
            onShow = { name -> scope.launch { PhotoSender.showNow(screen.host, screen.port, name); refreshLib() } },
            onOpen = { showLibrary = true },
        )

        Spacer(Modifier.height(24.dp))
        SectionLabel(stringResource(R.string.home_add))
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HomeAction(Modifier.weight(1f), Icons.Outlined.AddPhotoAlternate, stringResource(R.string.home_photos), NeonCyan, TintClay, !busy) {
                mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            }
            HomeAction(Modifier.weight(1f), Icons.Outlined.Palette, stringResource(R.string.home_art), NeonTeal, TintSage, !busy) { showArt = true }
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
            HomeAction(Modifier.weight(1f), Icons.Outlined.TextFields, stringResource(R.string.home_text), NeonViolet, TintPlum, true) { showText = true }
            HomeAction(Modifier.weight(1f), Icons.Outlined.RssFeed, stringResource(R.string.rss_title), NeonCyan, TintClay, true) { showRss = true }
        }

        Spacer(Modifier.height(18.dp))
        Box(Modifier.fillMaxWidth().height(22.dp), contentAlignment = Alignment.Center) {
            when {
                busy && progress.second > 1 -> ProgressRow("Sending ${progress.first}/${progress.second}…")
                busy -> ProgressRow(stringResource(R.string.remote_sending))
                status != null -> Text(
                    status!!, style = MaterialTheme.typography.labelMedium,
                    color = if (status!!.contains("✓") || status!!.contains("cleared")) GoodGreen else Color(0xFFC0503A),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.height(10.dp))
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
            effect = effect, filter = filter, pool = effectPool,
            onEffect = { effect = it; scope.launch { PhotoSender.setEffect(screen.host, screen.port, it) } },
            onFilter = { filter = it; scope.launch { PhotoSender.setFilter(screen.host, screen.port, it) } },
            onPool = { effectPool = it; scope.launch { PhotoSender.setEffectPool(screen.host, screen.port, it.toList()) } },
            onDismiss = { showEffects = false },
        )
    }
    if (showSlideshow) {
        SlideshowSheet(
            shuffle = shuffle, intervalMs = intervalMs, orientation = orientation, fit = fit, collage = collage,
            onShuffle = { shuffle = it; scope.launch { PhotoSender.setSlideshow(screen.host, screen.port, intervalMs, it) } },
            onInterval = { intervalMs = it; scope.launch { PhotoSender.setSlideshow(screen.host, screen.port, it, shuffle) } },
            onOrientation = { orientation = it; scope.launch { PhotoSender.setOrientation(screen.host, screen.port, it) } },
            onFit = { fit = it; scope.launch { PhotoSender.setFit(screen.host, screen.port, it) } },
            onCollage = { collage = it; scope.launch { PhotoSender.setCollage(screen.host, screen.port, it) } },
            onDismiss = { showSlideshow = false },
        )
    }
    if (showLibrary) {
        LibraryManager(screen = screen, bottomInset = bottomInset, onClose = { showLibrary = false; onInfoRefresh(scope); refreshLib() })
    }
    if (showText) {
        TextSheet(
            onText = { content, pos, size, color ->
                scope.launch { PhotoSender.setText(screen.host, screen.port, content, pos, size, color) }
            },
            onClock = { on, pos, date, style, size ->
                scope.launch { PhotoSender.setClock(screen.host, screen.port, on, pos, date, style, size) }
            },
            onClear = {
                scope.launch {
                    PhotoSender.setText(screen.host, screen.port, "", "bottom", "m", "white")
                    PhotoSender.setClock(screen.host, screen.port, false)
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
    if (showArt) {
        ArtSheet(screen = screen, onDismiss = { showArt = false; onInfoRefresh(scope); refreshLib() })
    }
    if (showRss) {
        RssSheet(screen = screen, onDismiss = { showRss = false })
    }
}

/** The big "now showing on your wall" framed preview + connection status. */
@Composable
private fun NowShowingHero(screen: DiscoveredScreen, current: String?, screenName: String) {
    Column(Modifier.fillMaxWidth()) {
        Box(
            Modifier.fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(24.dp), spotColor = Color(0xCC000000), ambientColor = Color(0x66000000))
                .clip(RoundedCornerShape(24.dp))
                .background(ElecSurface)
                .border(1.dp, ElecBorder, RoundedCornerShape(24.dp))
                .padding(6.dp),
        ) {
            Box(Modifier.fillMaxWidth().height(224.dp).clip(RoundedCornerShape(17.dp)).background(ElecSurfaceElevated)) {
                if (current != null) {
                    AsyncImage(
                        model = PhotoSender.fullUrl(screen.host, screen.port, current),
                        contentDescription = null, contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
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
            }
        }
        Spacer(Modifier.height(13.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(9.dp).clip(CircleShape).background(GoodGreen))
            Spacer(Modifier.width(9.dp))
            Text(stringResource(R.string.home_on_wall) + " ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(screenName, style = MaterialTheme.typography.bodyMedium, fontFamily = com.meylon.salongallery.ui.theme.ContentFont, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 1)
            Spacer(Modifier.weight(1f))
            Text(stringResource(R.string.remote_connected), style = MaterialTheme.typography.labelMedium, color = GoodGreen, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Horizontal strip of library thumbnails; tap to show one now, or open the manager. */
@Composable
private fun LibraryStrip(screen: DiscoveredScreen, items: List<String>, current: Int, onShow: (String) -> Unit, onOpen: () -> Unit) {
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
            ) {
                AsyncImage(
                    model = PhotoSender.thumbUrl(screen.host, screen.port, name),
                    contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(),
                )
                if (i == current) {
                    Text(
                        stringResource(R.string.badge_now),
                        style = MaterialTheme.typography.labelSmall, color = Color.White,
                        modifier = Modifier.padding(7.dp).clip(RoundedCornerShape(50)).background(GoodGreen).padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
        }
        Box(
            Modifier.width(64.dp).height(118.dp).clip(RoundedCornerShape(15.dp))
                .border(1.dp, ElecBorder, RoundedCornerShape(15.dp))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onOpen() },
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.ChevronRight, null, tint = NeonCyan, modifier = Modifier.size(22.dp))
                Spacer(Modifier.height(4.dp))
                Text(stringResource(R.string.album_all), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            }
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryManager(screen: DiscoveredScreen, bottomInset: androidx.compose.ui.unit.Dp = 0.dp, onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf<List<String>>(emptyList()) }
    var current by remember { mutableIntStateOf(0) }
    var pinned by remember { mutableStateOf<Set<String>>(emptySet()) }
    var durations by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var albums by remember { mutableStateOf<List<AlbumInfo>>(emptyList()) }
    var activeId by remember { mutableStateOf("all") }
    var loading by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var addTarget by remember { mutableStateOf<String?>(null) }
    var studioPhoto by remember { mutableStateOf<String?>(null) }
    var durationTarget by remember { mutableStateOf<String?>(null) }

    fun refresh() {
        scope.launch {
            val l = PhotoSender.getList(screen.host, screen.port)
            if (l != null) { items = l.items; current = l.current; activeId = l.albumId; pinned = l.pinned; durations = l.durations }
            val a = PhotoSender.getAlbums(screen.host, screen.port)
            if (a != null) albums = a.albums
            loading = false
        }
    }
    LaunchedEffect(Unit) { refresh() }

    val addPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        busy = true
        scope.launch {
            uris.forEach { u ->
                val isVideo = context.contentResolver.getType(u)?.startsWith("video") == true
                val b = withContext(Dispatchers.IO) {
                    runCatching { context.contentResolver.openInputStream(u)?.use { it.readBytes() } }.getOrNull()
                }
                if (b != null) {
                    if (isVideo) PhotoSender.sendVideo(screen.host, screen.port, b)
                    else PhotoSender.sendPhoto(screen.host, screen.port, b)
                }
            }
            busy = false; refresh()
        }
    }

    val lazyState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyState) { from, to ->
        items = items.toMutableList().apply { add(to.index, removeAt(from.index)) }
    }
    val isAll = activeId == "all"

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
        Box(Modifier.fillMaxSize().background(com.meylon.salongallery.ui.theme.ElecBg)) {
            SalonBackground {
                Column(Modifier.fillMaxSize().safeDrawingPadding().padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp + bottomInset)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RoundIconBtn(Icons.AutoMirrored.Outlined.ArrowBack) { onClose() }
                        Spacer(Modifier.size(14.dp))
                        Text("${stringResource(R.string.library_title)} · ${items.size}", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                        Spacer(Modifier.weight(1f))
                        if (busy) CircularProgressIndicator(color = NeonCyan, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                        else RoundIconBtn(Icons.Outlined.Add, accent = true) {
                            addPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
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
                            AlbumChip("${al.name} · ${al.count}", activeId == al.id) {
                                scope.launch { PhotoSender.setActiveAlbum(screen.host, screen.port, al.id); refresh() }
                            }
                        }
                        NewAlbumChip { showNew = true }
                    }
                    if (!isAll) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            stringResource(R.string.album_delete),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFF87171),
                            modifier = Modifier
                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                    val id = activeId
                                    scope.launch { PhotoSender.deleteAlbum(screen.host, screen.port, id); PhotoSender.setActiveAlbum(screen.host, screen.port, "all"); refresh() }
                                }
                                .padding(vertical = 4.dp),
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(stringResource(R.string.library_hint), style = MaterialTheme.typography.labelMedium, color = TextTertiary)
                    Spacer(Modifier.height(12.dp))
                    when {
                        loading -> Box(Modifier.fillMaxWidth().padding(40.dp), Alignment.Center) { CircularProgressIndicator(color = NeonCyan) }
                        items.isEmpty() -> Text(
                            stringResource(if (isAll) R.string.library_empty else R.string.album_empty),
                            style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
                        )
                        else -> LazyColumn(state = lazyState, modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            itemsIndexed(items, key = { _, n -> n }) { i, name ->
                                ReorderableItem(reorderState, key = name) { isDragging ->
                                    val isNow = i == current
                                    val isNext = items.size > 1 && i == (current + 1) % items.size
                                    val isPinned = name in pinned
                                    val isVideo = name.startsWith("v_")
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .border(
                                                if (isNow || isDragging) 1.5.dp else 1.dp,
                                                if (isDragging) NeonViolet else if (isNow) NeonCyan else ElecBorder,
                                                RoundedCornerShape(16.dp),
                                            )
                                            .background(ElecSurface)
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Box(
                                            Modifier.size(58.dp).clip(RoundedCornerShape(10.dp)).background(ElecSurfaceElevated)
                                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                                    scope.launch { PhotoSender.showNow(screen.host, screen.port, name); refresh() }
                                                },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            AsyncImage(
                                                model = PhotoSender.thumbUrl(screen.host, screen.port, name),
                                                contentDescription = null, contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize(),
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
                                                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { durationTarget = name }
                                                        .padding(horizontal = 9.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                ) {
                                                    Icon(Icons.Outlined.Timer, null, tint = TextSecondary, modifier = Modifier.size(13.dp))
                                                    Text(durLabel, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                                                }
                                            }
                                        }
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
                                            onDuration = { durationTarget = name },
                                            onPin = { scope.launch { PhotoSender.setPinned(screen.host, screen.port, name, !isPinned); refresh() } },
                                            onAddAlbum = { addTarget = name },
                                            onDelete = {
                                                items = items.filterIndexed { j, _ -> j != i }
                                                scope.launch {
                                                    if (isAll) PhotoSender.deletePhoto(screen.host, screen.port, name)
                                                    else PhotoSender.removeFromAlbum(screen.host, screen.port, activeId, name)
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
    addTarget?.let { photo ->
        AddToAlbumSheet(
            albums = albums,
            onPick = { id -> addTarget = null; scope.launch { PhotoSender.addToAlbum(screen.host, screen.port, id, photo); refresh() } },
            onNew = { addTarget = null; showNew = true },
            onDismiss = { addTarget = null },
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
    onEdit: () -> Unit, onDuration: () -> Unit, onPin: () -> Unit,
    onAddAlbum: () -> Unit, onDelete: () -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Box {
        SmallBtn(Icons.Outlined.MoreVert, TextSecondary) { open = true }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }, containerColor = com.meylon.salongallery.ui.theme.ElecSurface) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.studio_edit), color = TextPrimary) },
                leadingIcon = { Icon(Icons.Outlined.Tune, null, tint = NeonCyan) },
                onClick = { open = false; onEdit() },
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
            DropdownMenuItem(
                text = { Text(stringResource(if (isAll) R.string.delete else R.string.album_remove_from), color = Color(0xFFF87171)) },
                leadingIcon = { Icon(Icons.Outlined.Close, null, tint = Color(0xFFF87171)) },
                onClick = { open = false; onDelete() },
            )
        }
    }
}

/** Per-item display duration chooser. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DurationDialog(screen: DiscoveredScreen, name: String, onClose: () -> Unit) {
    val scope = rememberCoroutineScope()
    val isVideo = name.startsWith("v_")
    var sec by remember { mutableIntStateOf(0) }
    LaunchedEffect(name) { sec = PhotoSender.getDuration(screen.host, screen.port, name) }
    // 0 means "default" for photos, "full clip" for videos.
    val options = listOf(0, 3, 5, 10, 20, 30, 60)
    val labels = options.map { if (it == 0) (if (isVideo) "Full clip" else "Default") else "${it}s" }
    ModalBottomSheet(onDismissRequest = onClose, sheetState = rememberModalBottomSheetState(), containerColor = com.meylon.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().padding(24.dp)) {
            Text(stringResource(R.string.duration_title), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(if (isVideo) R.string.duration_hint_video else R.string.duration_hint_photo),
                style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
            )
            Spacer(Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEachIndexed { i, opt ->
                    val selected = opt == sec
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                            .border(if (selected) 1.5.dp else 1.dp, if (selected) NeonCyan else ElecBorder, RoundedCornerShape(14.dp))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                sec = opt; scope.launch { PhotoSender.setDuration(screen.host, screen.port, name, opt) }
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(labels[i], style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                        if (selected) Icon(Icons.Outlined.Check, null, tint = NeonCyan)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
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
        Box(Modifier.fillMaxSize().background(com.meylon.salongallery.ui.theme.ElecBg)) {
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
        containerColor = com.meylon.salongallery.ui.theme.ElecSurface,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddToAlbumSheet(albums: List<AlbumInfo>, onPick: (String) -> Unit, onNew: () -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(), containerColor = com.meylon.salongallery.ui.theme.ElecBg) {
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
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 1, fontFamily = com.meylon.salongallery.ui.theme.ContentFont)
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
        "BBC News" to "https://feeds.bbci.co.uk/news/rss.xml",
        "The Verge" to "https://www.theverge.com/rss/index.xml",
        "NASA" to "https://www.nasa.gov/rss/dyn/breaking_news.rss",
        "Hacker News" to "https://hnrss.org/frontpage",
        "NYT" to "https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml",
    )
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(), containerColor = com.meylon.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp)) {
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

/** All transitions as (key, label) for the Effects picker. */
private val TRANSITIONS = listOf(
    "fade" to "Fade", "dissolve" to "Dissolve", "slide" to "Slide ←", "slideright" to "Slide →",
    "slideup" to "Slide ↑", "slidedown" to "Slide ↓", "zoom" to "Zoom in", "zoomout" to "Zoom out",
    "reveal" to "Reveal", "grow" to "Grow", "swap" to "Swap", "drift" to "Drift",
    "cardstack" to "Stack", "kenburns" to "Ken Burns", "none" to "Off",
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun EffectsSheet(
    effect: String, filter: String, pool: Set<String>,
    onEffect: (String) -> Unit, onFilter: (String) -> Unit, onPool: (Set<String>) -> Unit, onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(), containerColor = com.meylon.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text(stringResource(R.string.effects_title), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)

            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.effects_transition), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(10.dp))
            val isRandom = effect == "random"
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EffectChip("🎲 " + stringResource(R.string.effects_random), selected = isRandom, showCheck = false) {
                    onEffect(if (isRandom) "fade" else "random")
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
                Text(stringResource(R.string.effects_random_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.effects_look), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(10.dp))
            EffectChip("🎲 " + stringResource(R.string.effects_random), selected = filter == "random", showCheck = false) {
                onFilter(if (filter == "random") "none" else "random")
            }
            if (filter == "random") {
                Spacer(Modifier.height(6.dp))
                Text(stringResource(R.string.look_random_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Spacer(Modifier.height(12.dp))
            val looks = listOf("none", "mono", "sepia", "warm", "cool", "vignette")
            val labels = listOf("Original", "Mono", "Sepia", "Warm", "Cool", "Vignette")
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                looks.forEachIndexed { i, key -> LookChip(labels[i], key, key == filter) { onFilter(key) } }
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
private fun LookChip(label: String, key: String, selected: Boolean, onClick: () -> Unit) {
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
                "sepia" -> Color(0xFF6E4A1E).copy(alpha = 0.42f)
                "warm" -> Color(0xFFFF8A3D).copy(alpha = 0.26f)
                "cool" -> Color(0xFF3D7AFF).copy(alpha = 0.26f)
                else -> Color.Transparent
            }
            if (tint != Color.Transparent) Box(Modifier.matchParentSize().background(tint))
            if (key == "vignette") Box(
                Modifier.matchParentSize().background(
                    Brush.radialGradient(0.0f to Color.Transparent, 0.6f to Color.Transparent, 1.0f to Color.Black.copy(alpha = 0.6f))
                )
            )
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
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(), containerColor = com.meylon.salongallery.ui.theme.ElecBg) {
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
            FramedContent(f.id, width, adaptiveColor = if (f.adaptive) Color(0xFF6E8CA8) else null, modifier = Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF7C8898), Color(0xFF3E4650)))))
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SlideshowSheet(
    shuffle: Boolean, intervalMs: Long, orientation: String, fit: String, collage: Boolean,
    onShuffle: (Boolean) -> Unit, onInterval: (Long) -> Unit, onOrientation: (String) -> Unit,
    onFit: (String) -> Unit, onCollage: (Boolean) -> Unit, onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(), containerColor = com.meylon.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text(stringResource(R.string.tile_slideshow), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.slideshow_fit), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            val fits = listOf("fill", "fit", "blur")
            SegRow(listOf("Fill", "Fit", "Blur"), fits.indexOf(fit).coerceAtLeast(0)) { onFit(fits[it]) }

            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.slideshow_order), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            SegRow(listOf("Sequential", "Shuffle"), if (shuffle) 1 else 0) { onShuffle(it == 1) }

            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.slideshow_interval), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            val intervals = listOf(10000L, 30000L, 60000L, 300000L, 900000L, 3600000L)
            SegRow(listOf("10s", "30s", "1m", "5m", "15m", "1h"), intervals.indexOf(intervalMs).coerceAtLeast(0)) { onInterval(intervals[it]) }

            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.slideshow_orientation), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            val orients = listOf("auto", "portrait", "landscape")
            SegRow(listOf("Auto", "Portrait", "Landscape"), orients.indexOf(orientation).coerceAtLeast(0)) { onOrientation(orients[it]) }

            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.slideshow_autofill), style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Text(stringResource(R.string.slideshow_autofill_hint), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Switch(checked = collage, onCheckedChange = onCollage)
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TextSheet(
    onText: (String, String, String, String) -> Unit,
    onClock: (Boolean, String, Boolean, String, String) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    var content by remember { mutableStateOf("") }
    var posIdx by remember { mutableIntStateOf(2) }
    var sizeIdx by remember { mutableIntStateOf(1) }
    var colorIdx by remember { mutableIntStateOf(0) }
    var clock by remember { mutableStateOf(false) }
    var clockPosIdx by remember { mutableIntStateOf(0) }
    var clockDate by remember { mutableStateOf(true) }
    var clockStyleIdx by remember { mutableIntStateOf(0) }
    var clockSizeIdx by remember { mutableIntStateOf(1) }
    val positions = listOf("top", "center", "bottom")
    val sizes = listOf("s", "m", "l")
    val colors = listOf("white", "black", "gold", "cyan", "violet")
    val clockPositions = listOf("top_start", "top_end", "bottom_start", "bottom_end", "center")
    val clockStyles = listOf("digital", "analog", "minimal", "mono")
    val clockSizes = listOf("s", "m", "l")

    // Live apply — no Apply button. Text is debounced; the clock applies on every change.
    // Each skips its first emission so opening the sheet doesn't clobber what's on screen.
    var textTouched by remember { mutableStateOf(false) }
    LaunchedEffect(content, posIdx, sizeIdx, colorIdx) {
        if (!textTouched) { textTouched = true; return@LaunchedEffect }
        kotlinx.coroutines.delay(250)
        onText(content, positions[posIdx], sizes[sizeIdx], colors[colorIdx])
    }
    var clockTouched by remember { mutableStateOf(false) }
    LaunchedEffect(clock, clockPosIdx, clockDate, clockStyleIdx, clockSizeIdx) {
        if (!clockTouched) { clockTouched = true; return@LaunchedEffect }
        onClock(clock, clockPositions[clockPosIdx], clockDate, clockStyles[clockStyleIdx], clockSizes[clockSizeIdx])
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(), containerColor = com.meylon.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text(stringResource(R.string.text_title), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = content, onValueChange = { content = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = com.meylon.salongallery.ui.theme.ContentFont),
                placeholder = { Text(stringResource(R.string.text_hint), color = TextTertiary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan, unfocusedBorderColor = ElecBorder,
                    focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = NeonCyan,
                ),
            )
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.text_position), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            SegRow(listOf("Top", "Center", "Bottom"), posIdx) { posIdx = it }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.text_size), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            SegRow(listOf("S", "M", "L"), sizeIdx) { sizeIdx = it }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.text_color), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            SegRow(listOf("White", "Black", "Gold", "Cyan", "Violet"), colorIdx) { colorIdx = it }
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.text_clock), style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                Switch(checked = clock, onCheckedChange = { clock = it })
            }
            if (clock) {
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.clock_style), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                SegRow(listOf("Digital", "Analog", "Minimal", "Mono"), clockStyleIdx) { clockStyleIdx = it }
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.clock_size), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                SegRow(listOf("S", "M", "L"), clockSizeIdx) { clockSizeIdx = it }
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.clock_position), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Spacer(Modifier.height(8.dp))
                SegRow(listOf("↖", "↗", "↙", "↘", "•"), clockPosIdx) { clockPosIdx = it }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.clock_show_date), style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                    Switch(checked = clockDate, onCheckedChange = { clockDate = it })
                }
            }
            Spacer(Modifier.height(20.dp))
            OutlineButton(text = stringResource(R.string.text_clear), onClick = { content = ""; clock = false; onClear() })
            Spacer(Modifier.height(16.dp))
        }
    }
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

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(), containerColor = com.meylon.salongallery.ui.theme.ElecBg) {
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
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(), containerColor = com.meylon.salongallery.ui.theme.ElecBg) {
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

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(), containerColor = com.meylon.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 20.dp)) {
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
                Modifier.weight(1f).height(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .then(if (sel) Modifier.background(AccentGradient) else Modifier.border(1.dp, ElecBorder, RoundedCornerShape(12.dp)))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onSelect(i) },
                contentAlignment = Alignment.Center,
            ) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = if (sel) Color(0xFF07121F) else TextPrimary)
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
private fun ScreenRow(screen: DiscoveredScreen, onClick: () -> Unit) {
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
            Text(screen.host, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}
