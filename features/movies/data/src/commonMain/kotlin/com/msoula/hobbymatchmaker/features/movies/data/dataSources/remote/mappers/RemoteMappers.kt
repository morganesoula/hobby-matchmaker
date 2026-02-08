package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.models.MovieDataModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteModel

fun MovieRemoteModel.toMovieDataModel(): MovieDataModel =
    MovieDataModel(
        id = this.id?.toLong() ?: MovieDataModel.Initial.id,
        title = this.title ?: MovieDataModel.Initial.title,
        overview = MovieDataModel.Initial.overview,
        remotePoster = this.poster ?: MovieDataModel.Initial.remotePoster,
        localPoster = MovieDataModel.Initial.localPoster,
        releaseDate = MovieDataModel.Initial.releaseDate,
        genres = MovieDataModel.Initial.genres,
        isFavorite = MovieDataModel.Initial.isFavorite,
        isSeen = MovieDataModel.Initial.isSeen,
        popularity = this.popularity,
        status = MovieDataModel.Initial.status,
        videoKey = MovieDataModel.Initial.videoKey,
        duration = MovieDataModel.Initial.duration,
        note = this.note
    )
