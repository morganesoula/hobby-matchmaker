package com.msoula.hobbymatchmaker.features.hub.presentation.models

data class HubFavoriteMoviesUIModel(
    val id: Long = 0L,
    val title: String = "",
    val releaseDate: String = "",
    val posterPath: String = "",
    val isShared: Boolean = false
) {
    companion object {
        val Initial = HubFavoriteMoviesUIModel()
    }
}
