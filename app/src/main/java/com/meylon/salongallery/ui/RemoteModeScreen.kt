package com.meylon.salongallery.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.DragIndicator
import androidx.compose.material.icons.outlined.FilterFrames
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Slideshow
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.meylon.salongallery.R
import com.meylon.salongallery.net.DiscoveredScreen
import com.meylon.salongallery.net.PhotoSender
import com.meylon.salongallery.net.RemoteSession
import com.meylon.salongallery.net.ScreenInfo
import com.meylon.salongallery.ui.components.AccentGradient
import com.meylon.salongallery.ui.components.OutlineButton
import com.meylon.salongallery.ui.components.SalonBackground
import com.meylon.salongallery.ui.components.SectionLabel
import com.meylon.salongallery.ui.theme.ElecBorder
import com.meylon.salongallery.ui.theme.ElecSurface
import com.meylon.salongallery.ui.theme.GoodGreen
import com.meylon.salongallery.ui.theme.NeonBlue
import com.meylon.salongallery.ui.theme.NeonCyan
import com.meylon.salongallery.ui.theme.NeonTeal
import com.meylon.salongallery.ui.theme.NeonViolet
import com.meylon.salongallery.ui.theme.NeonVioletLight
import com.meylon.salongallery.ui.theme.TextPrimary
import com.meylon.salongallery.ui.theme.TextSecondary
import com.meylon.salongallery.ui.theme.TextTertiary
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RemoteModeScreen(actions: AppActions) {
    val context = LocalContext.current
    val remoteScope = rememberCoroutineScope()
    val session = remember { RemoteSession(context) }

    DisposableEffect(Unit) {
        session.start()
        onDispose { session.stop() }
    }

    val screens by session.screens.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf<DiscoveredScreen?>(null) }
    var showSettings by remember { mutableStateOf(false) }
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
                } else {
                    ControlPanel(
                        screen = target,
                        onBack = { selected = null; info = null },
                        onInfoRefresh = { scope -> scope.launch { info = PhotoSender.getInfo(target.host, target.port) } },
                    )
                }
            }
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
                    Spacer(Modifier.height(12.dp))
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
private fun ControlPanel(
    screen: DiscoveredScreen,
    onBack: () -> Unit,
    onInfoRefresh: (kotlinx.coroutines.CoroutineScope) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0 to 0) } // done to total

    var frameId by remember { mutableIntStateOf(0) }
    var shuffle by remember { mutableStateOf(false) }
    var intervalMs by remember { mutableStateOf(8000L) }
    var orientation by remember { mutableStateOf("auto") }
    var effect by remember { mutableStateOf("fade") }
    var fit by remember { mutableStateOf("fill") }
    var showFrames by remember { mutableStateOf(false) }
    var showSlideshow by remember { mutableStateOf(false) }
    var showLibrary by remember { mutableStateOf(false) }

    suspend fun readBytes(uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
        runCatching { context.contentResolver.openInputStream(uri)?.use { it.readBytes() } }.getOrNull()
    }

    val photosPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        busy = true; status = null; progress = 0 to uris.size
        scope.launch {
            var ok = 0
            uris.forEachIndexed { i, uri ->
                val bytes = readBytes(uri)
                if (bytes != null && PhotoSender.sendPhoto(screen.host, screen.port, bytes) == null) ok++
                progress = (i + 1) to uris.size
            }
            busy = false
            status = "Sent $ok / ${uris.size} photos ✓"
            onInfoRefresh(scope)
        }
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        busy = true; status = null; progress = 0 to 0
        scope.launch {
            val bytes = readBytes(uri)
            val err = if (bytes != null) PhotoSender.sendVideo(screen.host, screen.port, bytes) else "read failed"
            busy = false; status = if (err == null) "Video sent ✓" else "Couldn't send · $err"
        }
    }
    val musicPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        busy = true; status = null; progress = 0 to 0
        scope.launch {
            val bytes = readBytes(uri)
            val err = if (bytes != null) PhotoSender.sendMusic(screen.host, screen.port, bytes) else "read failed"
            busy = false; status = if (err == null) "Music set ✓" else "Couldn't send · $err"
        }
    }

    Column(Modifier.fillMaxWidth()) {
        ConnectedHeader(screen.name)

        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CompactSlider(Modifier.weight(1f), Icons.Outlined.BrightnessMedium, NeonTeal) { v ->
                scope.launch { PhotoSender.setBrightness(screen.host, screen.port, v) }
            }
            CompactSlider(Modifier.weight(1f), Icons.AutoMirrored.Outlined.VolumeUp, NeonCyan) { v ->
                scope.launch { PhotoSender.setVolume(screen.host, screen.port, v) }
            }
        }

        Spacer(Modifier.height(12.dp))
        LibraryButton(onClick = { showLibrary = true })

        Spacer(Modifier.height(10.dp))
        WideButton(Icons.Outlined.Slideshow, stringResource(R.string.tile_slideshow), NeonBlue) { showSlideshow = true }

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionTile(Modifier.weight(1f), Icons.Outlined.PhotoLibrary, stringResource(R.string.tile_photos), NeonCyan, !busy) {
                photosPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            ActionTile(Modifier.weight(1f), Icons.Outlined.Movie, stringResource(R.string.tile_video), NeonViolet, !busy) {
                videoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionTile(Modifier.weight(1f), Icons.Outlined.FilterFrames, stringResource(R.string.tile_frame), NeonTeal, !busy) {
                showFrames = true
            }
            ActionTile(Modifier.weight(1f), Icons.Outlined.MusicNote, stringResource(R.string.tile_music), NeonVioletLight, !busy) {
                musicPicker.launch("audio/*")
            }
        }

        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(24.dp), contentAlignment = Alignment.Center) {
            when {
                busy && progress.second > 1 -> ProgressRow("Sending ${progress.first}/${progress.second}…")
                busy -> ProgressRow(stringResource(R.string.remote_sending))
                status != null -> Text(
                    status!!, style = MaterialTheme.typography.labelMedium,
                    color = if (status!!.contains("✓") || status!!.contains("cleared")) GoodGreen else Color(0xFFF87171),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlineButton(text = stringResource(R.string.remote_disconnect), onClick = onBack)
        Spacer(Modifier.height(20.dp))
    }

    if (showFrames) {
        FrameSheet(current = frameId, onPick = { id ->
            frameId = id; scope.launch { PhotoSender.setFrame(screen.host, screen.port, id) }
        }, onDismiss = { showFrames = false })
    }
    if (showSlideshow) {
        SlideshowSheet(
            shuffle = shuffle, intervalMs = intervalMs, orientation = orientation, effect = effect, fit = fit,
            onShuffle = { shuffle = it; scope.launch { PhotoSender.setSlideshow(screen.host, screen.port, intervalMs, it) } },
            onInterval = { intervalMs = it; scope.launch { PhotoSender.setSlideshow(screen.host, screen.port, it, shuffle) } },
            onOrientation = { orientation = it; scope.launch { PhotoSender.setOrientation(screen.host, screen.port, it) } },
            onEffect = { effect = it; scope.launch { PhotoSender.setEffect(screen.host, screen.port, it) } },
            onFit = { fit = it; scope.launch { PhotoSender.setFit(screen.host, screen.port, it) } },
            onDismiss = { showSlideshow = false },
        )
    }
    if (showLibrary) {
        LibraryManager(screen = screen, onClose = { showLibrary = false; onInfoRefresh(scope) })
    }
}

@Composable
private fun LibraryButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .background(ElecSurface)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(NeonCyan.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Outlined.Collections, null, tint = NeonCyan, modifier = Modifier.size(22.dp)) }
        Spacer(Modifier.size(14.dp))
        Text(stringResource(R.string.library_manage), style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun LibraryManager(screen: DiscoveredScreen, onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf<List<String>>(emptyList()) }
    var current by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }

    fun refresh() {
        scope.launch {
            val l = PhotoSender.getList(screen.host, screen.port)
            if (l != null) { items = l.items; current = l.current }
            loading = false
        }
    }
    LaunchedEffect(Unit) { refresh() }

    val addPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        busy = true
        scope.launch {
            uris.forEach { u ->
                val b = withContext(Dispatchers.IO) {
                    runCatching { context.contentResolver.openInputStream(u)?.use { it.readBytes() } }.getOrNull()
                }
                if (b != null) PhotoSender.sendPhoto(screen.host, screen.port, b)
            }
            busy = false; refresh()
        }
    }

    val lazyState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyState) { from, to ->
        items = items.toMutableList().apply { add(to.index, removeAt(from.index)) }
    }

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(com.meylon.salongallery.ui.theme.ElecBg)) {
            SalonBackground {
                Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RoundIconBtn(Icons.AutoMirrored.Outlined.ArrowBack) { onClose() }
                        Spacer(Modifier.size(14.dp))
                        Text("${stringResource(R.string.library_title)} · ${items.size}", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                        Spacer(Modifier.weight(1f))
                        if (busy) CircularProgressIndicator(color = NeonCyan, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                        else RoundIconBtn(Icons.Outlined.Add, accent = true) {
                            addPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.library_hint), style = MaterialTheme.typography.labelMedium, color = TextTertiary)
                    Spacer(Modifier.height(12.dp))
                    when {
                        loading -> Box(Modifier.fillMaxWidth().padding(40.dp), Alignment.Center) { CircularProgressIndicator(color = NeonCyan) }
                        items.isEmpty() -> Text(stringResource(R.string.library_empty), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        else -> LazyColumn(state = lazyState, modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            itemsIndexed(items, key = { _, n -> n }) { i, name ->
                                ReorderableItem(reorderState, key = name) { isDragging ->
                                    val isNow = i == current
                                    val isNext = items.size > 1 && i == (current + 1) % items.size
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
                                        AsyncImage(
                                            model = PhotoSender.thumbUrl(screen.host, screen.port, name),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(58.dp).clip(RoundedCornerShape(10.dp))
                                                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                                    scope.launch { PhotoSender.showNow(screen.host, screen.port, name); refresh() }
                                                },
                                        )
                                        Spacer(Modifier.size(12.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text("#${i + 1}", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                            if (isNow) Badge(stringResource(R.string.badge_now), NeonCyan)
                                            else if (isNext) Badge(stringResource(R.string.badge_next), NeonViolet)
                                        }
                                        Icon(
                                            Icons.Outlined.DragIndicator, contentDescription = "Drag to reorder",
                                            tint = TextSecondary,
                                            modifier = Modifier
                                                .size(40.dp).padding(8.dp)
                                                .draggableHandle(
                                                    onDragStopped = { scope.launch { PhotoSender.reorder(screen.host, screen.port, items) } },
                                                ),
                                        )
                                        SmallBtn(Icons.Outlined.Close, Color(0xFFF87171)) {
                                            items = items.filterIndexed { j, _ -> j != i }
                                            scope.launch { PhotoSender.deletePhoto(screen.host, screen.port, name); refresh() }
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
}

@Composable
private fun RoundIconBtn(icon: ImageVector, accent: Boolean = false, onClick: () -> Unit) {
    Box(
        Modifier.size(42.dp).clip(RoundedCornerShape(50))
            .border(1.dp, if (accent) NeonCyan.copy(alpha = 0.5f) else ElecBorder, RoundedCornerShape(50))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
        contentAlignment = Alignment.Center,
    ) { Icon(icon, null, tint = if (accent) NeonCyan else TextPrimary, modifier = Modifier.size(22.dp)) }
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
private fun SmallBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, onClick: () -> Unit) {
    Box(
        Modifier.size(38.dp).clip(RoundedCornerShape(10.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
        contentAlignment = Alignment.Center,
    ) { Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp)) }
}

@Composable
private fun ProgressRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CircularProgressIndicator(color = NeonCyan, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
    }
}

@Composable
private fun ConnectedHeader(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AccentGradient)
            .padding(1.5.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(ElecSurface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(AccentGradient),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Outlined.Tv, null, tint = Color.White, modifier = Modifier.size(22.dp)) }
        Spacer(Modifier.size(14.dp))
        Column(Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Text(stringResource(R.string.remote_connected), style = MaterialTheme.typography.bodyMedium, color = GoodGreen)
        }
    }
}

@Composable
private fun ActionTile(
    modifier: Modifier, icon: ImageVector, label: String, accent: Color, enabled: Boolean, onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .height(116.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(ElecSurface)
            .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(16.dp),
    ) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = accent, modifier = Modifier.size(24.dp)) }
        Spacer(Modifier.weight(1f))
        Text(label, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
    }
}

@Composable
private fun CompactSlider(modifier: Modifier, icon: ImageVector, accent: Color, onCommit: (Float) -> Unit) {
    var value by remember { mutableFloatStateOf(0.5f) }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ElecSurface)
            .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(16.dp))
            .padding(start = 12.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp))
        Slider(
            value = value, onValueChange = { value = it }, onValueChangeFinished = { onCommit(value) },
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent, inactiveTrackColor = ElecBorder),
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrameSheet(current: Int, onPick: (Int) -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(), containerColor = com.meylon.salongallery.ui.theme.ElecBg) {
        Column(Modifier.fillMaxWidth().padding(24.dp)) {
            Text(stringResource(R.string.tile_frame), style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(16.dp))
            FRAMES.forEach { f ->
                val sel = f.id == current
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(if (sel) 1.5.dp else 1.dp, if (sel) NeonCyan else ElecBorder, RoundedCornerShape(14.dp))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onPick(f.id) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val swatch = f.moldingColors.firstOrNull() ?: f.matColor ?: Color.Black
                    Box(Modifier.size(30.dp).clip(RoundedCornerShape(7.dp)).background(swatch).border(1.dp, ElecBorder, RoundedCornerShape(7.dp)))
                    Spacer(Modifier.size(12.dp))
                    Text(f.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SlideshowSheet(
    shuffle: Boolean, intervalMs: Long, orientation: String, effect: String, fit: String,
    onShuffle: (Boolean) -> Unit, onInterval: (Long) -> Unit, onOrientation: (String) -> Unit,
    onEffect: (String) -> Unit, onFit: (String) -> Unit, onDismiss: () -> Unit,
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
            val intervals = listOf(5000L, 10000L, 30000L, 60000L)
            SegRow(listOf("5s", "10s", "30s", "1m"), intervals.indexOf(intervalMs).coerceAtLeast(0)) { onInterval(intervals[it]) }

            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.slideshow_effect), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            val effects = listOf("fade", "slide", "zoom", "kenburns", "none")
            SegRow(listOf("Fade", "Slide", "Zoom", "Ken", "Off"), effects.indexOf(effect).coerceAtLeast(0)) { onEffect(effects[it]) }

            Spacer(Modifier.height(18.dp))
            Text(stringResource(R.string.slideshow_orientation), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            val orients = listOf("auto", "portrait", "landscape")
            SegRow(listOf("Auto", "Portrait", "Landscape"), orients.indexOf(orientation).coerceAtLeast(0)) { onOrientation(orients[it]) }
            Spacer(Modifier.height(20.dp))
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
