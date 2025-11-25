package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.flatMapSuspend
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase

data class SignInSuccess(val uid: String)
class SignInUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val setIsConnectedUseCase: SetIsConnectedUseCase
) {
    suspend operator fun invoke(parameters: Parameters.DoubleStringParam): AppResult<SignInSuccess, AppError> =
        authenticationRepository
            .signInWithEmailAndPassword(parameters.firstValue, parameters.secondValue)
            .flatMapSuspend { success ->
                setIsConnectedUseCase(true).mapSuccess {
                    SignInSuccess(
                        success
                    )
                }
            }
}
