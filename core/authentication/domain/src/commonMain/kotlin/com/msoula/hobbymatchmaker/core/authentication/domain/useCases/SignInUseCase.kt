package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.HMMAppError
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class SignInUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val authenticationRepository: AuthenticationRepository,
    private val setIsConnectedUseCase: SetIsConnectedUseCase
) : FlowUseCase<Parameters.DoubleStringParam, SignInSuccess, SignInErrorHMM>(dispatcher) {

    override fun execute(parameters: Parameters.DoubleStringParam): Flow<Result<SignInSuccess, SignInErrorHMM>> {
        return channelFlow {
            send(Result.Loading)

            when (val result = authenticationRepository.signInWithEmailAndPassword(
                parameters.firstValue,
                parameters.secondValue
            )) {
                is Result.Success -> {
                    setIsConnectedUseCase(true)
                    send(Result.Success(SignInSuccess))
                }

                is Result.Failure -> send(
                    Result.Failure(
                        mapSignInError(result.error)
                    )
                )


                else -> Unit
            }
        }.flowOn(dispatcher)
    }
}

data object SignInSuccess
sealed class SignInErrorHMM(override val message: String) : HMMAppError {
    data object WrongPassword : SignInErrorHMM("")
    data object UserNotFound : SignInErrorHMM("")
    data object UserDisabled : SignInErrorHMM("")
    data object TooManyRequests : SignInErrorHMM("")
    data class AccountAlreadyExists(val customErrorMessage: String) :
        SignInErrorHMM(customErrorMessage)
    data class LinkErrorHMM(val customErrorMessage: String) : SignInErrorHMM(customErrorMessage)
    data class Other(val customErrorMessage: String) : SignInErrorHMM(customErrorMessage)
}

private fun mapSignInError(error: HMMAppError): SignInErrorHMM =
    when (error) {
        is SignInWithEmailAndPasswordErrorHMM.UserNotFound -> SignInErrorHMM.UserNotFound
        is SignInWithEmailAndPasswordErrorHMM.WrongPassword -> SignInErrorHMM.WrongPassword
        is SignInWithEmailAndPasswordErrorHMM.UserDisabled -> SignInErrorHMM.UserDisabled
        is SignInWithEmailAndPasswordErrorHMM.TooManyRequests -> SignInErrorHMM.TooManyRequests
        else -> SignInErrorHMM.Other(error.message)
    }
