package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.mappers

import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.database.Actor
import com.msoula.hobbymatchmaker.core.database.models.MovieDetailDataEntity
import com.msoula.hobbymatchmaker.core.database.models.MovieUpdatedDataEntity
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.GenreDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import kotlinx.serialization.json.Json

fun MovieDetailDataEntity.toMovieDetailDomainModel(): MovieDetailDomainModel {
    return MovieDetailDomainModel(
        id = this.movie.movieId,
        title = this.movie.title,
        genre = parseGenresJson(this.movie.genres),
        popularity = this.movie.popularity,
        releaseDate = this.movie.releaseDate,
        synopsis = this.movie.synopsis,
        status = this.movie.status,
        localCoverFilePath = this.movie.localCoverFilePath,
        videoKey = this.movie.videoKey,
        cast = this.actors.map { actor ->
            MovieActorDomainModel(
                actor.actorId,
                actor.name,
                actor.role
            )
        }
    )
}

fun MovieDetailDomainModel.toMovieUpdated(): MovieUpdatedDataEntity {
    return MovieUpdatedDataEntity(
        movieId = this.id ?: -1,
        releaseDate = this.releaseDate,
        overview = this.synopsis,
        genres = this.genre?.toJson(),
        status = this.status,
        popularity = this.popularity,
        cast = this.cast?.map { actor -> Actor(actor.id, actor.name, actor.role) } ?: emptyList()
    )
}

private fun List<GenreDomainModel>.toJson(): String {
    return Json.encodeToString(this)
}

private fun parseGenresJson(json: String?): List<GenreDomainModel>? {
    if (json.isNullOrBlank()) return null

    return try {
        Json.decodeFromString<List<GenreDomainModel>>(json)
    } catch (e: Exception) {
        Logger.e("Error while parsing genre string DB to genre domain model")
        null
    }
}
