package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.models

data class MovieDetailDataModel(
    val id: Long = 0L,
    val title: String = "",
    val overview: String = "",
    val poster: String = "",
    val releaseDate: String = "",
    val genres: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val isSeen: Boolean = false,
    val popularity: Double? = null,
    val status: String? = null,
    val videoKey: String? = null,
    val duration: Long? = null,
    val note: Double? = null,
    val actors: List<ActorDataModel>? = null,
    val hasCast: Boolean = false
) {
    companion object {
        val Initial = MovieDetailDataModel()
    }
}
