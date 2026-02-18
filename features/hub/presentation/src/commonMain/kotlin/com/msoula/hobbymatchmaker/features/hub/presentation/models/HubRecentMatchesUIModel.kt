package com.msoula.hobbymatchmaker.features.hub.presentation.models

data class HubRecentMatchesUIModel(
    val name: String = "",
    val pseudo: String = "",
    val avatarUrl: String? = null,
    val sharedMovieIds: List<Long>? = null,
    val sharedWithCount: Int = 0
) {
    companion object {
        val Initial = HubRecentMatchesUIModel()
    }
}
