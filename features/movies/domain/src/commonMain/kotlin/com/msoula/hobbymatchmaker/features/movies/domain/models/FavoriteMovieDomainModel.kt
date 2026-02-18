package com.msoula.hobbymatchmaker.features.movies.domain.models

data class FavoriteMovieDomainModel(
    val id: Long = 0,
    val title: String = "",
    val releaseDate: String = "",
    val posterPath: String = "",
    val isShared: Boolean = false
) {
    companion object {
        val Initial = FavoriteMovieDomainModel()
    }
}
