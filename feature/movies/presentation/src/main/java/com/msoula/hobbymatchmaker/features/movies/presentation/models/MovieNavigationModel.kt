package com.msoula.hobbymatchmaker.features.movies.presentation.models

sealed interface MovieNavigationModel {
    data class OnMovieDetailClicked(val movieId: Long) : MovieNavigationModel
}
