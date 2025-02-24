package com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.local.mappers

import com.msoula.hobbymatchmaker.core.database.dao.models.Actor
import com.msoula.hobbymatchmaker.core.database.dao.models.Genre
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieEntityModel
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieWithActors
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.GenreDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel

fun MovieWithActors.toMovieDetailDomainModel(): MovieDetailDomainModel {
    return MovieDetailDomainModel(
        id = this.movie.movieId,
        title = this.movie.title,
        genre = this.movie.genres?.map { genre ->
            GenreDomainModel(
                genre.genreId.toInt(),
                genre.name
            )
        },
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

fun MovieDetailDomainModel.toMovieWithActors(): MovieWithActors {
    return MovieWithActors(
        movie = MovieEntityModel(
            movieId = this.id ?: 0L,
            title = this.title,
            synopsis = this.synopsis,
            releaseDate = this.releaseDate,
            genres = this.genre?.map { genre ->
                Genre(
                    genre.id?.toLong() ?: 0L,
                    genre.name
                )
            },
            popularity = this.popularity,
            status = this.status,
            videoKey = this.videoKey
        ),
        actors = this.cast?.map { actor ->
            Actor(actor.id, actor.name, actor.role)
        } ?: emptyList()
    )
}
