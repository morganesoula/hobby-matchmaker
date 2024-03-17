package com.msoula.hobbymatchmaker.features.movies.data.data_sources.local.mappers

import com.msoula.hobbymatchmaker.core.dao.models.MovieEntityModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel

fun MovieDomainModel.toMovieEntityModel(): MovieEntityModel {
    return MovieEntityModel(
        id = this.id,
        title = this.title,
        remotePosterPath = this.coverFileName,
        localPosterPath = this.localCoverFilePath,
        favourite = this.isFavorite,
        seen = this.isSeen,
    )
}

fun MovieEntityModel.toMovieDomainModel(): MovieDomainModel {
    return MovieDomainModel(
        id = this.id,
        title = this.title,
        coverFileName = this.remotePosterPath,
        localCoverFilePath = this.localPosterPath,
        isSeen = this.seen,
        isFavorite = this.favourite
    )
}


