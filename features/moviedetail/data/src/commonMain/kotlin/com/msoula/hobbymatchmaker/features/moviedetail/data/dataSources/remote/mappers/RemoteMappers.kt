package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieDetailResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideosResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.GenreDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel

fun MovieDetailResponseRemoteModel.toMovieDetailDomainModel(): MovieDetailDomainModel =
    MovieDetailDomainModel(
        id = this.id.toLong(),
        title = this.title,
        genre = this.genres.map { genre -> GenreDomainModel(genre.id ?: -1, genre.name ?: "") },
        popularity = this.popularity,
        releaseDate = this.releaseDate,
        synopsis = this.overview,
        status = this.status,
        duration = this.duration
    )

fun CastResponseRemoteModel.toMovieActorDomainModel(): MovieCastDomainModel {
    return MovieCastDomainModel(this.cast?.map {
        MovieActorDomainModel(
            id = it.id.toLong(),
            name = it.name,
            role = it.character
        )
    }.orEmpty())
}

fun MovieVideosResponseRemoteModel.toMovieVideoDomainModel(): MovieVideoDomainModel? {
    val trailer = results.firstOrNull {
        it.site.equals("YouTube", ignoreCase = true) &&
            it.type.equals("Trailer", ignoreCase = true)
    }

    return trailer?.let {
        MovieVideoDomainModel(
            key = it.key,
            type = it.type,
            site = it.site
        )
    }
}
