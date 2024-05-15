package com.msoula.hobbymatchmaker.features.movies.presentation.mappers

import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel

fun MovieDomainModel.toMovieUiModel(): MovieUiModel {
    return MovieUiModel(
        id = this.id,
        coverFilePath = this.localCoverFilePath,
        isFavorite = this.isFavorite,
        title = this.title
    )
}
