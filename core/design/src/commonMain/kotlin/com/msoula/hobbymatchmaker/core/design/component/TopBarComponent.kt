package com.msoula.hobbymatchmaker.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Announcement
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun HMMIosTopBar(
    title: String,
    onBack: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Padding for SafeArea
            .padding(top = 44.dp)
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 16.dp)
                .clickable {
                    if (onBack != null) {
                        onBack()
                    }
                }
        ) {
            if (onBack != null) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "back",
                    modifier = Modifier
                        .size(24.dp)
                        .minimumInteractiveComponentSize()
                        .border(2.dp, Color.Red)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun BoxScope.HMMHomeTopBar(onLogoutIconClick: () -> Unit, onRedirectIconClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .size(44.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                shape = CircleShape
            )
            .clickable(onClick = { onLogoutIconClick() })
            .semantics { role = Role.Button }
            .align(Alignment.TopEnd),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.Logout,
            contentDescription = "Logout",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }

    Box(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .size(44.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                shape = CircleShape
            )
            .clickable(onClick = { onRedirectIconClick() })
            .semantics { role = Role.Button }
            .align(Alignment.TopStart),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.Announcement,
            contentDescription = "Profile",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun BoxScope.HMMDetailTopBar(onNavigationIconClick: () -> Unit) {
    val isLightTheme = !isSystemInDarkTheme()
    val backgroundColor = if (isLightTheme) Color.Black.copy(alpha = 0.6f) else
        MaterialTheme.colorScheme.surface.copy(0.6f)
    val iconColor = if (isLightTheme) Color.White else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .padding(top = 8.dp, start = 8.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(color = backgroundColor)
            .clickable(onClick = onNavigationIconClick)
            .shadow(elevation = 4.dp, shape = CircleShape)
            .align(Alignment.TopStart)
            .zIndex(1f),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "Back",
            tint = iconColor
        )
    }
}
