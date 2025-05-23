package com.msoula.hobbymatchmaker.core.login.presentation.clients

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import dev.gitlive.firebase.auth.AuthCredential

interface FacebookUIClient {
    suspend fun getFacebookCredentials(): Pair<AuthCredential?, String?>
    fun registerCallback(
        onSuccess: (AuthCredential, String?) -> Unit,
        onError: (Exception) -> Unit
    )
    fun logIn()
}

class FacebookUIClientImpl(
    private val facebookUIClient: FacebookUIClient
) : SocialUIClient {

    override val providerType: ProviderType
        get() = ProviderType.FACEBOOK

    override suspend fun getCredential(): AuthCredential? {
        return facebookUIClient.getFacebookCredentials().first
    }
}
