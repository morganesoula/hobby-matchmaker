package com.msoula.hobbymatchmaker.features.movies.data.dataSources.mappers

import com.msoula.hobbymatchmaker.core.database.Movie
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel

fun MovieRemoteModel.toMovieDB(): Movie {
    return Movie(
        movieId = this.id?.toLong() ?: -1L,
        title = this.title,
        posterFileName = this.poster,
        synopsis = null,
        releaseDate = null,
        genres = null,
        localCoverFilePath = null,
        isFavorite = null,
        isSeen = null,
        popularity = this.popularity,
        status = null,
        videoKey = null,
        duration = null,
        note = this.note
    )
}

fun Movie.toMovieDomainModel(): MovieDomainModel {
    return MovieDomainModel(
        id = this.movieId,
        title = this.title ?: "",
        coverFileName = this.posterFileName ?: "",
        localCoverFilePath = this.localCoverFilePath ?: "",
        isFavorite = this.isFavorite == 1L,
        isSeen = this.isSeen == 1L,
        overview = this.synopsis,
        note = this.note ?: 0.0
    )
}
