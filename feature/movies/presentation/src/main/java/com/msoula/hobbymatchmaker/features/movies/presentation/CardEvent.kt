package com.msoula.hobbymatchmaker.features.movies.presentation

import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel

sealed interface CardEvent {
    data class OnDoubleTap(val movie: MovieUiModel) : CardEvent
}
