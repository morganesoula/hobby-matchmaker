package com.msoula.hobbymatchmaker.features.movies.domain.models

data class MovieDomainModel(
    val id: Long = 0L,
    val title: String = "",
    val coverFileName: String = "",
    val localCoverFilePath: String = "",
    val isFavorite: Boolean = false,
    val isSeen: Boolean = false,
    val overview: String = "",
    val note: Double = 0.0
) {
    companion object {
        val Initial = MovieDomainModel()
    }
}
