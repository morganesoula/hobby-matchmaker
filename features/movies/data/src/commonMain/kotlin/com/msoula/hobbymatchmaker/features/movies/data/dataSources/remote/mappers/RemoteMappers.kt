package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.models.MovieLocalDataModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteDataModel

fun MovieRemoteDataModel.toMovieDataModel(): MovieLocalDataModel =
    MovieLocalDataModel(
        id = this.id?.toLong() ?: MovieLocalDataModel.Initial.id,
        title = this.title ?: MovieLocalDataModel.Initial.title,
        overview = null,
        remotePoster = this.poster ?: MovieLocalDataModel.Initial.remotePoster,
        localPoster = MovieLocalDataModel.Initial.localPoster,
        releaseDate = MovieLocalDataModel.Initial.releaseDate,
        genres = MovieLocalDataModel.Initial.genres,
        isFavorite = MovieLocalDataModel.Initial.isFavorite,
        isSeen = MovieLocalDataModel.Initial.isSeen,
        popularity = this.popularity,
        status = MovieLocalDataModel.Initial.status,
        videoKey = MovieLocalDataModel.Initial.videoKey,
        duration = MovieLocalDataModel.Initial.duration,
        note = this.note
    )
