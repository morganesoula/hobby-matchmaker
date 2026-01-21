package com.msoula.hobbymatchmaker.features.movies.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class PaginationStateModel(
    val currentPage: Int = 3,
    val isLoadingMore: Boolean = false,
    val hasMorePages: Boolean = true
) {
    companion object {
        val Initial = PaginationStateModel()
    }
}
