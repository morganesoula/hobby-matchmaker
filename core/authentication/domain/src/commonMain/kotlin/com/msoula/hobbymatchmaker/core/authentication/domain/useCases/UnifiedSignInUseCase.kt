package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.flatMapSuspend
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase

class UnifiedSignInUseCase(
    private val signInUseCase: SignInUseCase,
    private val signInWithSocialProviderUseCase: SignInWithSocialProviderUseCase,
    private val setIsConnectedUseCase: SetIsConnectedUseCase
) {
    sealed interface Params {
        data class EmailPassword(val email: String, val password: String) : Params
        data class SocialProvider(
            val providerType: ProviderType,
            val credentialProvider: suspend () -> Any?
        ) :
            Params
    }

    suspend operator fun invoke(params: Params): AppResult<SignInSuccess, AppError> =
        when (params) {
            is Params.EmailPassword ->
                signInUseCase(Parameters.DoubleStringParam(params.email, params.password))

            is Params.SocialProvider ->
                signInWithSocialProviderUseCase(
                    providerType = params.providerType,
                    credentialProvider = params.credentialProvider
                ).mapSuccess { user -> SignInSuccess(uid = user?.uid ?: "") }
        }
            .flatMapSuspend { success ->
                setIsConnectedUseCase(true).mapSuccess { success }
            }
}
