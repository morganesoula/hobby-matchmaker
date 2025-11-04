package com.msoula.hobbymatchmaker.core.login.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent

@Composable
expect fun SocialMediaButtonListPlatformSpecificUI(
    modifier: Modifier,
    onEvent: (AuthenticationUIEvent) -> Unit,
    facebookUIClient: FacebookUIClient?
)
