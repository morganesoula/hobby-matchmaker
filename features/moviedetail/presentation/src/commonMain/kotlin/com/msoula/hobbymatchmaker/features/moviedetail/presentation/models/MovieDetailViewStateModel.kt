package com.msoula.hobbymatchmaker.features.moviedetail.presentation.models

import com.msoula.hobbymatchmaker.core.common.UIText

sealed interface MovieDetailViewStateModel {
    data object Loading : MovieDetailViewStateModel
    data object Empty : MovieDetailViewStateModel
    data class Success(val movie: MovieDetailUiModel) : MovieDetailViewStateModel
    data class Error(val error: UIText) : MovieDetailViewStateModel
}
