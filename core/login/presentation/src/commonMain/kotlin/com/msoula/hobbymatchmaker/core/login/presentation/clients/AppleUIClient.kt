package com.msoula.hobbymatchmaker.core.login.presentation.clients

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import dev.gitlive.firebase.auth.AuthCredential

interface AppleUIClient {
    suspend fun getAppleCredentials(): AppResult<AuthCredential?, AppError>
}

class AppleUIClientImpl(
    private val appleUIClient: AppleUIClient
) : SocialUIClient {

    override val providerType: ProviderType
        get() = ProviderType.APPLE

    override suspend fun getCredential(): AppResult<AuthCredential?, AppError> {
        return appleUIClient.getAppleCredentials()
    }
}
