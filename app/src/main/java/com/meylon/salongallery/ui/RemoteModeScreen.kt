package com.meylon.salongallery.ui

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.meylon.salongallery.ui.theme.NeonCyan
import com.meylon.salongallery.ui.theme.NeonTeal
import com.meylon.salongallery.ui.theme.NeonViolet
import com.meylon.salongallery.ui.theme.TextPrimary
import com.meylon.salongallery.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RemoteModeScreen(actions: AppActions) {
    val context = LocalContext.current
    val session = remember { RemoteSession(context) }

    DisposableEffect(Unit) {
        session.start()
        onDispose { session.stop() }
    }

    val screens by session.screens.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf<DiscoveredScreen?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    SalonBackground {
        Column(
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .widthIn(max = 560.dp)
        ) {
            TopBar(onSettings = { showSettings = true })
            Spacer(Modifier.height(28.dp))

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
                if (screens.isEmpty()) {
                    Searching()
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        screens.forEach { s ->
                            ScreenRow(screen = s, onClick = { selected = s })
                        }
                    }
                }
            } else {
                ControlPanel(screen = target, onBack = { selected = null })
            }
        }
    }

    if (showSettings) {
        SettingsSheet(actions = actions, onDismiss = { showSettings = false })
    }
}

@Composable
private fun ControlPanel(screen: DiscoveredScreen, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var info by remember { mutableStateOf<com.meylon.salongallery.net.ScreenInfo?>(null) }

    LaunchedEffect(screen.host, screen.port) {
        info = PhotoSender.getInfo(screen.host, screen.port)
    }

    fun sendMedia(uri: android.net.Uri?, video: Boolean) {
        if (uri == null) return
        busy = true; status = null
        scope.launch {
            val bytes = withContext(Dispatchers.IO) {
                runCatching { context.contentResolver.openInputStream(uri)?.use { it.readBytes() } }.getOrNull()
            }
            val err = if (bytes == null) "read failed"
            else if (video) PhotoSender.sendVideo(screen.host, screen.port, bytes)
            else PhotoSender.sendPhoto(screen.host, screen.port, bytes)
            busy = false
            status = if (err == null) (if (video) "Video sent ✓" else "Photo sent ✓") else "Couldn't send · $err"
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        sendMedia(it, video = false)
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        sendMedia(it, video = true)
    }

    Column(Modifier.fillMaxWidth()) {
        // Connected header (gradient border)
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
                Text(screen.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Text(stringResource(R.string.remote_connected), style = MaterialTheme.typography.bodyMedium, color = GoodGreen)
            }
        }

        info?.let {
            Spacer(Modifier.height(12.dp))
            ScreenInfoStrip(it)
        }

        Spacer(Modifier.height(16.dp))
        // 2x2 tile grid
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.PhotoLibrary,
                label = stringResource(R.string.tile_photo),
                accent = NeonCyan,
                enabled = !busy,
                onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            )
            ActionTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Movie,
                label = stringResource(R.string.tile_video),
                accent = NeonViolet,
                enabled = !busy,
                onClick = { videoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) },
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SliderTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.BrightnessMedium,
                label = stringResource(R.string.tile_brightness),
                accent = NeonTeal,
                onCommit = { v -> scope.launch { PhotoSender.setBrightness(screen.host, screen.port, v) } },
            )
            SliderTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.VolumeUp,
                label = stringResource(R.string.tile_volume),
                accent = NeonCyan,
                onCommit = { v -> scope.launch { PhotoSender.setVolume(screen.host, screen.port, v) } },
            )
        }

        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().height(22.dp), contentAlignment = Alignment.Center) {
            if (busy) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CircularProgressIndicator(color = NeonCyan, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                    Text(stringResource(R.string.remote_sending), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                }
            } else if (status != null) {
                Text(
                    status!!,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (status!!.contains("✓")) GoodGreen else Color(0xFFF87171),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlineButton(text = stringResource(R.string.remote_disconnect), onClick = onBack)
    }
}

@Composable
private fun ScreenInfoStrip(info: ScreenInfo) {
    val usedFrac = if (info.totalBytes > 0)
        (1f - info.freeBytes.toFloat() / info.totalBytes).coerceIn(0f, 1f) else 0f
    val warn = usedFrac >= 0.9f
    val warnColor = Color(0xFFF87171)
    Column(Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (info.widthPx > 0) InfoPill("${info.widthPx} × ${info.heightPx}")
            if (info.totalBytes > 0) InfoPill("${fmtBytes(info.freeBytes)} free of ${fmtBytes(info.totalBytes)}")
        }
        if (info.totalBytes > 0) {
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { usedFrac },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                color = if (warn) warnColor else NeonCyan,
                trackColor = ElecBorder,
            )
            if (warn) {
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.storage_warning),
                    style = MaterialTheme.typography.labelMedium,
                    color = warnColor,
                )
            }
        }
    }
}

@Composable
private fun InfoPill(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = TextSecondary,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, ElecBorder, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

private fun fmtBytes(b: Long): String {
    val gb = b / 1_000_000_000.0
    return if (gb >= 1) String.format("%.1f GB", gb) else String.format("%.0f MB", b / 1_000_000.0)
}

@Composable
private fun ActionTile(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    accent: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(ElecSurface)
            .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(18.dp),
    ) {
        Box(
            Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = accent, modifier = Modifier.size(26.dp)) }
        Spacer(Modifier.weight(1f))
        Text(label, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
    }
}

@Composable
private fun SliderTile(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    accent: Color,
    onCommit: (Float) -> Unit,
) {
    var value by remember { mutableFloatStateOf(0.5f) }
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(ElecSurface)
            .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) { Icon(icon, null, tint = accent, modifier = Modifier.size(22.dp)) }
            Spacer(Modifier.weight(1f))
            Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        }
        Spacer(Modifier.weight(1f))
        Text(label, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Slider(
            value = value,
            onValueChange = { value = it },
            onValueChangeFinished = { onCommit(value) },
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = ElecBorder,
            ),
        )
    }
}

@Composable
private fun Searching() {
    Column(
        Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = NeonCyan, strokeWidth = 3.dp, modifier = Modifier.size(34.dp))
        Spacer(Modifier.height(18.dp))
        Text(stringResource(R.string.remote_searching), style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(6.dp))
        Text(
            stringResource(R.string.remote_searching_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
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
