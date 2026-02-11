package com.msoula.hobbymatchmaker.features.moviedetail.domain.models

data class MovieDetailDomainModel(
    val id: Long = -1,
    val title: String = "",
    val isFavorite: Boolean = false,
    val genre: List<GenreDomainModel> = emptyList(),
    val popularity: Double = 0.0,
    val releaseDate: String = "",
    val synopsis: String = "",
    val status: String = "",
    val localCoverFilePath: String = "",
    val coverFileName: String = "",
    val videoKey: String = "",
    val cast: List<MovieActorDomainModel> = emptyList(),
    val hasCast: Boolean = false,
    val duration: Int = 0
) {
    companion object {
        val Initial = MovieDetailDomainModel()
    }
}

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
    val name: String = "",
    val role: String = ""
) {
    companion object {
        val Initial = MovieActorDomainModel()
    }
}
