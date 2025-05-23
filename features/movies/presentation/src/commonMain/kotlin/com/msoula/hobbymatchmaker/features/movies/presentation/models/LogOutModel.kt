package com.msoula.hobbymatchmaker.features.movies.presentation.models

sealed interface LogOutModel {
    data object Idle : LogOutModel
    data object Success : LogOutModel
    data class Error(val message: String) : LogOutModel
}
