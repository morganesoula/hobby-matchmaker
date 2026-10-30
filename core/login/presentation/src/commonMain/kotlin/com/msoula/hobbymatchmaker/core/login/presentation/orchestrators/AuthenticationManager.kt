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
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.HydrateUserProfileUseCase

class AuthenticationManager(
    private val hydrateUserProfileUseCase: HydrateUserProfileUseCase,
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
        val resultUser: AppResult<Pair<String, String>, AppError> = when (params) {
            is Params.EmailPassword ->
                signInUseCase(Parameters.DoubleStringParam(params.email, params.password))
                    .mapSuccess { it.uid to "" }

            is Params.SocialProvider ->
                signInWithSocialProviderUseCase(
                    providerType = params.providerType,
                    credentialProvider = params.credentialProvider
                ).mapSuccess { user -> user?.uid.orEmpty() to user?.displayName.orEmpty().substringBefore(" ") }
        }

        val (uid, displayName) = when (resultUser) {
            is AppResult.Success -> resultUser.data
            is AppResult.Failure -> return AppResult.Failure(resultUser.error)
        }

        return setIsConnectedUseCase(true)
            .flatMapSuspend {
                sessionManager.setCurrentUserProfileUuid(uid)
            }
            .flatMapSuspend {
                // A failed remote lookup must not block sign-in: the splash re-checks on next launch
                when (val hydration = hydrateUserProfileUseCase(uid, displayName)) {
                    is AppResult.Success -> AppResult.Success(SignInSuccess(uid, needsPseudo = !hydration.data))
                    is AppResult.Failure -> AppResult.Success(SignInSuccess(uid))
                }
            }
    }
}
