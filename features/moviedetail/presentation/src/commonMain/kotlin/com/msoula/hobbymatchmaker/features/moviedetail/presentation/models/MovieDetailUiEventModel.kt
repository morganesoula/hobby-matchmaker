package com.msoula.hobbymatchmaker.features.moviedetail.presentation.models

sealed interface MovieDetailUiEventModel {
    data class OnMovieDetailUiFetchedError(val error: String) : MovieDetailUiEventModel
    data class OnPlayMovieTrailerClicked(val movieId: Long, val isVideoURIknown: Boolean = false) :
        MovieDetailUiEventModel
    data class OnPlayMovieTrailerReady(val movieUri: String) : MovieDetailUiEventModel
    data class ErrorFetchingTrailer(val message: String) : MovieDetailUiEventModel
    data object LoadingTrailer : MovieDetailUiEventModel
}
