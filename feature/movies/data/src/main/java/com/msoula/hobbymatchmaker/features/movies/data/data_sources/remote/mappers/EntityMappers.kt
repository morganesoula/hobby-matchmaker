package com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.mappers

import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.models.MovieEntityModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel

fun MovieDomainModel.toMovieEntityModel(): MovieEntityModel {
    return MovieEntityModel(
        id = this.id,
        title = this.title,
        posterFileName = this.coverFileName,
        synopsis = this.overview,
        localCoverFilePath = this.localCoverFilePath,
        isFavorite = this.isFavorite,
        isSeen = this.isSeen
    )
}

fun MovieEntityModel.toMovieDomainModel(): MovieDomainModel {
    return MovieDomainModel(
        id = this.id ?: 0L,
        title = this.title ?: "",
        coverFileName = this.posterFileName ?: "",
        localCoverFilePath = this.localCoverFilePath ?: "",
        isFavorite = this.isFavorite ?: false,
        isSeen = this.isSeen ?: false,
        overview = this.synopsis
    )
}
