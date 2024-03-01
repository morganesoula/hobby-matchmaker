package com.msoula.component.event

sealed interface SwipeCardEvent {

    data class OnSwipeLeft(val movieId: Int) : SwipeCardEvent
    data object OnSwipeRight : SwipeCardEvent
}
