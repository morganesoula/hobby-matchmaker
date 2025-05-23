package com.msoula.hobbymatchmaker.core.login.presentation.signIn.models

sealed interface EmailResetModel {
    data object Loading : EmailResetModel
    data object Success : EmailResetModel
    data class Failure(val message: String) : EmailResetModel
}
