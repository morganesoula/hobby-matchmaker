package com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.UpdateMovieTrailerLocalError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeMovieDetailRepository(
    private val movieDetailFlow: Flow<MovieDetailDomainModel?> = emptyFlow(),
    private val fetchDetailResult: Result<MovieDetailDomainModel, MovieDetailDomainError> = Result.Failure(
        MovieDetailDomainError.MovieDetailError("Default")
    ),
    private val fetchCreditResult: Result<List<MovieActorDomainModel>?, MovieDetailDomainError> = Result.Success(
        emptyList()
    ),
    private val fetchTrailerResult: Result<MovieVideoDomainModel?, MovieDetailDomainError> = Result.Success(
        null
    ),
    private val updateTrailerResult: Result<Boolean, UpdateMovieTrailerLocalError> = Result.Success(
        true
    )
) : MovieDetailRepository {

    var savedMovie: MovieDetailDomainModel? = null
    var updatedVideoURI: String? = null

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailDomainModel, MovieDetailDomainError> = fetchDetailResult

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): Result<List<MovieActorDomainModel>?, MovieDetailDomainError> = fetchCreditResult

    override suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> =
        movieDetailFlow

    override suspend fun saveMovieDetail(movieDetailDomainModel: MovieDetailDomainModel) {
        savedMovie = movieDetailDomainModel
    }

    override suspend fun updateMovieVideoURI(
        movieId: Long,
        videoURI: String
    ): Result<Boolean, UpdateMovieTrailerLocalError> {
        updatedVideoURI = videoURI
        return updateTrailerResult
    }

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): Result<MovieVideoDomainModel?, MovieDetailDomainError> = fetchTrailerResult
}
