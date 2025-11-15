package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.flatMapSuspend
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ClearCurrentUserProfileUuidUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import kotlinx.coroutines.flow.first

data object LogOutSuccess
class LogOutUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val setIsConnectedUseCase: SetIsConnectedUseCase,
    private val clearCurrentUserProfileUuidUseCase: ClearCurrentUserProfileUuidUseCase,
    private val observeIsConnectedUseCase: ObserveIsConnectedUseCase
) {
    suspend operator fun invoke(): AppResult<LogOutSuccess, AppError> =
        authenticationRepository.logOut()
            .flatMapSuspend { setIsConnectedUseCase(false) }
            .mapSuccess {
                clearCurrentUserProfileUuidUseCase()
                observeIsConnectedUseCase().first { connected -> !connected }
                LogOutSuccess
            }
}
