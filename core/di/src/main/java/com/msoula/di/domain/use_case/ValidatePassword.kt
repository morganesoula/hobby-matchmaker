package com.msoula.di.domain.use_case

import com.msoula.di.domain.ValidationResult

class ValidatePassword {

    fun validatePassword(password: String): ValidationResult {
        return if (password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*(),.?\":{}|<>])(?=\\S+\$).{8,}\$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }

    fun validateLoginPassword(password: String): ValidationResult {
        return ValidationResult(password.isNotEmpty())
    }
}