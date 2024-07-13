package com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models

sealed interface MovieDetailEvent {
    data class OnMovieDetailFetchedError(val error: String) : MovieDetailEvent
}
