package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote

import android.util.Log
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.network.execute
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieActorDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieVideoDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services.MovieDetailService
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services.MovieVideosService
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieVideoDomainModel

class MovieDetailRemoteDataSourceImpl(
    private val movieDetailService: MovieDetailService,
    private val movieVideosService: MovieVideosService
) : MovieDetailRemoteDataSource {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailDomainModel> {
        return execute({
            movieDetailService.fetchMovieDetail(movieId, language)
        })
            .mapSuccess { response ->
                response.toMovieDetailDomainModel()
            }
    }

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): Result<MovieCastDomainModel?> {
        return execute({
            movieDetailService.fetchMovieCredits(movieId, language)
        })
            .mapSuccess { response ->
                response.toMovieActorDomainModel()
            }
    }

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): Result<MovieVideoDomainModel?> {
        return execute({
            movieVideosService.fetchMovieVideos(movieId, language)
        })
            .mapSuccess {
                it.toMovieVideoDomainModel()
            }
            .mapError { error ->
                Log.e("HMM", "Error fetching movie trailer: $error")
                error
            }
    }
}
