package com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models

import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

fun MovieDetailDomainModel.toMovieDetailUiModel(): MovieDetailUiModel {
    return MovieDetailUiModel(
        id = this.id ?: MovieDetailDomainModel.DEFAULT_ID,
        title = this.title ?: MovieDetailDomainModel.DEFAULT_TITLE,
        synopsis = this.synopsis ?: MovieDetailDomainModel.DEFAULT_SYNOPSIS,
        posterPath = this.localCoverFilePath ?: "",
        genre = this.genre?.map { it.name ?: "" } ?: emptyList(),
        releaseDate = this.releaseDate?.extractYear() ?: "",
        status = this.status ?: "",
        popularity = this.popularity ?: 0.0,
        cast = this.cast?.associate { actor ->
            val name = actor.name ?: MovieActorDomainModel.DEFAULT_NAME
            val role = actor.role ?: MovieActorDomainModel.DEFAULT_ROLE
            name to role
        }?.toMap() ?: emptyMap(),
        videoKey = this.videoKey ?: ""
    )
}

fun String.extractYear(): String {
    return if (this.isNotEmpty()) {
        val formatterBis = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(this, formatterBis)
        date.year.toString()
    } else {
        ""
    }
}
