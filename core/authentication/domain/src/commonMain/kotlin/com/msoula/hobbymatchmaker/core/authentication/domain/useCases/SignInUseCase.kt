package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SignInUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val authenticationRepository: AuthenticationRepository
) : FlowUseCase<Parameters.DoubleStringParam, SignInSuccess, SignInError>(dispatcher) {

    override fun execute(parameters: Parameters.DoubleStringParam): Flow<Result<SignInSuccess, SignInError>> {
        return flow {
            emit(Result.Loading)

            when (val result = authenticationRepository.signInWithEmailAndPassword(
                parameters.firstValue,
                parameters.secondValue
            )) {
                is Result.Success -> emit(Result.Success(SignInSuccess))
                is Result.Failure -> {
                    val error: SignInError = when (result.error) {
                        is SignInWithEmailAndPasswordError.UserNotFound -> SignInError.UserNotFound
                        is SignInWithEmailAndPasswordError.WrongPassword -> SignInError.WrongPassword
                        is SignInWithEmailAndPasswordError.UserDisabled -> SignInError.UserDisabled
                        is SignInWithEmailAndPasswordError.TooManyRequests -> SignInError.TooManyRequests
                        else -> SignInError.Other(result.error.message)
                    }
                    emit(Result.Failure(error))
                }

                else -> Unit
            }
        }.flowOn(dispatcher)
    }
}

data object SignInSuccess
sealed class SignInError(override val message: String) : AppError {
    data object WrongPassword : SignInError("")
    data object UserNotFound : SignInError("")
    data object UserDisabled : SignInError("")
    data object TooManyRequests : SignInError("")
    data class Other(val customErrorMessage: String) : SignInError(customErrorMessage)
}
