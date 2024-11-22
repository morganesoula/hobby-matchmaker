package com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.fakes

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.errors.FetchMovieTrailerRemoteError
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.errors.UpdateMovieTrailerLocalError
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieVideoDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepository
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.ObserveMovieErrors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMovieDetailRepository : MovieDetailRepository {

    private var movie: MutableState<MovieDetailDomainModel?> =
        mutableStateOf(MovieDetailDomainModel())

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailDomainModel, AppError> {
        val fetchedMovie = MovieDetailDomainModel(
            id = movie.value?.id,
            title = "Test title",
            synopsis = "Test synopsis"
        )
        return if (fetchedMovie.id == 2L) {
            Result.Failure(ObserveMovieErrors.Error("Intentional error while fetching fake data"))
        } else {
            Result.Success(fetchedMovie)
        }
    }

    override suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> {
        return flow {
            emit(movie.value)
        }
    }

    override suspend fun updateMovieVideoURI(
        movieId: Long,
        videoURI: String
    ): Result<Boolean, UpdateMovieTrailerLocalError> {
        movie.value?.videoKey = videoURI
        return Result.Success(true)
    }

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): Result<MovieVideoDomainModel?, FetchMovieTrailerRemoteError> {
        return if (movie.value?.id == 1L) {
            Result.Success(MovieVideoDomainModel("testKey", "video", "youtube"))
        } else if (movie.value?.id == 50L) {
            Result.Success(null)
        } else {
            Result.Failure(FetchMovieTrailerRemoteError("Intentional error while fetching fake data"))
        }
    }

    fun setMovie(movie: MovieDetailDomainModel?) {
        this.movie.value = movie
    }
}
