package com.msoula.movies.data

import com.msoula.movies.presentation.model.MovieUi

sealed class MovieUiStateResult {
    data object Loading : MovieUiStateResult()

    data object Empty : MovieUiStateResult()

    data class Fetched(val list: List<MovieUi>) : MovieUiStateResult()

    data class Error(val throwable: Throwable?) : MovieUiStateResult()
}
