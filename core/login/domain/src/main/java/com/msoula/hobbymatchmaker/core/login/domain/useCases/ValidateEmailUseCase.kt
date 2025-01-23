package com.msoula.hobbymatchmaker.core.login.domain.useCases

class ValidateEmailUseCase {
    operator fun invoke(email: String): ValidationResult {
        return if (email.matches("^[A-Za-z](.*)(@)(.+)(\\.)(.+)$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }
}
