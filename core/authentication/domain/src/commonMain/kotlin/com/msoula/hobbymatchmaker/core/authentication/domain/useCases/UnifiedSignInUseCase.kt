package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.toSignInError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.HMMAppError
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import dev.gitlive.firebase.auth.AuthCredential
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class UnifiedSignInUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val signInUseCase: SignInUseCase,
    private val signInWithCredentialUseCase: SignInWithCredentialUseCase,
    private val setIsConnectedUseCase: SetIsConnectedUseCase
) {
    sealed interface Params {
        data class EmailPassword(val email: String, val password: String) : Params
        data class SocialMedia(val credential: AuthCredential, val providerType: ProviderType) :
            Params
    }

    fun signIn(params: Params) =
        when (params) {
            is Params.EmailPassword ->
                signInUseCase(Parameters.DoubleStringParam(params.email, params.password))

            is Params.SocialMedia ->
                socialMediaSignIn(params.credential, params.providerType)
        }

    private fun socialMediaSignIn(credential: AuthCredential, providerType: ProviderType) =
        authenticationAction {
            signInWithCredentialUseCase(credential, providerType)
        }

    private fun <Error : HMMAppError> authenticationAction(
        call: suspend () -> Result<*, Error>
    ) = channelFlow<Result<SignInSuccess, SignInErrorHMM>> {
        send(Result.Loading)
        when (val result = call()) {
            is Result.Success -> {
                setIsConnectedUseCase(true)
                send(Result.Success(SignInSuccess))
            }

            is Result.Failure ->
                send(Result.Failure(result.error.toSignInError()))

            else -> Unit
        }
    }.flowOn(dispatcher)
}
