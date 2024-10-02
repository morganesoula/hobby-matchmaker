package com.msoula.hobbymatchmaker.core.authentication.domain.use_cases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.Result

class LogOutUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return authenticationRepository.logOut()
    }
}
