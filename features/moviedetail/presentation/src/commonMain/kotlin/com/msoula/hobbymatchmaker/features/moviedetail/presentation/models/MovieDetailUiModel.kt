package com.msoula.hobbymatchmaker.features.moviedetail.presentation.models

import com.msoula.hobbymatchmaker.core.common.extractYear
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.Res
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.movie_canceled
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.movie_in_production
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.movie_planned
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.movie_post_production
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.movie_released
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.movie_rumored
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
    val videoKey: String = ""
) {
    companion object {
        const val DEFAULT_ID = -1
        const val DEFAULT_TITLE = ""
        const val DEFAULT_SYNOPSIS = ""
        const val DEFAULT_POSTER_PATH = ""
        const val DEFAULT_RELEASE_DATE = ""
        const val DEFAULT_STATUS = ""
        const val DEFAULT_POPULARITY = 0.0
        const val DEFAULT_VIDEO_URI = ""
    }
}

suspend fun MovieDetailDomainModel.toMovieDetailUiModel(): MovieDetailUiModel {
    return MovieDetailUiModel(
        id = this.id ?: MovieDetailDomainModel.DEFAULT_ID,
        title = this.title ?: MovieDetailDomainModel.DEFAULT_TITLE,
        synopsis = this.synopsis ?: MovieDetailDomainModel.DEFAULT_SYNOPSIS,
        posterPath = this.localCoverFilePath ?: "",
        genre = this.genre?.map { it.name ?: "" } ?: emptyList(),
        releaseDate = this.releaseDate?.extractYear() ?: "",
        status = this.status?.mapStatus() ?: "",
        popularity = this.popularity ?: 0.0,
        cast = this.cast?.associate { actor ->
            (actor.name ?: MovieActorDomainModel.DEFAULT_NAME) to
                (actor.role ?: MovieActorDomainModel.DEFAULT_ROLE)
        } ?: emptyMap(),
        videoKey = this.videoKey ?: ""
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
