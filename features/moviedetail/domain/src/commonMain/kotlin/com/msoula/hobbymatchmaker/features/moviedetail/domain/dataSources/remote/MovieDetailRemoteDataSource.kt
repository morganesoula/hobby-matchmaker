package com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel

interface MovieDetailRemoteDataSource {
    suspend fun fetchMovieDetail(movieId: Long, language: String):
        R<MovieDetailDomainModel?, AppError>

    suspend fun fetchMovieCredit(movieId: Long, language: String):
        R<MovieCastDomainModel?, AppError>

    suspend fun fetchMovieTrailer(movieId: Long, language: String):
        R<MovieVideoDomainModel?, AppError>
}
