package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.local.mappers

import com.msoula.hobbymatchmaker.core.database.dao.models.ActorEntityModel
import com.msoula.hobbymatchmaker.core.database.dao.models.MovieEntityModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.Genre
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieInfoDomainModel


fun MovieEntityModel.toMovieInfoDomainModel(): MovieInfoDomainModel {
    return MovieInfoDomainModel(
        id = this.movieId,
        title = this.title,
        synopsis = this.overview,
        posterPath = this.localPosterPath,
        genre = this.genre?.toListGenre(),
        releaseDate = this.releaseDate,
        popularity = this.popularity,
        status = this.status,
        videoKey = this.videoUri
    )
}

fun ActorEntityModel.toMovieActorDomainModel(): MovieActorDomainModel {
    return MovieActorDomainModel(
        id = this.actorId,
        name = this.name,
        role = this.role
    )
}

fun MovieActorDomainModel.toActorEntityModel(): ActorEntityModel {
    return ActorEntityModel(
        actorId = this.id,
        name = this.name ?: "",
        role = this.role ?: ""
    )
}

fun List<ActorEntityModel>.toMovieCastDomainModel(): MovieCastDomainModel {
    return MovieCastDomainModel(
        this.map { it.toMovieActorDomainModel() }
    )
}

private fun List<String>.toListGenre(): List<Genre> {
    return this.map { genreName ->
        Genre(id = Genre.DEFAULT_ID, name = genreName)
    }
}
