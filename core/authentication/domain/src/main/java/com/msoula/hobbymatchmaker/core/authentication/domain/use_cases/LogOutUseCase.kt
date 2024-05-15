package com.msoula.hobbymatchmaker.core.authentication.domain.use_cases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.mapSuccess

class LogOutUseCase(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke() = authenticationRepository.logOut().mapSuccess {
        authenticationRepository.observeAuthState()
    }
}
