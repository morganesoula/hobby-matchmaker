package com.msoula.hobbymatchmaker.features.movies.presentation.models

import com.msoula.hobbymatchmaker.core.design.util.UIText


sealed interface MovieUiEventModel {
    data class OnMovieDetailClicked(val movieId: Long) : MovieUiEventModel
    data class OnMovieUiFetchedError(val error: UIText) : MovieUiEventModel
    data class OnLogOutFailure(val error: UIText) : MovieUiEventModel
    data class ShowError(val error: UIText) : MovieUiEventModel
    data object OnLogOutSuccess : MovieUiEventModel
    data object NoFetchingDetailPossible : MovieUiEventModel
}
