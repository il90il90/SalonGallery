package com.meylon.salongallery.ui

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.meylon.salongallery.R
import com.meylon.salongallery.data.DeviceRole
import com.meylon.salongallery.ui.components.AccentGradient
import com.meylon.salongallery.ui.components.BrandRow
import com.meylon.salongallery.ui.components.GradientButton
import com.meylon.salongallery.ui.components.SalonBackground
import com.meylon.salongallery.ui.components.SectionLabel
import com.meylon.salongallery.ui.theme.ElecBorder
import com.meylon.salongallery.ui.theme.ElecSurface
import com.meylon.salongallery.ui.theme.NeonBlue
import com.meylon.salongallery.ui.theme.NeonCyan
import com.meylon.salongallery.ui.theme.NeonTeal
import com.meylon.salongallery.ui.theme.NeonViolet
import com.meylon.salongallery.ui.theme.TextPrimary
import com.meylon.salongallery.ui.theme.TextSecondary

@Composable
fun RoleSelectionScreen(
    onRoleChosen: (DeviceRole) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selected by remember { mutableStateOf<DeviceRole?>(null) }

    SalonBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 22.dp)
                .widthIn(max = 560.dp),
        ) {
            BrandRow(name = stringResource(R.string.app_name))

            Spacer(Modifier.height(40.dp))
            SectionLabel(stringResource(R.string.role_overline))
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.role_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.role_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )

            Spacer(Modifier.height(28.dp))
            RoleCard(
                icon = Icons.Outlined.Tv,
                title = stringResource(R.string.role_screen_title),
                desc = stringResource(R.string.role_screen_desc),
                selected = selected == DeviceRole.SCREEN,
                onClick = { selected = DeviceRole.SCREEN },
            )
            Spacer(Modifier.height(14.dp))
            RoleCard(
                icon = Icons.Outlined.Smartphone,
                title = stringResource(R.string.role_remote_title),
                desc = stringResource(R.string.role_remote_desc),
                selected = selected == DeviceRole.REMOTE,
                onClick = { selected = DeviceRole.REMOTE },
            )

            Spacer(Modifier.height(24.dp))
            Spacer(Modifier.weight(1f))
            GradientButton(
                text = stringResource(R.string.role_continue),
                enabled = selected != null,
                onClick = { selected?.let(onRoleChosen) },
            )
        }
    }
}

@Composable
private fun RoleCard(
    icon: ImageVector,
    title: String,
    desc: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val outerShape = RoundedCornerShape(20.dp)
    val innerShape = RoundedCornerShape(18.dp)
    val elev by animateDpAsState(if (selected) 18.dp else 0.dp, label = "elev")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (selected) Modifier.shadow(elev, outerShape, spotColor = NeonBlue, ambientColor = NeonViolet)
                else Modifier
            )
            .clip(outerShape)
            .then(
                if (selected) Modifier.background(AccentGradient)
                else Modifier.background(ElecBorder)
            )
            .padding(if (selected) 1.5.dp else 1.dp)
            .clip(innerShape)
            .background(ElecSurface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconChip(icon = icon, selected = selected)
            Spacer(Modifier.size(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
            Spacer(Modifier.size(10.dp))
            SelectionDot(selected = selected)
        }
    }
}

@Composable
private fun IconChip(icon: ImageVector, selected: Boolean) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(shape)
            .then(
                if (selected) Modifier.background(AccentGradient)
                else Modifier.background(NeonCyan.copy(alpha = 0.10f))
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (selected) Color.White else NeonTeal,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun SelectionDot(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .then(
                if (selected) Modifier.background(AccentGradient)
                else Modifier.border(1.5.dp, ElecBorder, CircleShape)
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(15.dp))
        }
    }
}
