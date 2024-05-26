package com.msoula.hobbymatchmaker.core.authentication.domain.use_cases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository

class ObserveAuthenticationStateUseCase(private val authenticationRepository: AuthenticationRepository) {
    operator fun invoke() = authenticationRepository.observeAuthenticationState()
}
