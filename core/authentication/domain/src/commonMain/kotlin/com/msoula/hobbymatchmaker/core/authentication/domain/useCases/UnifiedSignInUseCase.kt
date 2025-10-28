package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.flatMapSuspend
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import dev.gitlive.firebase.auth.AuthCredential

class UnifiedSignInUseCase(
    private val signInUseCase: SignInUseCase,
    private val signInWithCredentialUseCase: SignInWithCredentialUseCase,
    private val setIsConnectedUseCase: SetIsConnectedUseCase
) {
    sealed interface Params {
        data class EmailPassword(val email: String, val password: String) : Params
        data class SocialMedia(val credential: AuthCredential, val providerType: ProviderType) :
            Params
    }

    suspend operator fun invoke(params: Params): AppResult<SignInSuccess, AppError> =
        when (params) {
            is Params.EmailPassword ->
                signInUseCase(Parameters.DoubleStringParam(params.email, params.password))

            is Params.SocialMedia ->
                signInWithCredentialUseCase(params.credential, params.providerType)
                    .mapSuccess { info -> SignInSuccess(uid = info.uid ?: "") }
        }
            .flatMapSuspend { success ->
                setIsConnectedUseCase(true).mapSuccess { success }
            }
}
