package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun SocialMediaButtonsPlatformSpecific(
    modifier: Modifier = Modifier,
    onGoogleClick: () -> Unit,
    onAppleClick: () -> Unit = {},
    onFacebookClick: () -> Unit = {},
    loading: Boolean
)
