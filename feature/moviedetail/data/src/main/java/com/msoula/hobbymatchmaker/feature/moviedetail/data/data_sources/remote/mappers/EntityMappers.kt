package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers

import com.msoula.hobbymatchmaker.core.database.dao.models.Actor
import com.msoula.hobbymatchmaker.core.database.dao.models.Genre
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieEntityModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel

fun MovieDetailDomainModel.toMovieEntityModel(): MovieEntityModel {
    return MovieEntityModel(
        movieId = this.id ?: 0L,
        title = this.title,
        genres = this.genre?.map { genre ->
            Genre(
                genreId = genre.id?.toLong() ?: 0L,
                name = genre.name
            )

        },
        popularity = this.popularity,
        releaseDate = this.releaseDate,
        synopsis = this.synopsis,
        status = this.status,
        localCoverFilePath = this.localCoverFilePath,
        videoKey = this.videoKey,
        cast = this.cast?.map { actor ->
            Actor(
                actorId = actor.id,
                name = actor.name,
                role = actor.role
            )
        }

    )
}

fun MovieEntityModel.toMovieDetailDomainModel(): MovieDetailDomainModel {
    return MovieDetailDomainModel(
        id = this.movieId,
        title = this.title,
        genre = this.genres?.map { genre ->
            com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.GenreDomainModel(
                id = genre.genreId.toInt(),
                name = genre.name
            )
        },
        popularity = this.popularity,
        releaseDate = this.releaseDate,
        synopsis = this.synopsis,
        status = this.status,
        localCoverFilePath = this.localCoverFilePath,
        videoKey = this.videoKey,
        cast = this.cast?.map { actor ->
            MovieActorDomainModel(
                id = actor.actorId,
                name = actor.name
            )
        }
    )
}
