package com.msoula.hobbymatchmaker.core.di.domain.useCase

data class AuthFormValidationUseCase(
    val validatePasswordUseCase: ValidatePasswordUseCase,
    val validateEmailUseCase: ValidateEmailUseCase,
    val validateFirstNameUseCase: ValidateNameUseCase,
    val validateLastNameUseCase: ValidateNameUseCase
)
