package com.msoula.hobbymatchmaker.features.movies.presentation.models

sealed interface CardEventModel {
    data class OnDoubleTap(val movie: MovieUiModel) : CardEventModel
}
