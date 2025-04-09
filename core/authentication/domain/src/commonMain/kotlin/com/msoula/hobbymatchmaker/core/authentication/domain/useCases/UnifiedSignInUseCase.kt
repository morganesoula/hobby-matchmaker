package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import dev.gitlive.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

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

    fun signIn(params: Params): Flow<Result<SignInSuccess, SignInError>> = when (params) {
        is Params.EmailPassword -> channelFlow {
            send(Result.Loading)

            signInUseCase(
                Parameters.DoubleStringParam(params.email, params.password)
            ).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        setIsConnectedUseCase(true)
                        send(Result.Success(SignInSuccess))
                    }

                    is Result.Failure -> send(Result.Failure(result.error))
                    else -> Unit
                }
            }
        }

        is Params.SocialMedia -> channelFlow {
            send(Result.Loading)

            when (val result = signInWithCredentialUseCase(
                params.credential, params.providerType
            )) {

                is Result.Success -> {
                    setIsConnectedUseCase(true)
                    send(Result.Success(SignInSuccess))
                }

                is Result.Failure -> send(Result.Failure(SignInError.Other(result.error.message)))
                else -> Unit
            }
        }
    }
}
