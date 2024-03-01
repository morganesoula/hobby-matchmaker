package com.msoula.movies.data

import com.msoula.movies.data.model.Movie

sealed class MovieUiStateResult {
    data object Loading : MovieUiStateResult()
    data class Fetched(val list: List<Movie>) : MovieUiStateResult()
    data class Error(val throwable: Throwable?) : MovieUiStateResult()
}
