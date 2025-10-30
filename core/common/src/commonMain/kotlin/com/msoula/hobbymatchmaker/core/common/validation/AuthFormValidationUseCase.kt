package com.msoula.hobbymatchmaker.core.common.validation

data class AuthFormValidationUseCase(
    val validatePasswordUseCase: ValidatePasswordUseCase,
    val validateEmailUseCase: ValidateEmailUseCase,
    val validateFirstNameUseCase: ValidateNameUseCase,
    val validateLastNameUseCase: ValidateNameUseCase
)
