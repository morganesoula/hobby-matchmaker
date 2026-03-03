package com.msoula.hobbymatchmaker.features.movies.presentation.models

sealed interface MovieEventModel {
    data class OnCardDoubleTap(val movie: MovieUiModel) : MovieEventModel
    data class OnCardSingleTap(val movieId: Long, val movieOverview: String?) : MovieEventModel
    data object RetryMovies : MovieEventModel
}
