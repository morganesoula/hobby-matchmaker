package com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieDetailRepository {

    suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): R<MovieDetailDomainModel?, AppError>

    suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): R<List<MovieActorDomainModel>?, AppError>

    fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?>

    suspend fun saveMovieDetail(movieDetailDomainModel: MovieDetailDomainModel): R<Unit, AppError>

    suspend fun updateMovieVideoURI(
        movieId: Long,
        videoURI: String
    ): R<Unit, AppError>

    suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): R<MovieVideoDomainModel?, AppError>
}
