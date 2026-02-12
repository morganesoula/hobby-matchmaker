package com.msoula.hobbymatchmaker.core.login.presentation.clients

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import dev.gitlive.firebase.auth.AuthCredential

interface FacebookUIClient {
    fun registerCallback(
        onSuccess: (AuthCredential, String?) -> Unit,
        onError: (Exception) -> Unit
    )

    fun logIn()
    fun hasValidToken(): Boolean
}

class FacebookUIClientImpl(
    val facebookUIClient: FacebookUIClient
) : SocialUIClient {

    override val providerType: ProviderType
        get() = ProviderType.FACEBOOK

    override suspend fun getCredential(): AppResult<AuthCredential?, AppError> =
        AppResult.Success(null)
}
