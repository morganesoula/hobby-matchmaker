package com.msoula.movies.presentation

import com.msoula.movies.presentation.model.MovieUi

sealed interface CardEvent {
    data class OnDoubleTap(val movie: MovieUi) : CardEvent
}
