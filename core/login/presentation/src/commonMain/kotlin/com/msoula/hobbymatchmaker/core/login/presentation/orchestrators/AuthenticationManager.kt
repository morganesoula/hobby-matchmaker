package com.msoula.hobbymatchmaker.core.login.presentation.orchestrators

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.SignInWithSocialProviderUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.flatMapSuspend
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.CreateDefaultUserProfileUseCase

class AuthenticationManager(
    private val createDefaultUserProfileUseCase: CreateDefaultUserProfileUseCase,
    private val signInUseCase: SignInUseCase,
    private val signInWithSocialProviderUseCase: SignInWithSocialProviderUseCase,
    private val setIsConnectedUseCase: SetIsConnectedUseCase,
    private val sessionManager: SessionManager
) {
    sealed interface Params {
        data class EmailPassword(val email: String, val password: String) : Params
        data class SocialProvider(
            val providerType: ProviderType,
            val credentialProvider: suspend () -> Any?
        ) : Params
    }

    suspend operator fun invoke(params: Params): AppResult<SignInSuccess, AppError> {
        val resultUid: AppResult<String, AppError> = when (params) {
            is Params.EmailPassword ->
                signInUseCase(Parameters.DoubleStringParam(params.email, params.password))
                    .mapSuccess { it.uid }

            is Params.SocialProvider ->
                signInWithSocialProviderUseCase(
                    providerType = params.providerType,
                    credentialProvider = params.credentialProvider
                ).mapSuccess { user -> user?.uid.orEmpty() }
        }

        val uid = when (resultUid) {
            is AppResult.Success -> resultUid.data
            is AppResult.Failure -> return AppResult.Failure(resultUid.error)
        }

        return setIsConnectedUseCase(true)
            .flatMapSuspend {
                sessionManager.setCurrentUserProfileUuid(uid)
            }
            .flatMapSuspend {
                createDefaultUserProfileUseCase(uid, "")
                    .mapSuccess { SignInSuccess(uid = uid) }
            }
    }
}
