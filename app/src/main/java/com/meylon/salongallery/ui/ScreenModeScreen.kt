package com.meylon.salongallery.ui

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.meylon.salongallery.R
import com.meylon.salongallery.net.ScreenSession
import com.meylon.salongallery.ui.components.LogoChip
import com.meylon.salongallery.ui.components.SalonBackground
import com.meylon.salongallery.ui.components.SectionLabel
import com.meylon.salongallery.ui.theme.ElecBorder
import com.meylon.salongallery.ui.theme.ElecSurface
import com.meylon.salongallery.ui.theme.GoodGreen
import com.meylon.salongallery.ui.theme.NeonCyan
import com.meylon.salongallery.ui.theme.TextPrimary
import com.meylon.salongallery.ui.theme.TextSecondary

@Composable
fun ScreenModeScreen(actions: AppActions) {
    val context = LocalContext.current
    val deviceName = remember { Build.MODEL ?: "Salon Screen" }
    val session = remember { ScreenSession(context, deviceName, actions.version) }

    DisposableEffect(Unit) {
        session.start()
        onDispose { session.stop() }
    }

    val photoVersion by session.photoVersion.collectAsStateWithLifecycle()
    val running by session.running.collectAsStateWithLifecycle()
    var showSettings by remember { mutableStateOf(false) }
    val hasPhoto = photoVersion > 0 && session.photoFile.exists()

    SalonBackground {
        Column(
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            TopBar(onSettings = { showSettings = true })
            Spacer(Modifier.height(14.dp))

            if (hasPhoto) {
                val request = ImageRequest.Builder(context)
                    .data(session.photoFile)
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .setParameter("v", photoVersion, memoryCacheKey = photoVersion.toString())
                    .build()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(androidx.compose.ui.graphics.Color.Black)
                        .border(1.dp, ElecBorder, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = request,
                        contentDescription = "Displayed photo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(6.dp),
                    )
                }
            } else {
                WaitingToPair(deviceName = deviceName, running = running)
            }
        }
    }

    if (showSettings) {
        SettingsSheet(actions = actions, onDismiss = { showSettings = false })
    }
}

@Composable
private fun WaitingToPair(deviceName: String, running: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize().widthIn(max = 520.dp),
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
            color = TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        // The screen's own name, so it's identifiable on the remote.
        Text(
            deviceName,
            style = MaterialTheme.typography.titleLarge,
            color = NeonCyan,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.screen_ready_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(20.dp))
        StatusPill(running = running)
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun StatusPill(running: Boolean) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, ElecBorder, RoundedCornerShape(50))
            .background(ElecSurface)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(if (running) GoodGreen else TextSecondary))
        Text(
            stringResource(if (running) R.string.screen_advertising else R.string.screen_starting),
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
        )
    }
}
