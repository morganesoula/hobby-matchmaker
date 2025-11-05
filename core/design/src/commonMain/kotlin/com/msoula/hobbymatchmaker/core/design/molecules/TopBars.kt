package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Announcement
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTopBar(
    modifier: Modifier = Modifier,
    redirectToProfile: () -> Unit,
    redirectToSignIn: () -> Unit
) {
    TopAppBar(
        title = {},
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent
        ),
        actions = {
            Row {
                IconButton(
                    onClick = { redirectToProfile() },
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Announcement,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )

                IconButton(
                    onClick = { redirectToSignIn() },
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackNavigationTopBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {},
        modifier = modifier.statusBarsPadding(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        windowInsets = WindowInsets(0),
        navigationIcon = {
            IconButton(
                onClick = { onBack() },
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    )
}
