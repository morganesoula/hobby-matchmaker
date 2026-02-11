package com.msoula.hobbymatchmaker.features.moviedetail.presentation.models

sealed interface MovieDetailUiEventModel {
    data class OnPlayMovieTrailerClicked(val movieId: Long, val isVideoURIknown: Boolean = false) :
        MovieDetailUiEventModel
    data class OnMovieDoubleTap(val movieId: Long) : MovieDetailUiEventModel
}
