package com.il90.salongallery

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.il90.salongallery.data.DeviceRole
import com.il90.salongallery.data.RolePreferences
import com.il90.salongallery.ui.AppActions
import com.il90.salongallery.ui.UpdateDialog
import com.il90.salongallery.ui.RemoteModeScreen
import com.il90.salongallery.ui.RoleSelectionScreen
import com.il90.salongallery.ui.ScreenModeScreen
import com.il90.salongallery.ui.theme.SalonGalleryTheme
import com.il90.salongallery.update.UpdateManager
import com.il90.salongallery.update.UpdateStatus
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Dark gallery theme: keep the system bars transparent with light icons.
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = androidx.activity.SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        val prefs = RolePreferences(applicationContext)
        setContent {
            SalonGalleryTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(prefs)
                }
            }
        }
    }
}

/** Shows the previous run's crash report (stack trace + memory snapshot) in a scrollable dialog. */
@Composable
private fun CrashReportDialog(report: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.crash_title)) },
        text = {
            Column(
                Modifier
                    .heightIn(max = 360.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(stringResource(R.string.crash_explain))
                Spacer(Modifier.height(12.dp))
                Text(
                    report,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.crash_dismiss)) }
        },
    )
}

@Composable
private fun AppRoot(prefs: RolePreferences) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val role by prefs.role.collectAsStateWithLifecycle(initialValue = DeviceRole.UNSET)

    // So the Display's waiting screen can show the Wi-Fi network name: Android hides the SSID
    // without location permission. Best-effort — if denied, the screen simply omits the name.
    val locationPermission = rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(role) {
        if (role == DeviceRole.SCREEN &&
            androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            runCatching { locationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION) }
        }
    }

    // Keep a foreground service alive while this device is a Display, so the gallery
    // keeps running (and stays discoverable) when the app is minimized.
    LaunchedEffect(role) {
        val intent = android.content.Intent(context, DisplayService::class.java)
        if (role == DeviceRole.SCREEN) {
            androidx.core.content.ContextCompat.startForegroundService(context, intent)
        } else {
            context.stopService(intent)
            com.il90.salongallery.net.ScreenSessionHolder.stopAll()
        }
    }

    var isChecking by remember { mutableStateOf(false) }
    var availableVersion by remember { mutableStateOf<String?>(null) }
    var apkUrl by remember { mutableStateOf<String?>(null) }
    var apkNotes by remember { mutableStateOf("") }
    var updateDismissed by remember { mutableStateOf(false) }

    fun runCheck(showToast: Boolean, force: Boolean = true) {
        if (isChecking) return
        isChecking = true
        scope.launch {
            when (val status = UpdateManager.checkForUpdate(force = force)) {
                is UpdateStatus.Available -> {
                    availableVersion = status.latest
                    apkUrl = status.apkUrl
                    apkNotes = status.notes
                    if (showToast) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.update_available, status.latest),
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
                is UpdateStatus.UpToDate -> {
                    availableVersion = null
                    apkUrl = null
                    if (showToast) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.up_to_date, status.current),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
                is UpdateStatus.Error -> {
                    if (showToast) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.update_check_failed) + " (" + status.message + ")",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
            }
            isChecking = false
        }
    }

    // Silent, throttled auto-check when the app comes to the foreground.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            // Check both on entry (so the prompt is fresh when the app opens) and on exit
            // (so the next launch already knows about a new build).
            if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_STOP) {
                runCheck(showToast = false, force = false)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    when (role) {
        DeviceRole.UNSET -> RoleSelectionScreen(
            onRoleChosen = { chosen -> scope.launch { prefs.setRole(chosen) } }
        )
        else -> {
            val actions = AppActions(
                version = UpdateManager.currentVersion,
                isChecking = isChecking,
                availableVersion = availableVersion,
                onCheckUpdate = { runCheck(showToast = true) },
                onInstallUpdate = {
                    apkUrl?.let { url ->
                        Toast.makeText(context, context.getString(R.string.downloading_update), Toast.LENGTH_SHORT).show()
                        scope.launch {
                            val r = UpdateManager.downloadAndInstall(context, url)
                            if (r.isFailure) Toast.makeText(
                                context,
                                context.getString(R.string.update_download_failed) + " (" + (r.exceptionOrNull()?.message ?: "error") + ")",
                                Toast.LENGTH_LONG,
                            ).show()
                        }
                    }
                },
                onChangeRole = { scope.launch { prefs.setRole(DeviceRole.UNSET) } },
            )
            if (role == DeviceRole.SCREEN) {
                ScreenModeScreen(actions)
            } else {
                RemoteModeScreen(actions)
            }
        }
    }

    // If the previous run crashed, show the saved report on this launch so an unattended screen's
    // failure is visible. Dismissing clears it.
    var crashReport by remember { mutableStateOf(com.il90.salongallery.diag.CrashLog.pending(context)) }
    crashReport?.let { report ->
        CrashReportDialog(report) {
            com.il90.salongallery.diag.CrashLog.clear(context)
            crashReport = null
        }
    }

    // On launch, if a newer version is available, offer to update right away.
    if (availableVersion != null && !updateDismissed) {
        UpdateDialog(
            version = availableVersion!!,
            notes = apkNotes,
            onUpdate = {
                updateDismissed = true
                apkUrl?.let { url ->
                    Toast.makeText(context, context.getString(R.string.downloading_update), Toast.LENGTH_SHORT).show()
                    scope.launch {
                        val r = UpdateManager.downloadAndInstall(context, url)
                        if (r.isFailure) Toast.makeText(
                            context,
                            context.getString(R.string.update_download_failed) + " (" + (r.exceptionOrNull()?.message ?: "error") + ")",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
            },
            onLater = { updateDismissed = true },
        )
    }
}
