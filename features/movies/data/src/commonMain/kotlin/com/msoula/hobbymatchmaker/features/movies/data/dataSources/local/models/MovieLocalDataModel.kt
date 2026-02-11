package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.models

data class MovieLocalDataModel(
    val id: Long = 0L,
    val title: String = "",
    val overview: String = "",
    val remotePoster: String = "",
    val localPoster: String? = null,
    val releaseDate: String = "",
    val genres: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isSeen: Boolean = false,
    val popularity: Double? = null,
    val status: String? = null,
    val videoKey: String? = null,
    val duration: Long? = null,
    val note: Double? = null
) {
    companion object Companion {
        val Initial = MovieLocalDataModel()
    }
}
