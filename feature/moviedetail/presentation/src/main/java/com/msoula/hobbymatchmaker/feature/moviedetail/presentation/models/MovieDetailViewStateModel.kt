package com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models

sealed interface MovieDetailViewStateModel {
    data object Loading : MovieDetailViewStateModel
    data object Empty : MovieDetailViewStateModel
    data class Success(val movie: MovieDetailUiModel) : MovieDetailViewStateModel
    data class Error(val error: String) : MovieDetailViewStateModel
}
