package com.meylon.salongallery.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.meylon.salongallery.net.PhotoFilter
import com.meylon.salongallery.net.PhotoFit
import com.meylon.salongallery.net.RssFeed
import com.meylon.salongallery.net.RssItem
import com.meylon.salongallery.net.ScreenSession
import com.meylon.salongallery.net.isVideoName
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.delay

/**
 * A full-screen, self-driving live preview of the Display — used by the in-app demo so the
 * whole flow (frames, effects, clock, news) can be seen without a second device.
 */
@Composable
fun DisplayPreview(session: ScreenSession, onClose: () -> Unit) {
    val context = LocalContext.current
    val libVer by session.libraryVersion.collectAsState()
    val frameId by session.frameId.collectAsState()
    val frameRandom by session.frameRandom.collectAsState()
    val framePool by session.framePool.collectAsState()
    val frameWidth by session.frameWidth.collectAsState()
    val fit by session.photoFit.collectAsState()
    val filter by session.photoFilter.collectAsState()
    val interval by session.intervalMs.collectAsState()
    val textOverlay by session.textOverlay.collectAsState()
    val clock by session.clock.collectAsState()
    val brightness by session.brightness.collectAsState()
    val rssOn by session.rssOn.collectAsState()
    val rssFeeds by session.rssFeeds.collectAsState()
    val rssConfig by session.rssConfig.collectAsState()

    val files = remember(libVer) { session.activeFiles() }

    var rssItems by remember { mutableStateOf<List<RssItem>>(emptyList()) }
    LaunchedEffect(rssOn, rssFeeds) {
        if (!rssOn || rssFeeds.isEmpty()) { rssItems = emptyList(); return@LaunchedEffect }
        while (true) { rssItems = RssFeed.fetch(rssFeeds); delay(5 * 60 * 1000L) }
    }

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            when {
                files.isNotEmpty() -> {
                    var idx by remember(files) { mutableIntStateOf(session.currentIndex.value.coerceIn(0, files.lastIndex)) }
                    LaunchedEffect(files, interval) {
                        while (files.size > 1) {
                            delay(interval)
                            idx = (idx + 1) % files.size
                            session.currentIndex.value = idx
                        }
                    }
                    val activeFrame = if (frameRandom && framePool.isNotEmpty())
                        framePool[kotlin.random.Random(idx.toLong()).nextInt(framePool.size)] else frameId
                    var adaptive by remember { mutableStateOf<Color?>(null) }
                    LaunchedEffect(activeFrame, idx, libVer) {
                        adaptive = if (frameById(activeFrame).adaptive) {
                            val f = files.getOrNull(idx)
                            if (f != null && !isVideoName(f.name)) kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { dominantColor(f) } else null
                        } else null
                    }
                    FramedContent(activeFrame, frameWidth, adaptive, Modifier.fillMaxSize()) {
                        AnimatedContent(idx, transitionSpec = { fadeIn(tween(700)) togetherWith fadeOut(tween(700)) }, label = "preview") { i ->
                            val f = files[i.coerceIn(0, files.lastIndex)]
                            if (isVideoName(f.name)) {
                                Box(Modifier.fillMaxSize().background(Color(0xFF14110C)), contentAlignment = Alignment.Center) {
                                    AsyncImage(model = f, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                                    Box(Modifier.size(64.dp).clip(RoundedCornerShape(50)).background(Color.Black.copy(alpha = 0.45f)), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Outlined.PlayArrow, null, tint = Color.White, modifier = Modifier.size(40.dp))
                                    }
                                }
                            } else {
                                PhotoContent(f, fit, session.transformFor(f.name), filter)
                            }
                        }
                    }
                }

                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Add photos to see them here", color = Color.White.copy(0.7f), textAlign = TextAlign.Center)
                }
            }

            OverlayLayer(textOverlay, clock)
            if (rssOn && rssItems.isNotEmpty()) RssTicker(rssItems, rssConfig)

            if (brightness in 0f..0.999f) {
                Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = (1f - brightness) * 0.82f)))
            }

            Box(
                Modifier.align(Alignment.TopEnd).padding(16.dp).size(44.dp).clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.4f)).border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClose() },
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Outlined.Close, "Close preview", tint = Color.White, modifier = Modifier.size(22.dp)) }
        }
    }
}
