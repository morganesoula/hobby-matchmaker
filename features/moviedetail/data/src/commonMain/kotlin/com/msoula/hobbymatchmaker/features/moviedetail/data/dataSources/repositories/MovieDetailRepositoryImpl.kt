package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.mappers.toMovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.mappers.toUpdatedMovieDetailDataModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.mappers.toMovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.mappers.toMovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.mappers.toMovieVideoDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieDetailRepositoryImpl(
    private val movieDetailRemoteDataSource: MovieDetailRemoteDataSource,
    private val movieDetailLocalDataSource: MovieDetailLocalDataSource
) : MovieDetailRepository {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): AppResult<MovieDetailDomainModel, AppError> =
        movieDetailRemoteDataSource
            .fetchMovieDetail(movieId, language)
            .onSuccess { detail ->
                Logger.d("FetchMovieDetail - repo - id: ${detail?.id}")
            }
            .flatMap { movie ->
                movie
                    ?.toMovieDetailDomainModel()
                    ?.let { AppResult.Success(it) }
                    ?: AppResult.Failure(AppError.Domain.NotFound)
            }

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): AppResult<List<MovieActorDomainModel>, AppError> =
        movieDetailRemoteDataSource
            .fetchMovieCredit(movieId, language)
            .flatMap { list ->
                list
                    ?.toMovieActorDomainModel()?.cast
                    ?.let { AppResult.Success(it) }
                    ?: AppResult.Failure(AppError.Domain.NotFound)
            }

    override suspend fun saveMovieDetail(movieDetailDomainModel: MovieDetailDomainModel): AppResult<Unit, AppError> =
        movieDetailLocalDataSource.saveMovieDetail(movieDetailDomainModel.toUpdatedMovieDetailDataModel())

    override fun observeMovieDetail(movieId: Long): Flow<AppResult<MovieDetailDomainModel, AppError>> =
        movieDetailLocalDataSource.observeMovieDetail(movieId).map { detail ->
            detail?.let {
                AppResult.Success(it.toMovieDetailDomainModel())
            } ?: AppResult.Failure(AppError.Domain.NotFound)
        }

    override suspend fun updateMovieVideoURI(
        movieId: Long,
        videoURI: String
    ): AppResult<Unit, AppError> =
        movieDetailLocalDataSource.updateMovieVideoUri(movieId, videoURI)

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): AppResult<MovieVideoDomainModel, AppError> =
        movieDetailRemoteDataSource.fetchMovieTrailer(movieId, language)
            .flatMap { movie ->
                movie
                    ?.toMovieVideoDomainModel()
                    ?.let { AppResult.Success(it) }
                    ?: AppResult.Failure(AppError.Domain.NotFound)
            }
}
