package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import dev.gitlive.firebase.auth.AuthCredential

interface SocialUIClient {
    val providerType: ProviderType
    suspend fun getCredential(): AuthCredential?
}
