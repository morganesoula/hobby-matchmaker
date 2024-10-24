package com.msoula.hobbymatchmaker.core.di.domain.useCase

import com.msoula.hobbymatchmaker.core.di.domain.ValidationResult

class ValidatePasswordUseCase {
    fun validatePassword(password: String): ValidationResult {
        return if (password.matches(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*(),.?\":{}|<>])(?=\\S+\$).{8,}\$"
                    .toRegex()
            )
        ) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }

    fun validateLoginPassword(password: String): ValidationResult {
        return ValidationResult(password.isNotEmpty())
    }
}
