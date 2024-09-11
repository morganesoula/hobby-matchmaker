package com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.models

data class MovieEntityModel(
    val id: Long? = 0L,
    val title: String? = "",
    val posterFileName: String? = "",
    val synopsis: String? = "",
    val releaseDate: String? = "",
    val genres: List<Genre>? = emptyList(),
    val localCoverFilePath: String? = "",
    @field:JvmField
    val isFavorite: Boolean? = false,
    @field:JvmField
    val isSeen: Boolean? = false,
    val popularity: Double? = 0.0,
    val status: String? = "",
    val cast: List<Actor>? = emptyList(),
    val videoKey: String? = ""
)

data class Genre(
    val id: Int? = null,
    val name: String? = null
) {
    companion object {
        const val DEFAULT_ID: Int = -1
        const val DEFAULT_NAME: String = ""
    }
}

data class Actor(
    val id: Long = 0L,
    val name: String? = null,
    val role: String? = null
)
