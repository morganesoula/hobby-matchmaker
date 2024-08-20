package com.msoula.hobbymatchmaker.feature.moviedetail.domain.models

data class MovieDetailDomainModel(
    val info: MovieInfoDomainModel,
    val cast: MovieCastDomainModel?
)

data class MovieInfoDomainModel(
    val id: Long? = null,
    val title: String? = null,
    val genre: List<Genre>? = null,
    val popularity: Double? = null,
    val releaseDate: String? = null,
    val synopsis: String? = null,
    val status: String? = null,
    val posterPath: String? = null,
    val videoKey: String? = ""
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

data class Genre(
    val id: Int?,
    val name: String?
) {
    companion object {
        const val DEFAULT_ID: Int = -1
        const val DEFAULT_NAME: String = ""
    }
}

data class MovieCastDomainModel(
    val cast: List<MovieActorDomainModel>
)

data class MovieActorDomainModel(
    val id: Long,
    val name: String?,
    val role: String?
) {
    companion object {
        const val DEFAULT_NAME: String = ""
        const val DEFAULT_ROLE: String = ""
    }
}
