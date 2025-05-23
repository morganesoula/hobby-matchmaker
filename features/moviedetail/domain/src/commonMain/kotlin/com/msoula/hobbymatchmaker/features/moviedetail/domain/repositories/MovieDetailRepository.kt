package com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.UpdateMovieTrailerLocalError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieDetailRepository {

    suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailDomainModel, MovieDetailDomainError>

    suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): Result<List<MovieActorDomainModel>?, MovieDetailDomainError>

    suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?>

    suspend fun saveMovieDetail(movieDetailDomainModel: MovieDetailDomainModel)

    suspend fun updateMovieVideoURI(
        movieId: Long,
        videoURI: String
    ): Result<Boolean, UpdateMovieTrailerLocalError>

    suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): Result<MovieVideoDomainModel?, MovieDetailDomainError>
}
