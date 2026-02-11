package com.msoula.hobbymatchmaker.features.moviedetail.presentation.models

import com.msoula.hobbymatchmaker.core.common.extractYear
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel

data class MovieDetailUiModel(
    val id: Long = -1,
    val title: String = "",
    val isFavorite: Boolean = false,
    val synopsis: String = "",
    val posterPath: String = "",
    val genre: List<String> = emptyList(),
    val releaseDate: String = "",
    val status: String = "",
    val popularity: Double = 0.0,
    val hasCast: Boolean = true,
    val cast: List<MovieDetailActorUiModel> = emptyList(),
    val videoKey: String = "",
    val duration: Int = -1
) {
    companion object {
        val Initial = MovieDetailUiModel()
    }
}

fun MovieDetailDomainModel.toMovieDetailUiModel(): MovieDetailUiModel {
    val local = this.localCoverFilePath
    val remote = this.coverFileName

    val resolvedPoster = when {
        local.startsWith("file://", ignoreCase = true) -> local
        local.startsWith("/data/")
            || local.startsWith("/storage/")
            || local.startsWith("/var/")
            || local.startsWith("/private/var/")
            -> "file://$local"

        remote.startsWith("http", ignoreCase = true) -> remote
        remote.startsWith("/") -> "https://image.tmdb.org/t/p/w500$remote"

        else -> MovieDetailDomainModel.Initial.coverFileName
    }

    return MovieDetailUiModel(
        id = this.id,
        title = this.title,
        isFavorite = this.isFavorite,
        synopsis = this.synopsis,
        posterPath = resolvedPoster,
        genre = this.genre.map { it.name },
        releaseDate = this.releaseDate.extractYear(),
        status = this.status,
        popularity = this.popularity,
        cast = this.cast
            .filter { it.name.isNotBlank() }
            .map {
                MovieDetailActorUiModel(
                    name = it.name,
                    role = it.role
                )
            },
        videoKey = this.videoKey,
        duration = this.duration,
        hasCast = this.cast.isNotEmpty()
    )
}
