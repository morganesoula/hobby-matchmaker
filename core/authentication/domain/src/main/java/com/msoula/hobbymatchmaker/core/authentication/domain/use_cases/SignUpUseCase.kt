package com.msoula.hobbymatchmaker.core.authentication.domain.use_cases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository

class SignUpUseCase(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke(email: String, password: String) =
        authenticationRepository.signUp(email, password)
}
