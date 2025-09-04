package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.fakes

import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideoResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideosResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieVideosKtorService

class FakeMovieVideosKtorService : MovieVideosKtorService {

    override suspend fun fetchMovieVideos(
        movie: Long,
        language: String
    ): Result<MovieVideosResponseRemoteModel, MovieDetailDataErrorHMM> {
        return Result.Success(
            MovieVideosResponseRemoteModel(
                results = listOf(
                    MovieVideoResponseRemoteModel(
                        key = "test key 1",
                        site = "YouTube",
                        type = "Trailer"
                    )
                )
            )
        )
    }
}
