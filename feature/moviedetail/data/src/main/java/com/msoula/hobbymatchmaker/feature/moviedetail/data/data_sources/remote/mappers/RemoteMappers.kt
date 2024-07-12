package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers

import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.models.MovieDetailRemoteModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.Genre
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel

fun MovieDetailRemoteModel.toMovieDetailDomainModel(): MovieDetailDomainModel {
    return MovieDetailDomainModel(
        title = this.originalTitle,
        popularity = this.popularity,
        releaseDate = this.releaseDate,
        status = this.status,
        synopsis = this.overview,
        genre = this.genres.map { Genre(it.id ?: -1, it.name ?: "") }
    )
}
