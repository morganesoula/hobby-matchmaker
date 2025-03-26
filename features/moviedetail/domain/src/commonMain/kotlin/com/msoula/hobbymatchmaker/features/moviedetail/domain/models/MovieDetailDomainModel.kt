package com.msoula.hobbymatchmaker.features.moviedetail.domain.models

import kotlinx.serialization.Serializable

data class MovieDetailDomainModel(
    val id: Long? = null,
    val title: String? = null,
    val genre: List<GenreDomainModel>? = null,
    val popularity: Double? = null,
    val releaseDate: String? = null,
    val synopsis: String? = null,
    val status: String? = null,
    val localCoverFilePath: String? = null,
    var videoKey: String? = "",
    val cast: List<MovieActorDomainModel>? = null
) {
    companion object {
        const val DEFAULT_ID: Long = -1
        const val DEFAULT_TITLE: String = ""
        const val DEFAULT_POPULARITY: Double = -1.0
        const val DEFAULT_RELEASE_DATE: String = ""
        const val DEFAULT_SYNOPSIS: String = ""
        const val DEFAULT_STATUS: String = ""
        const val DEFAULT_POSTER_PATH: String = ""
    }
}

@Serializable
data class GenreDomainModel(
    val id: Int? = null,
    val name: String? = null
) {
    companion object {
        const val DEFAULT_ID: Int = -1
        const val DEFAULT_NAME: String = ""
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
