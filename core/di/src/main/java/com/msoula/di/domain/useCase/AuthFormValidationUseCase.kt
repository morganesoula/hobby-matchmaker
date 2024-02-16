package com.msoula.di.domain.useCase

data class AuthFormValidationUseCase(
    val validateEmail: ValidateEmail,
    val validatePassword: ValidatePassword,
    val validateFirstName: ValidateName,
    val validateLastName: ValidateName,
)
