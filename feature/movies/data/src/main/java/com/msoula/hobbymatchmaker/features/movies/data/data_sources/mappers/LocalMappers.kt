package com.msoula.hobbymatchmaker.features.movies.data.data_sources.mappers

import com.msoula.hobbymatchmaker.core.database.dao.models.MovieEntityModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel

fun MovieDomainModel.toMovieEntityModel(): MovieEntityModel {
    return MovieEntityModel(
        movieId = this.id,
        title = this.title,
        posterFileName = this.coverFileName,
        localCoverFilePath = this.localCoverFilePath,
        isFavorite = this.isFavorite,
        isSeen = this.isSeen
    )
}

fun MovieEntityModel.toMovieDomainModel(): MovieDomainModel {
    return MovieDomainModel(
        id = this.movieId,
        title = this.title ?: "",
        coverFileName = this.posterFileName ?: "",
        localCoverFilePath = this.localCoverFilePath ?: "",
        isFavorite = this.isFavorite ?: false,
        isSeen = this.isSeen ?: false,
        overview = this.synopsis
    )
}
