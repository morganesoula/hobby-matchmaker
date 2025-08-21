package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieDetailResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideosResponseRemoteModel

interface MovieDetailRemoteDataSource {
    suspend fun fetchMovieDetail(movieId: Long, language: String):
            R<MovieDetailResponseRemoteModel?, AppError>

    suspend fun fetchMovieCredit(movieId: Long, language: String):
            R<CastResponseRemoteModel?, AppError>

    suspend fun fetchMovieTrailer(movieId: Long, language: String):
            R<MovieVideosResponseRemoteModel?, AppError>
}
