package com.msoula.hobbymatchmaker.features.hub.presentation.models

sealed interface FavoriteMoviesSuccess {
    data object Empty : FavoriteMoviesSuccess
    data class Success(val movies: List<HubFavoriteMoviesUIModel>) : FavoriteMoviesSuccess
}

sealed interface RecentMatchesSuccess {
    data class Success(val friends: List<HubRecentMatchesUIModel>) : RecentMatchesSuccess
    data object Empty : RecentMatchesSuccess
}
