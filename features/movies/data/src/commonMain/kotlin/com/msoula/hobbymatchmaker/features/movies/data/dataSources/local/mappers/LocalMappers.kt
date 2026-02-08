package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.mappers

import com.msoula.hobbymatchmaker.core.database.Movie
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.models.MovieDataModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlin.collections.emptyList

fun Movie.toMovieDataModel(): MovieDataModel =
    MovieDataModel(
        id = this.movieId,
        title = this.title ?: MovieDataModel.Initial.title,
        overview = this.synopsis ?: "",
        localPoster = this.localCoverFilePath ?: MovieDataModel.Initial.localPoster,
        remotePoster = this.posterFileName ?: MovieDataModel.Initial.remotePoster,
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

fun MovieDataModel.toMovie(): Movie =
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
        note = this.note
    )

fun MovieDataModel.toMovieDomainModel(): MovieDomainModel =
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
