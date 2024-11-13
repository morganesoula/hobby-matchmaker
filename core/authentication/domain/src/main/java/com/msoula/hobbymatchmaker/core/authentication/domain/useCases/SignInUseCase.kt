package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SignInUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val authenticationRepository: AuthenticationRepository,
    private val setIsConnectedUseCase: SetIsConnectedUseCase
) : FlowUseCase<Parameters.DoubleStringParam, SignInSuccess, SignInError>(dispatcher) {

    override fun execute(parameters: Parameters.DoubleStringParam): Flow<Result<SignInSuccess, SignInError>> {
        return flow {
            emit(Result.Loading)

            when (val result = authenticationRepository.signInWithEmailAndPassword(
                parameters.firstValue,
                parameters.secondValue
            )) {
                is Result.Success -> {
                    setIsConnectedUseCase(true)
                    emit(Result.Success(SignInSuccess))
                }

                is Result.Failure -> emit(Result.Failure(result.error))
                is Result.BusinessRuleError -> {
                    emit(
                        when (result.error) {
                            is SignInWithEmailAndPasswordError.UserNotFound -> Result.BusinessRuleError(
                                SignInError.UserNotFound
                            )

                            is SignInWithEmailAndPasswordError.WrongPassword -> Result.BusinessRuleError(
                                SignInError.WrongPassword
                            )

                            is SignInWithEmailAndPasswordError.UserDisabled -> Result.BusinessRuleError(
                                SignInError.UserDisabled
                            )

                            is SignInWithEmailAndPasswordError.TooManyRequests -> Result.BusinessRuleError(
                                SignInError.TooManyRequests
                            )

                            is SignInWithEmailAndPasswordError.Other -> Result.BusinessRuleError(
                                SignInError.Other
                            )
                        }
                    )
                }

                else -> Unit
            }
        }.flowOn(dispatcher)
    }
}

data object SignInSuccess
sealed class SignInError(override val message: String) : AppError {
    data object WrongPassword : SignInError("Wrong password")
    data object UserNotFound : SignInError("User not found")
    data object UserDisabled : SignInError("User disabled")
    data object TooManyRequests : SignInError("Too many requests")
    data object Other : SignInError("Other error")
}
