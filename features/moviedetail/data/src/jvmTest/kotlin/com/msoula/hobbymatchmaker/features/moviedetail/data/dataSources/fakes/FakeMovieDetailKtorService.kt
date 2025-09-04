package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.fakes

import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.ActorResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.GenreResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieDetailResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieCreditsKtorErrorHMM
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieDetailKtorErrorHMM
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieDetailKtorService

class FakeMovieDetailKtorService : MovieDetailKtorService {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailResponseRemoteModel, MovieDetailKtorErrorHMM> {
        return Result.Success(
            MovieDetailResponseRemoteModel(
                id = 1,
                title = "Test movie",
                genres = listOf(GenreResponseRemoteModel(name = "Action")),
                originalTitle = "Original test movie",
                overview = "Random test movie overview",
                popularity = 8.9,
                releaseDate = "2025-07-11",
                status = "Released",
            )
        )
    }

    override suspend fun fetchMovieCredits(
        movieId: Long,
        language: String
    ): Result<CastResponseRemoteModel, MovieCreditsKtorErrorHMM> =
        Result.Success(
            CastResponseRemoteModel(
                cast = listOf(
                    ActorResponseRemoteModel(
                        id = 1,
                        name = "Actor one",
                        character = "Role one"
                    )
                )
            )
        )
}
