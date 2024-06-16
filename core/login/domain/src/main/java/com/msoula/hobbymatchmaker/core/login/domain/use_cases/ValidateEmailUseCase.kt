package com.msoula.hobbymatchmaker.core.login.domain.use_cases

import com.msoula.hobbymatchmaker.core.common.ValidationResult

class ValidateEmailUseCase {
    operator fun invoke(email: String): ValidationResult {
        return if (email.matches("^[A-Za-z](.*)(@)(.+)(\\.)(.+)$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }
}
