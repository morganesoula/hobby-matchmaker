package com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models

import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel

data class MovieDetailUiModel(
    val title: String = ""
)

fun MovieDetailDomainModel.toMovieDetailUiModel(): MovieDetailUiModel {
    return MovieDetailUiModel(
        title = this.title ?: MovieDetailDomainModel.DEFAULT_TITLE
    )
}
