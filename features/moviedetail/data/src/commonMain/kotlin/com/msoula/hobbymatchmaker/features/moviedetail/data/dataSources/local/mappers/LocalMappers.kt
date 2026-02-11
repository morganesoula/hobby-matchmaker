package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.mappers

import com.msoula.hobbymatchmaker.core.database.Actor
import com.msoula.hobbymatchmaker.core.database.models.MovieDetailDataEntity
import com.msoula.hobbymatchmaker.core.database.models.MovieUpdatedDataEntity
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.models.ActorDataModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.models.MovieDetailDataModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.models.UpdatedMovieDetailDataModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.GenreDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel

fun MovieDetailDomainModel.toUpdatedMovieDetailDataModel(): UpdatedMovieDetailDataModel =
    UpdatedMovieDetailDataModel(
        id = this.id,
        releaseDate = this.releaseDate,
        overview = this.synopsis,
        genres = this.genre.joinToString(",") { it.name },
        status = this.status,
        popularity = this.popularity,
        cast = this.cast.map { it.toActorDataModel() },
        duration = this.duration,
        hasCast = this.hasCast
    )

fun MovieDetailDataModel.toMovieDetailDomainModel(): MovieDetailDomainModel =
    MovieDetailDomainModel(
        id = this.id,
        title = this.title,
        isFavorite = this.isFavorite,
        genre = this.genres.map { genre -> GenreDomainModel(name = genre) },
        popularity = this.popularity ?: MovieDetailDomainModel.Initial.popularity,
        releaseDate = this.releaseDate,
        synopsis = this.overview,
        status = this.status ?: MovieDetailDomainModel.Initial.status,
        localCoverFilePath = this.poster,
        coverFileName = this.poster,
        videoKey = this.videoKey ?: MovieDetailDomainModel.Initial.videoKey,
        cast = this.actors?.map { it.toMovieActorDomainModel() }
            ?: MovieDetailDomainModel.Initial.cast,
        duration = this.duration?.toInt() ?: MovieDetailDomainModel.Initial.duration,
        hasCast = this.hasCast

    )

fun MovieDetailDataEntity.toMovieDetailDataModel(): MovieDetailDataModel =
    MovieDetailDataModel(
        id = this.movie.movieId,
        title = this.movie.title ?: MovieDetailDataModel.Initial.title,
        overview = this.movie.synopsis ?: MovieDetailDataModel.Initial.overview,
        poster = this.movie.posterFileName ?: MovieDetailDataModel.Initial.poster,
        releaseDate = this.movie.releaseDate ?: MovieDetailDataModel.Initial.releaseDate,
        genres = this.movie.genres?.split(",") ?: emptyList(),
        isFavorite = this.movie.isFavorite == 1L,
        isSeen = this.movie.isSeen == 1L,
        popularity = this.movie.popularity,
        status = this.movie.status,
        videoKey = this.movie.videoKey,
        duration = this.movie.duration,
        note = this.movie.note,
        actors = this.actors.map { it.toActorDataModel() },
        hasCast = this.movie.has_cast == 1L
    )

fun UpdatedMovieDetailDataModel.toMovieUpdatedDetailDataEntity(): MovieUpdatedDataEntity =
    MovieUpdatedDataEntity(
        movieId = this.id,
        releaseDate = this.releaseDate,
        overview = this.overview,
        genres = this.genres,
        status = this.status,
        popularity = this.popularity,
        cast = this.cast.map { it.toActor() },
        duration = this.duration,
        hasCast = this.hasCast
    )

fun ActorDataModel.toActor(): Actor =
    Actor(
        actorId = this.id,
        name = this.name,
        role = this.role
    )

fun Actor.toActorDataModel(): ActorDataModel =
    ActorDataModel(
        id = this.actorId,
        name = this.name ?: ActorDataModel.Initial.name,
        role = this.role ?: ActorDataModel.Initial.role
    )

fun ActorDataModel.toMovieActorDomainModel(): MovieActorDomainModel =
    MovieActorDomainModel(
        id = this.id,
        name = this.name,
        role = this.role
    )

fun MovieActorDomainModel.toActorDataModel(): ActorDataModel =
    ActorDataModel(
        id = this.id,
        name = this.name,
        role = this.role
    )
