package com.msoula.hobbymatchmaker.features.moviedetail.presentation.models

import com.msoula.hobbymatchmaker.core.common.extractYear
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.movie_canceled
import com.msoula.hobbymatchmaker.core.design.movie_in_production
import com.msoula.hobbymatchmaker.core.design.movie_planned
import com.msoula.hobbymatchmaker.core.design.movie_post_production
import com.msoula.hobbymatchmaker.core.design.movie_released
import com.msoula.hobbymatchmaker.core.design.movie_rumored
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.GenreDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import org.jetbrains.compose.resources.getString

data class MovieDetailUiModel(
    val id: Long = -1,
    val title: String = "",
    val synopsis: String = "",
    val posterPath: String = "",
    val genre: List<String> = emptyList(),
    val releaseDate: String = "",
    val status: String = "",
    val popularity: Double = 0.0,
    val cast: Map<String, String> = emptyMap(),
    val videoKey: String = "",
    val duration: Int = -1
)

suspend fun MovieDetailDomainModel.toMovieDetailUiModel(): MovieDetailUiModel {
    val local = this.localCoverFilePath.orEmpty()
    val remote = this.coverFileName.orEmpty()

    val resolvedPoster = when {
        local.startsWith("file://", ignoreCase = true) -> local
        local.startsWith("/data/")
            || local.startsWith("/storage/")
            || local.startsWith("/var/")
            || local.startsWith("/private/var/")
            -> "file://$local"

        remote.startsWith("http", ignoreCase = true) -> remote
        remote.startsWith("/") -> "https://image.tmdb.org/t/p/w500$remote"

        else -> MovieDetailDomainModel.DEFAULT_POSTER_PATH
    }

    return MovieDetailUiModel(
        id = this.id ?: MovieDetailDomainModel.DEFAULT_ID,
        title = this.title ?: MovieDetailDomainModel.DEFAULT_TITLE,
        synopsis = this.synopsis ?: MovieDetailDomainModel.DEFAULT_SYNOPSIS,
        posterPath = resolvedPoster,
        genre = this.genre?.map { it.name ?: "" } ?: listOf(GenreDomainModel.DEFAULT_NAME),
        releaseDate = this.releaseDate?.extractYear()
            ?: MovieDetailDomainModel.DEFAULT_RELEASE_DATE,
        status = this.status?.mapStatus() ?: MovieDetailDomainModel.DEFAULT_STATUS,
        popularity = this.popularity ?: MovieDetailDomainModel.DEFAULT_POPULARITY,
        cast = this.cast?.associate { actor ->
            (actor.name ?: MovieActorDomainModel.DEFAULT_NAME) to
                (actor.role ?: MovieActorDomainModel.DEFAULT_ROLE)
        } ?: emptyMap(),
        videoKey = this.videoKey ?: MovieDetailDomainModel.DEFAULT_VIDEO_KEY,
        duration = this.duration ?: MovieDetailDomainModel.DEFAULT_DURATION
    )
}

private suspend fun String.mapStatus(): String =
    when (this.trim()) {
        "Rumored" -> getString(Res.string.movie_rumored)
        "Planned" -> getString(Res.string.movie_planned)
        "In Production" -> getString(Res.string.movie_in_production)
        "Post Production" -> getString(Res.string.movie_post_production)
        "Released" -> getString(Res.string.movie_released)
        "Canceled" -> getString(Res.string.movie_canceled)
        else -> this
    }
