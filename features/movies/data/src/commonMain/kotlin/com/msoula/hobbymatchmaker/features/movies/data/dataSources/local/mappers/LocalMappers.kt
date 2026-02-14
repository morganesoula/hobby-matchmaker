package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.mappers

import com.msoula.hobbymatchmaker.core.database.Movie
import com.msoula.hobbymatchmaker.core.database.models.FavoriteMovieDataEntity
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.models.FavoriteMovieLocalDataModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.models.MovieLocalDataModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.FavoriteMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel

fun Movie.toMovieDataModel(): MovieLocalDataModel =
    MovieLocalDataModel(
        id = this.movieId,
        title = this.title ?: MovieLocalDataModel.Initial.title,
        overview = this.synopsis ?: "",
        localPoster = this.localCoverFilePath ?: MovieLocalDataModel.Initial.localPoster,
        remotePoster = this.posterFileName ?: MovieLocalDataModel.Initial.remotePoster,
        releaseDate = this.releaseDate ?: "",
        genres = this.genres?.split(",") ?: emptyList(),
        isFavorite = this.isFavorite == 1L,
        isSeen = this.isSeen == 1L,
        popularity = this.popularity,
        status = this.status,
        videoKey = this.videoKey,
        duration = this.duration,
        note = this.note
    )

fun MovieLocalDataModel.toMovie(): Movie =
    Movie(
        movieId = this.id,
        title = this.title,
        posterFileName = this.remotePoster,
        localCoverFilePath = this.localPoster,
        synopsis = this.overview,
        releaseDate = this.releaseDate,
        genres = this.genres.joinToString(","),
        isFavorite = if (this.isFavorite) 1L else 0L,
        isSeen = if (this.isSeen) 1L else 0L,
        popularity = this.popularity,
        status = this.status,
        videoKey = this.videoKey,
        duration = this.duration,
        note = this.note,
        has_cast = 0L
    )

fun MovieLocalDataModel.toMovieDomainModel(): MovieDomainModel =
    MovieDomainModel(
        id = this.id,
        title = this.title,
        coverFileName = this.remotePoster,
        localCoverFilePath = this.localPoster ?: MovieDomainModel.Initial.localCoverFilePath,
        isFavorite = this.isFavorite,
        isSeen = this.isSeen,
        overview = this.overview,
        note = this.note ?: MovieDomainModel.Initial.note
    )

fun FavoriteMovieDataEntity.toFavoriteMovieDataModel(): FavoriteMovieLocalDataModel =
    FavoriteMovieLocalDataModel(
        id = this.id,
        title = this.title,
        posterPath = this.posterLocalPath,
        releaseDate = this.releaseDate
    )

fun FavoriteMovieLocalDataModel.toFavoriteMovieDomainModel(): FavoriteMovieDomainModel =
    FavoriteMovieDomainModel(
        id = this.id,
        title = this.title,
        releaseDate = this.releaseDate,
        posterPath = this.posterPath
    )
