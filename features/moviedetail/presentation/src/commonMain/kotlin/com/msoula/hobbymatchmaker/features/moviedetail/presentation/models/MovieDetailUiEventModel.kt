package com.msoula.hobbymatchmaker.features.moviedetail.presentation.models

import com.msoula.hobbymatchmaker.core.common.UIText

sealed interface MovieDetailUiEventModel {
    data class OnMovieDetailUiFetchedError(val error: UIText) : MovieDetailUiEventModel
    data class OnPlayMovieTrailerClicked(val movieId: Long, val isVideoURIknown: Boolean = false) :
        MovieDetailUiEventModel

    data class OnPlayMovieTrailerReady(val movieUri: String) : MovieDetailUiEventModel
    data object ErrorFetchingTrailer : MovieDetailUiEventModel
    data object LoadingTrailer : MovieDetailUiEventModel
    data object NoConnection : MovieDetailUiEventModel
}
