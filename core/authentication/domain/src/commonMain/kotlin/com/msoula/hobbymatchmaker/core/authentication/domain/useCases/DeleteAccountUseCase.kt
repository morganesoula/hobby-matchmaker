package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.flatMapSuspend
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ClearCurrentUserProfileUuidUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase

data object DeleteAccountSuccess
class DeleteAccountUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val setIsConnectedUseCase: SetIsConnectedUseCase,
    private val clearCurrentUserProfileUuidUseCase: ClearCurrentUserProfileUuidUseCase,
) {
    suspend operator fun invoke(): AppResult<DeleteAccountSuccess, AppError> =
        authenticationRepository.deleteAccount()
            .flatMapSuspend { setIsConnectedUseCase(false) }
            .flatMapSuspend { clearCurrentUserProfileUuidUseCase() }
            .mapSuccess { DeleteAccountSuccess }
}
