package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SignUpUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val createUserUseCase: CreateUserUseCase,
    private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Parameters.DoubleStringParam, SignUpSuccess, SignUpErrors>(dispatcher) {

    override fun execute(parameters: Parameters.DoubleStringParam): Flow<Result<SignUpSuccess, SignUpErrors>> {
        val email = parameters.firstValue

        return flow {
            emit(Result.Loading)

            when (val result =
                authenticationRepository.signUp(email, parameters.secondValue)) {
                is Result.Success -> when (val creatingUserResult = createUserUseCase(
                    SessionUserDomainModel(uid = result.data, email = email)
                )) {
                    is Result.Success -> emit(Result.Success(SignUpSuccess(result.data)))
                    is Result.Failure -> emit(
                        Result.Failure(
                            SignUpErrors.CreateUserError.SaveError(creatingUserResult.error.message)
                        )
                    )

                    else -> Unit
                }

                is Result.Failure -> {
                    val error: AppError = when (result.error) {
                        is CreateUserWithEmailAndPasswordError.EmailAlreadyExists -> SignUpErrors.EmailAlreadyExists
                        is CreateUserWithEmailAndPasswordError.UserDisabled -> SignUpErrors.UserDisabled
                        is CreateUserWithEmailAndPasswordError.TooManyRequests -> SignUpErrors.TooManyRequests
                        is CreateUserWithEmailAndPasswordError.InternalError -> SignUpErrors.InternalError
                        else -> SignUpErrors.UnknownError(result.error.message)
                    }
                    emit(Result.Failure(error))
                }

                is Result.Loading -> Unit
            }
        }.flowOn(dispatcher)
    }
}

data class SignUpSuccess(val uid: String)
sealed class SignUpErrors(override val message: String) : AppError {
    data object EmailAlreadyExists : SignUpErrors("")
    data object UserDisabled : SignUpErrors("")
    data object TooManyRequests : SignUpErrors("")
    data object InternalError : SignUpErrors("")
    data class UnknownError(override val message: String) : SignUpErrors("")
    sealed class CreateUserError(override val message: String) : AppError {
        data class SaveError(val saveErrorMessage: String) :
            CreateUserError(saveErrorMessage)
    }
}
