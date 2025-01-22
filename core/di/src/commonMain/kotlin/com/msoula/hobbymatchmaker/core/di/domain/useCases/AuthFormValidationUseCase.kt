package com.msoula.hobbymatchmaker.core.di.domain.useCases

data class AuthFormValidationUseCase(
    val validatePasswordUseCase: ValidatePasswordUseCase,
    val validateEmailUseCase: ValidateEmailUseCase,
    val validateFirstNameUseCase: ValidateNameUseCase,
    val validateLastNameUseCase: ValidateNameUseCase
)
