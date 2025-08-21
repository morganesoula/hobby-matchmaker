package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieDetailResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideosResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieDetailKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieVideosKtorService

class MovieDetailRemoteDataSourceImpl(
    private val movieDetailKtorService: MovieDetailKtorService,
    private val movieVideosKtorService: MovieVideosKtorService
) : MovieDetailRemoteDataSource {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): R<MovieDetailResponseRemoteModel, AppError> =
        movieDetailKtorService.fetchMovieDetail(movieId, language)

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): R<CastResponseRemoteModel?, AppError> =
        movieDetailKtorService.fetchMovieCredits(movieId, language)

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): R<MovieVideosResponseRemoteModel?, AppError> =
        movieVideosKtorService.fetchMovieVideos(movieId, language)
}


