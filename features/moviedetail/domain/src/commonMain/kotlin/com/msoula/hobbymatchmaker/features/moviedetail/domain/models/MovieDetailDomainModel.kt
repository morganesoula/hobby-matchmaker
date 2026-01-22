package com.msoula.hobbymatchmaker.features.moviedetail.domain.models

import kotlinx.serialization.Serializable

data class MovieDetailDomainModel(
    val id: Long? = null,
    val title: String? = null,
    val isFavorite: Boolean? = null,
    val genre: List<GenreDomainModel>? = null,
    val popularity: Double? = null,
    val releaseDate: String? = null,
    val synopsis: String? = null,
    val status: String? = null,
    val localCoverFilePath: String? = null,
    val coverFileName: String? = null,
    var videoKey: String? = "",
    val cast: List<MovieActorDomainModel>? = null,
    val duration: Int? = null
) {
    companion object {
        const val DEFAULT_ID: Long = -1
        const val DEFAULT_TITLE: String = ""
        const val DEFAULT_IS_FAVORITE: Boolean = false
        const val DEFAULT_POPULARITY: Double = -1.0
        const val DEFAULT_RELEASE_DATE: String = ""
        const val DEFAULT_SYNOPSIS: String = ""
        const val DEFAULT_STATUS: String = ""
        const val DEFAULT_POSTER_PATH: String = ""
        const val DEFAULT_VIDEO_KEY: String = ""
        const val DEFAULT_DURATION: Int = 0
    }
}

@Serializable
data class GenreDomainModel(
    val id: Int = DEFAULT_ID,
    val name: String = DEFAULT_NAME
) {
    init {
        require(name.isNotBlank()) { "Genre name cannot be blank" }
    }

    companion object {
        const val DEFAULT_ID: Int = -1
        const val DEFAULT_NAME: String = "Unknown"
    }
}

data class MovieCastDomainModel(
    val cast: List<MovieActorDomainModel> = emptyList()
)

data class MovieActorDomainModel(
    val id: Long = 0L,
    val name: String? = null,
    val role: String? = null
) {
    companion object {
        const val DEFAULT_NAME: String = ""
        const val DEFAULT_ROLE: String = ""
    }
}
