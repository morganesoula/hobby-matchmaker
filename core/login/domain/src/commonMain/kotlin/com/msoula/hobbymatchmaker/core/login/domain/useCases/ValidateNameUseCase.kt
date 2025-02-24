package com.msoula.hobbymatchmaker.core.login.domain.useCases

import com.msoula.hobbymatchmaker.core.common.data.ValidationResult
import com.msoula.hobbymatchmaker.core.login.domain.Res
import com.msoula.hobbymatchmaker.core.login.domain.no_number_allowed

class ValidateNameUseCase {
    operator fun invoke(name: String): ValidationResult {
        return if (name.matches("^[a-zA-Z-]+\$".toRegex())) {
            ValidationResult(true)
        } else {
            ValidationResult(false, Res.string.no_number_allowed.toString()
            )
        }
    }
}
