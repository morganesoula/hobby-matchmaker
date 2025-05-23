package com.msoula.hobbymatchmaker.features.movies.presentation.models

sealed interface MovieUiEventModel {
    data class OnMovieDetailClicked(val movieId: Long) : MovieUiEventModel
    data class OnMovieUiFetchedError(val error: String) : MovieUiEventModel
    data object OnLogOutSuccess : MovieUiEventModel
    data object NoFetchingDetailPossible : MovieUiEventModel
}
