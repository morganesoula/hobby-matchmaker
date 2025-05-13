package com.msoula.hobbymatchmaker.core.login.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import dev.gitlive.firebase.auth.AuthCredential

@Composable
expect fun SocialMediaButtonListPlatformSpecificUI(
    modifier: Modifier,
    signInState: SignInEvent,
    onFacebookButtonClicked: ((credential: AuthCredential) -> Unit)? = null,
    onAppleButtonClicked: (() -> Unit)? = null,
    onGoogleButtonClicked: () -> Unit,
    facebookUIClient: FacebookUIClient?
)
