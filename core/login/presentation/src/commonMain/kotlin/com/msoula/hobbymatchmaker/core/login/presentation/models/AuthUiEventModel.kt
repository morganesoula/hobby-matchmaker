package com.msoula.hobbymatchmaker.core.login.presentation.models

import com.msoula.hobbymatchmaker.core.common.UIText

sealed interface AuthUiEventModel {
    data class ShowError(val error: UIText) : AuthUiEventModel
    data object OnResetPasswordSuccess : AuthUiEventModel
    data object OnSignInSuccess : AuthUiEventModel
    data object OnSignUpSuccess : AuthUiEventModel
}
