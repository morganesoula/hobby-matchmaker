package com.msoula.hobbymatchmaker.features.hub.presentation.models

data class HubRecentMatchesUIModel(
    val uid: String = "",
    val name: String = "",
    val pseudo: String = "",
    val avatarUrl: String? = null,
    val sharedMovieIds: List<Long> = emptyList(),
    val commonMovies: List<HubFavoriteMoviesUIModel> = emptyList()
) {
    companion object {
        val Initial = HubRecentMatchesUIModel()
    }
}
