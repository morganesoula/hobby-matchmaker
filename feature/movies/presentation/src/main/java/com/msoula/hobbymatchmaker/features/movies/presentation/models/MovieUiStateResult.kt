package com.msoula.hobbymatchmaker.features.movies.presentation.models

sealed class MovieUiStateResult {
    data object Loading : MovieUiStateResult()

    data object Empty : MovieUiStateResult()

    data class Fetched(val list: List<MovieUiModel>) : MovieUiStateResult()

    data class Error(val throwable: Throwable?) : MovieUiStateResult()
}
