package com.msoula.movies.presentation

import com.msoula.movies.data.model.MovieUi

sealed interface CardEvent {
    data class OnDoubleTap(val movie: MovieUi) : CardEvent
}
