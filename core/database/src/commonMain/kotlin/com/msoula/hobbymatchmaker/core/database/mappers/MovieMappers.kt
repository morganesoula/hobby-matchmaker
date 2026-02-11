package com.msoula.hobbymatchmaker.core.database.mappers

import com.msoula.hobbymatchmaker.core.database.Actor
import com.msoula.hobbymatchmaker.core.database.Movie
import com.msoula.hobbymatchmaker.core.database.ObserveDetailMovie
import com.msoula.hobbymatchmaker.core.database.models.MovieDetailDataEntity

fun ObserveDetailMovie.toMovieDetailDataEntity(list: List<ObserveDetailMovie>): MovieDetailDataEntity =
    MovieDetailDataEntity(
        movie = Movie(
            movieId = movieId, title = this.title,
            posterFileName = this.posterFileName,
            synopsis = this.synopsis,
            releaseDate = this.releaseDate,
            genres = this.genres,
            localCoverFilePath = this.localCoverFilePath,
            isFavorite = this.isFavorite,
            isSeen = null,
            popularity = this.popularity,
            status = this.status,
            videoKey = this.videoKey,
            duration = this.duration,
            note = null,
            has_cast = this.has_cast
        ),
        actors = list
            .filter { it.actorId != null }
            .map {
                Actor(
                    actorId = it.actorId ?: 0L,
                    name = it.actorName,
                    role = it.actorRole
                )
            }
    )
