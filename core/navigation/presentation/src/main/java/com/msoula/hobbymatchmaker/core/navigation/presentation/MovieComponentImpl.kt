package com.msoula.hobbymatchmaker.core.navigation.presentation

import com.arkivanov.decompose.ComponentContext
import com.msoula.hobbymatchmaker.core.navigation.domain.MainComponent

class MovieComponentImpl(
    private val componentContext: ComponentContext,
    private val onNavigateToMovieDetail: (Long) -> Unit
) : MainComponent {

    override fun onMovieClicked(movieId: Long) {
        onNavigateToMovieDetail(movieId)
    }
}
