package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Parameters

class ResetPasswordUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(parameters: Parameters.StringParam): AppResult<Unit, AppError> {
        val email = parameters.value.trim()
        if (email.isBlank()) {
            return AppResult.Failure(AppError.Domain.Validation("Email is required"))
        }

        return when (val result = authenticationRepository.resetPassword(email)) {
            is AppResult.Success -> result
            is AppResult.Failure -> when (result.error) {
                is AppError.Domain.Unauthorized -> AppResult.Success(Unit)
                else -> result
            }
        }
    }
}
