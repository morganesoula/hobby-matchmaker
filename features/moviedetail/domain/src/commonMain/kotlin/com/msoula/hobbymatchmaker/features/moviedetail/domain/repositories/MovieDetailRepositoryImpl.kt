package com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.core.common.map
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.common.safeCall
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import kotlinx.coroutines.flow.Flow

class MovieDetailRepositoryImpl(
    private val movieDetailRemoteDataSource: MovieDetailRemoteDataSource,
    private val movieDetailLocalDataSource: MovieDetailLocalDataSource
) : MovieDetailRepository {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): R<MovieDetailDomainModel?, AppError> =
        movieDetailRemoteDataSource
            .fetchMovieDetail(movieId, language)
            .onSuccess { detail ->
                Logger.d("FetchMovieDetail - repo - id: ${detail?.id}")
            }

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): R<List<MovieActorDomainModel>?, AppError> =
        movieDetailRemoteDataSource
            .fetchMovieCredit(movieId, language)
            .map { castModel -> castModel?.cast }

    override suspend fun saveMovieDetail(movieDetailDomainModel: MovieDetailDomainModel) {
        movieDetailLocalDataSource.saveMovieDetail(movieDetailDomainModel)
    }

    override suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> {
        return movieDetailLocalDataSource.observeMovieDetail(movieId)
    }

    override suspend fun updateMovieVideoURI(
        movieId: Long,
        videoURI: String
    ): R<Boolean, AppError> =
        safeCall {
            movieDetailLocalDataSource.updateMovieVideoUri(movieId, videoURI)
            true
        }

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): R<MovieVideoDomainModel?, AppError> =
        movieDetailRemoteDataSource.fetchMovieTrailer(movieId, language)
}

