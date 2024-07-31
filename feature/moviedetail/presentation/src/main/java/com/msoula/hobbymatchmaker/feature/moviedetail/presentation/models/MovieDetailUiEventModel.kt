package com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models

sealed interface MovieDetailUiEventModel {
    data class OnMovieDetailUiFetchedError(val error: String) : MovieDetailUiEventModel
}
