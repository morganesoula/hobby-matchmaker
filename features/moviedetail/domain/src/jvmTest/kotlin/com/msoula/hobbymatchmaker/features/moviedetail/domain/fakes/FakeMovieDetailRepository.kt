package com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainErrorHMM
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.UpdateMovieTrailerLocalErrorHMM
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeMovieDetailRepository(
    private val movieDetailFlow: Flow<MovieDetailDomainModel?> = emptyFlow(),
    private val fetchDetailResult: Result<MovieDetailDomainModel, MovieDetailDomainErrorHMM> = Result.Failure(
        MovieDetailDomainErrorHMM.MovieDetailErrorHMM("Default")
    ),
    private val fetchCreditResult: Result<List<MovieActorDomainModel>?, MovieDetailDomainErrorHMM> = Result.Success(
        emptyList()
    ),
    private val fetchTrailerResult: Result<MovieVideoDomainModel?, MovieDetailDomainErrorHMM> = Result.Success(
        null
    ),
    private val updateTrailerResult: Result<Boolean, UpdateMovieTrailerLocalErrorHMM> = Result.Success(
        true
    )
) : MovieDetailRepository {

    var savedMovie: MovieDetailDomainModel? = null
    var updatedVideoURI: String? = null

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailDomainModel, MovieDetailDomainErrorHMM> = fetchDetailResult

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): Result<List<MovieActorDomainModel>?, MovieDetailDomainErrorHMM> = fetchCreditResult

    override suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> =
        movieDetailFlow

    override suspend fun saveMovieDetail(movieDetailDomainModel: MovieDetailDomainModel) {
        savedMovie = movieDetailDomainModel
    }

    override suspend fun updateMovieVideoURI(
        movieId: Long,
        videoURI: String
    ): Result<Boolean, UpdateMovieTrailerLocalErrorHMM> {
        updatedVideoURI = videoURI
        return updateTrailerResult
    }

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): Result<MovieVideoDomainModel?, MovieDetailDomainErrorHMM> = fetchTrailerResult
}
