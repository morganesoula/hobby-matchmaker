package com.msoula.hobbymatchmaker.features.movies.data.dataSources.mappers

import com.msoula.hobbymatchmaker.core.database.Movie
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel

fun MovieDomainModel.toMovieDB(): Movie {
    return Movie(
        movieId = this.id,
        title = this.title,
        posterFileName = this.coverFileName,
        localCoverFilePath = this.localCoverFilePath,
        isFavorite = if (this.isFavorite) 1 else 0,
        isSeen = if (this.isSeen) 1 else 0,
        synopsis = this.overview,
        genres = null,
        releaseDate = null,
        popularity = null,
        status = null,
        videoKey = null
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
        overview = this.synopsis
    )
}
