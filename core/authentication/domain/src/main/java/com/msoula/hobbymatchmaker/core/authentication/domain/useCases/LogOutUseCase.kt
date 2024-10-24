package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.Result

class LogOutUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): Result<Boolean, LogOutError> {
        return authenticationRepository.logOut()
    }
}
