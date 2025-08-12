package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class ContinueAsGuestUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val authenticationRepository: AuthenticationRepository,
    private val sessionRepository: SessionRepository
) : FlowUseCase<Parameters.None, GuestSignInSuccess, GuestSignInError>(dispatcher) {

    override fun execute(parameters: Parameters.None):
        Flow<Result<GuestSignInSuccess, GuestSignInError>> {

        return channelFlow {
            send(Result.Loading)

            when (val result = authenticationRepository.signInAnonymously()) {
                is Result.Success -> {
                    val userInfo = result.data
                    val sessionUser = SessionUserDomainModel(
                        uid = userInfo.uid ?: "anonymous",
                        email = userInfo.email.orEmpty()
                    )

                    when (val result = sessionRepository.createUser(sessionUser)) {
                        is Result.Success -> {
                            sessionRepository.setIsConnected(true)
                            send(Result.Success(GuestSignInSuccess(sessionUser)))
                        }

                        is Result.Failure ->
                            send(
                                Result.Failure(
                                    GuestSignInError(
                                        result.error.message
                                    )
                                )
                            )

                        else -> Unit
                    }
                }

                is Result.Failure ->
                    send(
                        Result.Failure(
                            GuestSignInError(
                                result.error.message
                            )
                        )
                    )

                else -> Unit
            }
        }.flowOn(dispatcher)
    }
}

data class GuestSignInSuccess(val user: SessionUserDomainModel)
data class GuestSignInError(override val message: String) : AppError
