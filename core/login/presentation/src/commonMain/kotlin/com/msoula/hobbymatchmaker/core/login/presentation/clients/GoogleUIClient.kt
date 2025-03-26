package com.msoula.hobbymatchmaker.core.login.presentation.clients

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import dev.gitlive.firebase.auth.AuthCredential

interface GoogleUIClient {
    suspend fun getGoogleCredentials(): AuthCredential?
}

class GoogleUIClientImpl(
    private val googleUIClient: GoogleUIClient
) : SocialUIClient {

    override val providerType: ProviderType
        get() = ProviderType.GOOGLE

    override suspend fun getCredential(): AuthCredential? {
        return googleUIClient.getGoogleCredentials()
    }
}
