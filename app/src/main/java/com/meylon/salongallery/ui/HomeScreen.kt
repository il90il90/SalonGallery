package com.meylon.salongallery.ui

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
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.SystemUpdateAlt
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.meylon.salongallery.R
import com.meylon.salongallery.data.DeviceRole
import com.meylon.salongallery.ui.components.BrandRow
import com.meylon.salongallery.ui.components.GradientButton
import com.meylon.salongallery.ui.components.LogoChip
import com.meylon.salongallery.ui.components.OutlineButton
import com.meylon.salongallery.ui.components.SalonBackground
import com.meylon.salongallery.ui.components.SectionLabel
import com.meylon.salongallery.ui.theme.ElecBorder
import com.meylon.salongallery.ui.theme.GoodGreen
import com.meylon.salongallery.ui.theme.NeonCyan
import com.meylon.salongallery.ui.theme.TextPrimary
import com.meylon.salongallery.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    role: DeviceRole,
    version: String,
    isChecking: Boolean,
    availableVersion: String?,
    onCheckUpdate: () -> Unit,
    onInstallUpdate: () -> Unit,
    onChangeRole: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isScreen = role == DeviceRole.SCREEN
    SalonBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 22.dp)
                .widthIn(max = 560.dp),
        ) {
            BrandRow(name = stringResource(R.string.app_name))

            Spacer(Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LogoChip(size = 88, icon = if (isScreen) Icons.Outlined.Tv else Icons.Outlined.Smartphone)
                Spacer(Modifier.height(20.dp))
                SectionLabel(stringResource(R.string.home_overline))
                Spacer(Modifier.height(10.dp))
                Text(
                    text = stringResource(
                        if (isScreen) R.string.home_screen_mode else R.string.home_remote_mode
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        if (isScreen) R.string.home_screen_hint else R.string.home_remote_hint
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(20.dp))
                VersionPill(version = version, availableVersion = availableVersion)
            }

            Spacer(Modifier.weight(1f))

            if (availableVersion != null) {
                GradientButton(
                    text = stringResource(R.string.update_now),
                    leading = Icons.Outlined.SystemUpdateAlt,
                    onClick = onInstallUpdate,
                )
                Spacer(Modifier.height(12.dp))
            }
            OutlineButton(
                text = stringResource(if (isChecking) R.string.checking_update else R.string.check_update),
                leading = Icons.Outlined.Refresh,
                enabled = !isChecking,
                onClick = onCheckUpdate,
            )
            Spacer(Modifier.height(12.dp))
            OutlineButton(
                text = stringResource(R.string.change_role),
                leading = Icons.Outlined.SwapHoriz,
                onClick = onChangeRole,
            )
        }
    }
}

@Composable
private fun VersionPill(version: String, availableVersion: String?) {
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
