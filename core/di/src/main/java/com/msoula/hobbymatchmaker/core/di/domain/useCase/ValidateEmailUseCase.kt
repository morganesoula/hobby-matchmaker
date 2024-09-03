package com.msoula.hobbymatchmaker.core.di.domain.useCase

import com.msoula.hobbymatchmaker.core.di.domain.ValidationResult

class ValidateEmailUseCase {
    operator fun invoke(email: String): ValidationResult {
        return if (email.matches("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false)
        }
    }
}
