package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.fakes

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideoResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideosResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieVideosKtorService

class FakeMovieVideosKtorService : MovieVideosKtorService {

    var resultVideos: AppResult<MovieVideosResponseRemoteModel, AppError> =
        AppResult.Failure(AppError.Network.Unknown())

    var lastMovieId: Long? = null
    var lastLanguage: String? = null

    override suspend fun fetchMovieVideos(
        movie: Long,
        language: String
    ): AppResult<MovieVideosResponseRemoteModel, AppError> {
        lastMovieId = movie
        lastLanguage = language
        return resultVideos
    }
}
