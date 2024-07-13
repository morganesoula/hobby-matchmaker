package com.msoula.hobbymatchmaker.feature.moviedetail.domain.models

data class MovieDetailDomainModel(
    val title: String?,
    val genre: List<Genre>?,
    val popularity: Double?,
    val releaseDate: String?,
    val synopsis: String?,
    val status: String?
) {
    companion object {
        const val DEFAULT_TITLE: String = ""
        const val DEFAULT_POPULARITY: Double = -1.0
        const val DEFAULT_RELEASE_DATE: String = ""
        const val DEFAULT_SYNOPSIS: String = ""
        const val DEFAULT_STATUS: String = ""
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
