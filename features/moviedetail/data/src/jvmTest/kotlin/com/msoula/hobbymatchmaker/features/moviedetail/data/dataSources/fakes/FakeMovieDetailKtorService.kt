package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.fakes

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieDetailResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieDetailKtorService

class FakeMovieDetailKtorService : MovieDetailKtorService {

    var resultDetail: AppResult<MovieDetailResponseRemoteModel, AppError> =
        AppResult.Failure(AppError.Network.Unknown())
    var resultCredits: AppResult<CastResponseRemoteModel, AppError> =
        AppResult.Failure(AppError.Network.Unknown())

    var lastDetailMovieId: Long? = null
    var lastDetailLanguage: String? = null
    var lastCreditsMovieId: Long? = null
    var lastCreditsLanguage: String? = null

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): AppResult<MovieDetailResponseRemoteModel, AppError> {
        lastDetailMovieId = movieId
        lastDetailLanguage = language
        return resultDetail
    }

    override suspend fun fetchMovieCredits(
        movieId: Long,
        language: String
    ): AppResult<CastResponseRemoteModel, AppError> {
        lastCreditsMovieId = movieId
        lastCreditsLanguage = language
        return resultCredits
    }
}
