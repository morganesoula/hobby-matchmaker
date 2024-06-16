package com.msoula.hobbymatchmaker.core.login.domain.use_cases

import com.msoula.hobbymatchmaker.core.common.ValidationResult

class ValidatePasswordUseCase {
    fun validatePassword(password: String): ValidationResult {
        return if (password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*(),.?\":{}|<>])(?=\\S+\$).{8,}\$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }
}
