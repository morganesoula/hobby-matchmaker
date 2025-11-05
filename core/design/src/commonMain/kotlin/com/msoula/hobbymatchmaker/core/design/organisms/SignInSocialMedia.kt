package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SignInSocialMedia(
    modifier: Modifier = Modifier,
    onGoogleClick: () -> Unit,
    onAppleClick: () -> Unit = {},
    onFacebookClick: () -> Unit = {},
    loading: Boolean = false
) {
    SocialMediaButtonsPlatformSpecific(
        modifier,
        onGoogleClick = onGoogleClick,
        onAppleClick = onAppleClick,
        onFacebookClick = onFacebookClick,
        loading = loading,
    )
}
