package com.msoula.hobbymatchmaker.core.design.util

import androidx.compose.runtime.Immutable

@Immutable
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val error: UIText, val hint: UIErrorHint = UIErrorHint()) : UiState<Nothing>
    data object Empty : UiState<Nothing>
}

sealed interface FormState<out T> {
    data class Idle<T>(val value: T) : FormState<T>
    data class Validation<T>(val value: T) : FormState<T>
    data class Invalid<T>(val value: T, val errors: Map<String, String>) : FormState<T>
    data class Submitting<T>(val value: T) : FormState<T>
    data class Success<T>(val result: T) : FormState<T>
    data class Error<T>(val value: T, val error: UIText) : FormState<T>
}
