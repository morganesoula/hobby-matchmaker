package com.msoula.hobbymatchmaker.features.movies.presentation.mappers

import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel.Companion.DEFAULT_COVER_FILE_NAME
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel.Companion.DEFAULT_IS_SEEN
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel.Companion.DEFAULT_TITLE
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel

fun MovieDomainModel.toMovieUiModel(): MovieUiModel {
    return MovieUiModel(
        id = this.id,
        coverFilePath = this.localCoverFilePath,
        isFavorite = this.isFavorite
    )
}

fun MovieUiModel.toMovieDomainModel(): MovieDomainModel {
    return MovieDomainModel(
        id = this.id,
        title = DEFAULT_TITLE,
        coverFileName = DEFAULT_COVER_FILE_NAME,
        localCoverFilePath = this.coverFilePath,
        isFavorite = this.isFavorite,
        isSeen = DEFAULT_IS_SEEN
    )
}
