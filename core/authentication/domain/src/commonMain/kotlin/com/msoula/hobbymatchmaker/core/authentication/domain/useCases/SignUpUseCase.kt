package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.HMMAppError
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.errors.SessionErrors
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class SignUpUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val createUserUseCase: CreateUserUseCase,
    private val dispatcher: CoroutineDispatcher
) : FlowUseCase<Parameters.DoubleStringParam, SignUpSuccess, SignUpErrors>(dispatcher) {

    override fun execute(parameters: Parameters.DoubleStringParam): Flow<Result<SignUpSuccess, SignUpErrors>> {
        val email = parameters.firstValue

        return channelFlow {
            send(Result.Loading)

            when (val result = authenticationRepository.signUp(email, parameters.secondValue)) {
                is Result.Success -> {
                    when (val creatingUserResult = createUserUseCase(
                        SessionUserDomainModel(uid = result.data, email = email)
                    )) {
                        is Result.Success -> send(Result.Success(SignUpSuccess(result.data)))
                        is Result.Failure -> send(
                            Result.Failure(
                                mapCreateUserError(
                                    creatingUserResult.error
                                )
                            )
                        )

                        is Result.Loading -> Unit
                    }
                }

                is Result.Failure -> {
                    send(Result.Failure(mapSignUpError(result.error)))
                }

                is Result.Loading -> Unit
            }
        }.flowOn(dispatcher)
    }
}

private fun mapSignUpError(error: HMMAppError): SignUpErrors = when (error) {
    is CreateUserWithEmailAndPasswordErrorHMM.EmailAlreadyExists -> SignUpErrors.EmailAlreadyExists
    is CreateUserWithEmailAndPasswordErrorHMM.UserDisabled -> SignUpErrors.UserDisabled
    is CreateUserWithEmailAndPasswordErrorHMM.TooManyRequests -> SignUpErrors.TooManyRequests
    is CreateUserWithEmailAndPasswordErrorHMM.InternalErrorHMM -> SignUpErrors.InternalErrorHMM
    is CreateUserWithEmailAndPasswordErrorHMM.Connection -> SignUpErrors.Connection
    else -> SignUpErrors.UnknownErrorHMM(error.message)
}

private fun mapCreateUserError(error: SessionErrors.CreateUserErrorHMM): SignUpErrors {
    return when (error) {
        is SessionErrors.CreateUserErrorHMM.SaveErrorHMM ->
            SignUpErrors.UnknownErrorHMM(error.message)
    }
}

data class SignUpSuccess(val uid: String)

sealed class SignUpErrors(override val message: String) : HMMAppError {
    data object EmailAlreadyExists : SignUpErrors("")
    data object UserDisabled : SignUpErrors("")
    data object TooManyRequests : SignUpErrors("")
    data object InternalErrorHMM : SignUpErrors("")
    data object Connection : SignUpErrors("")
    data class UnknownErrorHMM(override val message: String) : SignUpErrors("")
}
