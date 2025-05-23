package com.msoula.hobbymatchmaker.features.movies.presentation.models

sealed interface FetchStatusModel {
    data object NeverFetched : FetchStatusModel
    data object Loading : FetchStatusModel
    data object Success : FetchStatusModel
    data class Error(val error: String) : FetchStatusModel
}
