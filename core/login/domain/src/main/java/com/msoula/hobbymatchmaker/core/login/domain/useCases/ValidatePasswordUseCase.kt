package com.msoula.hobbymatchmaker.core.login.domain.useCases

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
}
