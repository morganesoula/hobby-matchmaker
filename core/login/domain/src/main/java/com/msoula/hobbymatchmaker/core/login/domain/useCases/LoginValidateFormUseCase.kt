package com.msoula.hobbymatchmaker.core.login.domain.useCases

data class LoginValidateFormUseCase(
    val validateEmail: ValidateEmailUseCase,
    val validatePassword: ValidatePasswordUseCase,
    val validateFirstName: ValidateNameUseCase
)
