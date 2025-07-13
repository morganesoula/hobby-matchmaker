package com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories

import com.msoula.hobbymatchmaker.core.common.ExternalServiceError
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.UpdateMovieTrailerLocalError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes.FakeMovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes.FakeMovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class MovieDetailRepositoryImplTest : FunSpec({
    val dispatcher = StandardTestDispatcher()

    context("FetchMovieDetail - Success") {
        test("return Success when valid MovieDetailRemoteModel") {
            val fakeLocalDataSource = FakeMovieDetailLocalDataSource()
            val fakeRemoteDataSource = FakeMovieDetailRemoteDataSource()

            val repository = MovieDetailRepositoryImpl(
                movieDetailRemoteDataSource = fakeRemoteDataSource,
                movieDetailLocalDataSource = fakeLocalDataSource
            )

            runTest(dispatcher) {
                val result = repository
                    .fetchMovieDetail(1L, "en")

                result shouldBe Result.Success(MovieDetailDomainModel())
            }
        }
    }

    context("FetchMovieDetail - Failure") {
        test("returns Failure with EmptyDataError when remote returns null data") {
            val fakeLocalDataSource = FakeMovieDetailLocalDataSource()
            val fakeRemoteDataSource = FakeMovieDetailRemoteDataSource(
                fetchMovieDetailResult = Result.Failure(
                    MovieDetailDomainError.MovieDetailError("No data found")
                )
            )

            val repository = MovieDetailRepositoryImpl(
                movieDetailRemoteDataSource = fakeRemoteDataSource,
                movieDetailLocalDataSource = fakeLocalDataSource
            )

            runTest(dispatcher) {
                val result = repository.fetchMovieDetail(1L, "en")

                result shouldBe Result.Failure(
                    MovieDetailDomainError.MovieDetailError(
                        "No data found"
                    )
                )
            }
        }

        test("returns Failure with ExternalServiceError on exception") {
            val fakeLocalDataSource = FakeMovieDetailLocalDataSource()
            val fakeRemoteDataSource = object : MovieDetailRemoteDataSource {
                override suspend fun fetchMovieCredit(
                    movieId: Long,
                    language: String
                ): Result<MovieCastDomainModel?, MovieDetailDomainError> {
                    throw RuntimeException("Remote error")
                }

                override suspend fun fetchMovieDetail(
                    movieId: Long,
                    language: String
                ): Result<MovieDetailDomainModel?, MovieDetailDomainError> =
                    Result.Success(null)

                override suspend fun fetchMovieTrailer(
                    movieId: Long,
                    language: String
                ): Result<MovieVideoDomainModel?, MovieDetailDomainError> =
                    Result.Success(null)
            }

            val repository = MovieDetailRepositoryImpl(
                movieDetailRemoteDataSource = fakeRemoteDataSource,
                movieDetailLocalDataSource = fakeLocalDataSource
            )

            runTest(dispatcher) {
                val result = repository.fetchMovieCredit(1L, "en")

                result.shouldBeInstanceOf<Result.Failure>()
                val error = result.error

                error.shouldBeInstanceOf<ExternalServiceError>()
                error.message shouldBe "Remote error"
            }
        }
    }

    context("FetchMovieCredit - Success") {
        test("returns Success with cast list when remote returns valid data") {
            val dummyCast: List<MovieActorDomainModel> = listOf(
                MovieActorDomainModel(1L, "Name one", "Actor")
            )

            val fakeLocalDataSource = FakeMovieDetailLocalDataSource()
            val fakeRemoteDataSource = FakeMovieDetailRemoteDataSource(
                fetchMovieDetailCreditResult = Result.Success(
                    MovieCastDomainModel(cast = dummyCast)
                )
            )

            val repository = MovieDetailRepositoryImpl(
                movieDetailRemoteDataSource = fakeRemoteDataSource,
                movieDetailLocalDataSource = fakeLocalDataSource
            )

            runTest(dispatcher) {
                val result = repository
                    .fetchMovieCredit(1L, "en")

                result shouldBe Result.Success(dummyCast)
            }
        }
    }

    context("FetchMovieCredit - Failure") {
        test("returns Failure when remote returns error") {
            val fakeLocalDataSource = FakeMovieDetailLocalDataSource()
            val fakeRemoteDataSource = FakeMovieDetailRemoteDataSource(
                fetchMovieDetailCreditResult = Result.Failure(
                    MovieDetailDomainError.CreditError("Error with credit")
                )
            )

            val repository = MovieDetailRepositoryImpl(
                movieDetailRemoteDataSource = fakeRemoteDataSource,
                movieDetailLocalDataSource = fakeLocalDataSource
            )

            runTest(dispatcher) {
                val result = repository.fetchMovieCredit(1L, "en")

                result shouldBe Result.Failure(MovieDetailDomainError.CreditError("Error with credit"))
            }
        }

        test("returns Failure with ExternalServiceError on exception") {
            val fakeLocalDataSource = FakeMovieDetailLocalDataSource()
            val fakeRemoteDataSource = object : MovieDetailRemoteDataSource {
                override suspend fun fetchMovieCredit(
                    movieId: Long,
                    language: String
                ): Result<MovieCastDomainModel?, MovieDetailDomainError> {
                    throw RuntimeException("Error on runtime")
                }

                override suspend fun fetchMovieDetail(
                    movieId: Long,
                    language: String
                ): Result<MovieDetailDomainModel?, MovieDetailDomainError> = Result.Success(
                    MovieDetailDomainModel()
                )

                override suspend fun fetchMovieTrailer(
                    movieId: Long,
                    language: String
                ): Result<MovieVideoDomainModel?, MovieDetailDomainError> =
                    Result.Success(MovieVideoDomainModel("abc123", "", "YouTube"))
            }

            val repository = MovieDetailRepositoryImpl(
                fakeRemoteDataSource, fakeLocalDataSource
            )

            runTest(dispatcher) {
                val result = repository.fetchMovieCredit(1L, "en")

                result.shouldBeInstanceOf<Result.Failure>()
                result.error.message shouldBe "Error on runtime"
            }
        }
    }

    context("UpdateMovieVideoURI - Success") {
        test("returns Success when local update succeeds") {
            val fakeLocalDataSource = FakeMovieDetailLocalDataSource()
            val fakeRemoteDataSource = FakeMovieDetailRemoteDataSource()

            val repository = MovieDetailRepositoryImpl(
                movieDetailRemoteDataSource = fakeRemoteDataSource,
                movieDetailLocalDataSource = fakeLocalDataSource
            )

            runTest(dispatcher) {
                val result = repository
                    .updateMovieVideoURI(1L, "fakeVideoURI")

                result shouldBe Result.Success(true)
            }
        }
    }

    context("UpdateMovieVideoURI - Failure") {
        test("returns Failure with UpdateMovieTrailerLocalError on exception") {
            val fakeLocalDataSource = object : MovieDetailLocalDataSource {
                override suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> =
                    emptyFlow()

                override suspend fun updateMovieVideoUri(movieId: Long, videoKey: String) {
                    throw RuntimeException("Database failure")
                }

                override suspend fun saveMovieDetail(movieDetail: MovieDetailDomainModel) {}
            }

            val fakeRemoteDataSource = FakeMovieDetailRemoteDataSource()

            val repository = MovieDetailRepositoryImpl(
                movieDetailRemoteDataSource = fakeRemoteDataSource,
                movieDetailLocalDataSource = fakeLocalDataSource
            )

            runTest(dispatcher) {
                val result = repository.updateMovieVideoURI(1L, "fakeVideoURI")

                result.shouldBeInstanceOf<Result.Failure>()
                result.error.shouldBeInstanceOf<UpdateMovieTrailerLocalError>()
                result.error.message shouldBe "Error while updating movie trailer in DB + Database failure"
            }
        }
    }
})
