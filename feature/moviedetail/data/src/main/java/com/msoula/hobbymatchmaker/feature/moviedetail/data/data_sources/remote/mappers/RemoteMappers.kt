package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers

import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.models.MovieInfoResponseRemoteModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.Genre
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieInfoDomainModel

fun MovieInfoResponseRemoteModel.toMovieInfoDomainModel(): MovieInfoDomainModel {
    return MovieInfoDomainModel(
        id = this.id.toLong(),
        title = this.title,
        genre = this.genres.map { genre -> Genre(genre.id ?: -1, genre.name ?: "") },
        popularity = this.popularity,
        releaseDate = this.releaseDate,
        synopsis = this.overview,
        status = this.status
    )
}

fun CastResponseRemoteModel.toMovieActorDomainModel(): MovieCastDomainModel {
    return MovieCastDomainModel(this.cast?.map {
        MovieActorDomainModel(
            id = it.id.toLong(),
            name = it.name,
            role = it.character
        )
    }.orEmpty())
}
