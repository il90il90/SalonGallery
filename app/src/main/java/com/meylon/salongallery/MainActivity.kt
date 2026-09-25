package com.meylon.salongallery

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meylon.salongallery.data.DeviceRole
import com.meylon.salongallery.data.RolePreferences
import com.meylon.salongallery.ui.HomeScreen
import com.meylon.salongallery.ui.RoleSelectionScreen
import com.meylon.salongallery.ui.theme.SalonGalleryTheme
import com.meylon.salongallery.update.UpdateManager
import com.meylon.salongallery.update.UpdateStatus
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

@Composable
private fun AppRoot(prefs: RolePreferences) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val role by prefs.role.collectAsStateWithLifecycle(initialValue = DeviceRole.UNSET)

    var isChecking by remember { mutableStateOf(false) }
    var availableVersion by remember { mutableStateOf<String?>(null) }
    var apkUrl by remember { mutableStateOf<String?>(null) }

    fun runCheck(showToast: Boolean) {
        if (isChecking) return
        isChecking = true
        scope.launch {
            when (val status = UpdateManager.checkForUpdate()) {
                is UpdateStatus.Available -> {
                    availableVersion = status.latest
                    apkUrl = status.apkUrl
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
                            context.getString(R.string.update_check_failed),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
            isChecking = false
        }
    }

    // Show the installed version as a toast when the app opens.
    LaunchedEffect(Unit) {
        Toast.makeText(
            context,
            context.getString(R.string.installed_version, UpdateManager.currentVersion),
            Toast.LENGTH_SHORT,
        ).show()
    }

    // Check for updates on open (ON_START) and on close (ON_STOP).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> runCheck(showToast = false)
                Lifecycle.Event.ON_STOP -> runCheck(showToast = false)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    when (role) {
        DeviceRole.UNSET -> RoleSelectionScreen(
            onRoleChosen = { chosen -> scope.launch { prefs.setRole(chosen) } }
        )
        else -> HomeScreen(
            role = role,
            version = UpdateManager.currentVersion,
            isChecking = isChecking,
            availableVersion = availableVersion,
            onCheckUpdate = { runCheck(showToast = true) },
            onInstallUpdate = {
                apkUrl?.let { url ->
                    Toast.makeText(
                        context,
                        context.getString(R.string.downloading_update),
                        Toast.LENGTH_SHORT,
                    ).show()
                    scope.launch { UpdateManager.downloadAndInstall(context, url) }
                }
            },
            onChangeRole = { scope.launch { prefs.setRole(DeviceRole.UNSET) } },
        )
    }
}
