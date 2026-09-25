package com.meylon.salongallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.SystemUpdateAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.meylon.salongallery.R
import com.meylon.salongallery.ui.components.BrandRow
import com.meylon.salongallery.ui.components.GradientButton
import com.meylon.salongallery.ui.components.OutlineButton
import com.meylon.salongallery.ui.theme.ElecBg
import com.meylon.salongallery.ui.theme.ElecBorder
import com.meylon.salongallery.ui.theme.GoodGreen
import com.meylon.salongallery.ui.theme.NeonCyan
import com.meylon.salongallery.ui.theme.TextPrimary
import com.meylon.salongallery.ui.theme.TextSecondary

/** App-wide actions (updates + role) surfaced from the settings sheet. */
data class AppActions(
    val version: String,
    val isChecking: Boolean,
    val availableVersion: String?,
    val onCheckUpdate: () -> Unit,
    val onInstallUpdate: () -> Unit,
    val onChangeRole: () -> Unit,
)

/** Brand on the left, a settings gear on the right. */
@Composable
fun TopBar(onSettings: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BrandRow(name = stringResource(R.string.app_name))
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(1.dp, ElecBorder, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onSettings() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = TextPrimary, modifier = Modifier.size(22.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(actions: AppActions, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ElecBg,
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(
                stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
            )
            Spacer(Modifier.height(16.dp))
            VersionPill(version = actions.version, availableVersion = actions.availableVersion)
            Spacer(Modifier.height(20.dp))
            if (actions.availableVersion != null) {
                GradientButton(
                    text = stringResource(R.string.update_now),
                    leading = Icons.Outlined.SystemUpdateAlt,
                    onClick = actions.onInstallUpdate,
                )
                Spacer(Modifier.height(12.dp))
            }
            OutlineButton(
                text = stringResource(if (actions.isChecking) R.string.checking_update else R.string.check_update),
                leading = Icons.Outlined.Refresh,
                enabled = !actions.isChecking,
                onClick = actions.onCheckUpdate,
            )
            Spacer(Modifier.height(12.dp))
            OutlineButton(
                text = stringResource(R.string.change_role),
                leading = Icons.Outlined.SwapHoriz,
                onClick = actions.onChangeRole,
            )
        }
    }
}

@Composable
fun VersionPill(version: String, availableVersion: String?) {
    val text = if (availableVersion != null) {
        stringResource(R.string.update_available, availableVersion)
    } else {
        stringResource(R.string.up_to_date, version)
    }
    val dot = if (availableVersion != null) NeonCyan else GoodGreen
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, ElecBorder, RoundedCornerShape(50))
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(dot))
        Text(text, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
    }
}
