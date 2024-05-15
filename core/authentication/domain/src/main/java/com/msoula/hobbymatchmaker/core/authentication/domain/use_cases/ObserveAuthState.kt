package com.msoula.hobbymatchmaker.core.authentication.domain.use_cases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository

class ObserveAuthState(private val authenticationRepository: AuthenticationRepository) {
    operator fun invoke() = authenticationRepository.observeAuthState()
}
