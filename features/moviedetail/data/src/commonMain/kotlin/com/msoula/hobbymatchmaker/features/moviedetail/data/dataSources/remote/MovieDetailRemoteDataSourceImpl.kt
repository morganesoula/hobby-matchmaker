package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.core.common.map
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.mappers.toMovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.mappers.toMovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.mappers.toMovieVideoDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieDetailKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieVideosKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel

class MovieDetailRemoteDataSourceImpl(
    private val movieDetailKtorService: MovieDetailKtorService,
    private val movieVideosKtorService: MovieVideosKtorService
) : MovieDetailRemoteDataSource {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): R<MovieDetailDomainModel, AppError> =
        movieDetailKtorService.fetchMovieDetail(movieId, language)
            .map {
                it.toMovieDetailDomainModel()
            }

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): R<MovieCastDomainModel?, AppError> =
        movieDetailKtorService.fetchMovieCredits(movieId, language)
            .map {
                it.toMovieActorDomainModel()
            }

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): R<MovieVideoDomainModel?, AppError> =
        movieVideosKtorService.fetchMovieVideos(movieId, language)
            .map {
                it.toMovieVideoDomainModel()
            }
}


