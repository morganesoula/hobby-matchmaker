package com.msoula.hobbymatchmaker.core.login.domain.useCases

import com.msoula.hobbymatchmaker.core.common.data.ValidationResult

class ValidateEmailUseCase {
    operator fun invoke(email: String): ValidationResult {
        return if (email.matches("^[A-Za-z](.*)(@)(.+)(\\.)(.+)$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }
}
