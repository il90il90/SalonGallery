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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meylon.salongallery.R
import com.meylon.salongallery.net.DiscoveredScreen
import com.meylon.salongallery.net.PhotoSender
import com.meylon.salongallery.net.RemoteSession
import com.meylon.salongallery.ui.components.AccentGradient
import com.meylon.salongallery.ui.components.GradientButton
import com.meylon.salongallery.ui.components.OutlineButton
import com.meylon.salongallery.ui.components.SalonBackground
import com.meylon.salongallery.ui.components.SectionLabel
import com.meylon.salongallery.ui.theme.ElecBorder
import com.meylon.salongallery.ui.theme.ElecSurface
import com.meylon.salongallery.ui.theme.GoodGreen
import com.meylon.salongallery.ui.theme.NeonCyan
import com.meylon.salongallery.ui.theme.NeonTeal
import com.meylon.salongallery.ui.theme.TextPrimary
import com.meylon.salongallery.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private enum class SendState { IDLE, SENDING, SENT, FAILED }

@Composable
fun RemoteModeScreen(actions: AppActions) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val session = remember { RemoteSession(context) }

    DisposableEffect(Unit) {
        session.start()
        onDispose { session.stop() }
    }

    val screens by session.screens.collectAsStateWithLifecycle()
    var selected by remember { mutableStateOf<DiscoveredScreen?>(null) }
    var sendState by remember { mutableStateOf(SendState.IDLE) }
    var showSettings by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val target = selected
        if (uri != null && target != null) {
            sendState = SendState.SENDING
            scope.launch {
                val bytes = runCatching {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }.getOrNull()
                sendState = if (bytes != null && PhotoSender.sendPhoto(target.host, target.port, bytes)) {
                    SendState.SENT
                } else {
                    SendState.FAILED
                }
            }
        }
    }

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
            SectionLabel(stringResource(R.string.remote_overline))
            Spacer(Modifier.height(10.dp))
            Text(
                stringResource(R.string.remote_screens_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
            )
            Spacer(Modifier.height(24.dp))

            val target = selected
            if (target == null) {
                if (screens.isEmpty()) {
                    Searching()
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        screens.forEach { s ->
                            ScreenRow(screen = s, onClick = { selected = s; sendState = SendState.IDLE })
                        }
                    }
                }
            } else {
                ConnectedPanel(
                    screen = target,
                    sendState = sendState,
                    onSend = {
                        sendState = SendState.IDLE
                        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onBack = { selected = null; sendState = SendState.IDLE },
                )
            }
        }
    }

    if (showSettings) {
        SettingsSheet(actions = actions, onDismiss = { showSettings = false })
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
        Text(
            stringResource(R.string.remote_searching),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
        )
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
        ) {
            Icon(Icons.Outlined.Tv, null, tint = NeonTeal, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.size(14.dp))
        Column(Modifier.weight(1f)) {
            Text(screen.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Text(screen.host, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

@Composable
private fun ConnectedPanel(
    screen: DiscoveredScreen,
    sendState: SendState,
    onSend: () -> Unit,
    onBack: () -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
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
            ) {
                Icon(Icons.Outlined.Tv, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.size(14.dp))
            Column(Modifier.weight(1f)) {
                Text(screen.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Text(stringResource(R.string.remote_connected), style = MaterialTheme.typography.bodyMedium, color = GoodGreen)
            }
        }

        Spacer(Modifier.height(20.dp))
        GradientButton(
            text = if (sendState == SendState.SENDING) stringResource(R.string.remote_sending)
            else stringResource(R.string.remote_send_photo),
            leading = Icons.Outlined.PhotoLibrary,
            enabled = sendState != SendState.SENDING,
            onClick = onSend,
        )

        if (sendState == SendState.SENT || sendState == SendState.FAILED) {
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(if (sendState == SendState.SENT) R.string.remote_sent else R.string.remote_send_failed),
                style = MaterialTheme.typography.labelLarge,
                color = if (sendState == SendState.SENT) GoodGreen else Color(0xFFF87171),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(12.dp))
        OutlineButton(
            text = stringResource(R.string.remote_disconnect),
            onClick = onBack,
        )
    }
}
