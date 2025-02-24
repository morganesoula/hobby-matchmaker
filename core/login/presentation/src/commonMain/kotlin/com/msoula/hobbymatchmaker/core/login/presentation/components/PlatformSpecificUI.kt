package com.msoula.hobbymatchmaker.core.login.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun SocialMediaButtonListPlatformSpecificUI(
    modifier: Modifier,
    onFacebookButtonClicked: (() -> Unit)? = null,
    onAppleButtonClicked: (() -> Unit)? = null,
    onGoogleButtonClicked: () -> Unit
)
