package com.msoula.hobbymatchmaker.core.login.domain.useCases

import com.msoula.hobbymatchmaker.core.common.ValidationResult
import com.msoula.hobbymatchmaker.core.login.domain.R

class ValidateNameUseCase {
    operator fun invoke(name: String): ValidationResult {
        return if (name.matches("^[a-zA-Z-]+\$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false, R.string.no_number_allowed)
        }
    }
}
