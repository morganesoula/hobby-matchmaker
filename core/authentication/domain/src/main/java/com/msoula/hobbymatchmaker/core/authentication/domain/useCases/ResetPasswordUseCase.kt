package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository

class ResetPasswordUseCase(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke(email: String) = authenticationRepository.resetPassword(email)
}
