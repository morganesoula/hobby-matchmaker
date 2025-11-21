package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.flatMapSuspend
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.CreateDefaultUserProfileUseCase

class UnifiedSignInUseCase(
    private val createDefaultUserProfileUseCase: CreateDefaultUserProfileUseCase,
    private val signInUseCase: SignInUseCase,
    private val signInWithSocialProviderUseCase: SignInWithSocialProviderUseCase,
    private val setIsConnectedUseCase: SetIsConnectedUseCase
) {
    var userUid: String? = null

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
                    .mapSuccess {
                        userUid = it.uid
                    }

            is Params.SocialProvider ->
                signInWithSocialProviderUseCase(
                    providerType = params.providerType,
                    credentialProvider = params.credentialProvider
                ).mapSuccess { user ->
                    userUid = user?.uid
                    Logger.d("UserUid at the end of signInWithSocialProvider is: $userUid")
                }
        }
            .flatMapSuspend { setIsConnectedUseCase(true) }
            .flatMapSuspend {
                Logger.d("UserUid right before creating default profile is: $userUid")
                createDefaultUserProfileUseCase(
                    uid = userUid ?: "",
                    name = ""
                ).mapSuccess {
                    Logger.d("Final flatMap userUid is: $userUid")
                    SignInSuccess(uid = userUid ?: "")
                }
            }

}
