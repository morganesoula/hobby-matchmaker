package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers

import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.models.Actor
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.models.Genre
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.models.MovieEntityModel

fun MovieDetailDomainModel.toMovieEntityModel(): MovieEntityModel {
    return MovieEntityModel(
        id = this.id,
        title = this.title,
        genres = this.genre?.map { genre ->
            Genre(
                id = genre.id,
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
                id = actor.id,
                name = actor.name,
                role = actor.role
            )
        }

    )
}

fun MovieEntityModel.toMovieDetailDomainModel(): MovieDetailDomainModel {
    return MovieDetailDomainModel(
        id = this.id,
        title = this.title,
        genre = this.genres?.map { genre ->
            com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.Genre(
                id = genre.id,
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
                id = actor.id,
                name = actor.name
            )
        }
    )
}
