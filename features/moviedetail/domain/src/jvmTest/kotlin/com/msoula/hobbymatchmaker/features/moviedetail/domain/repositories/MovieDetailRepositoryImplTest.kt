package com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories

import com.msoula.hobbymatchmaker.core.common.ExternalServiceErrorHMM
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainErrorHMM
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.UpdateMovieTrailerLocalErrorHMM
import com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes.FakeMovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes.FakeMovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.fail
import com.msoula.hobbymatchmaker.core.common.Result as AppResult

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

                result shouldBe AppResult.Success(MovieDetailDomainModel())
            }
        }
    }

    context("FetchMovieDetail - Failure") {
        test("returns Failure with EmptyDataError when remote returns null data") {
            val fakeLocalDataSource = FakeMovieDetailLocalDataSource()
            val fakeRemoteDataSource = FakeMovieDetailRemoteDataSource(
                fetchMovieDetailResult = AppResult.Failure(
                    MovieDetailDomainErrorHMM.MovieDetailErrorHMM("No data found")
                )
            )

            val repository = MovieDetailRepositoryImpl(
                movieDetailRemoteDataSource = fakeRemoteDataSource,
                movieDetailLocalDataSource = fakeLocalDataSource
            )

            runTest(dispatcher) {
                val result = repository.fetchMovieDetail(1L, "en")

                result shouldBe AppResult.Failure(
                    MovieDetailDomainErrorHMM.MovieDetailErrorHMM(
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
                ): Result<MovieCastDomainModel?, MovieDetailDomainErrorHMM> {
                    throw RuntimeException("Remote error")
                }

                override suspend fun fetchMovieDetail(
                    movieId: Long,
                    language: String
                ): Result<MovieDetailDomainModel?, MovieDetailDomainErrorHMM> =
                    Result.Success(null)

                override suspend fun fetchMovieTrailer(
                    movieId: Long,
                    language: String
                ): Result<MovieVideoDomainModel?, MovieDetailDomainErrorHMM> =
                    Result.Success(null)
            }

            val repository = MovieDetailRepositoryImpl(
                movieDetailRemoteDataSource = fakeRemoteDataSource,
                movieDetailLocalDataSource = fakeLocalDataSource
            )

            runTest(dispatcher) {
                val result = repository.fetchMovieCredit(1L, "en")

                when (result) {
                    is AppResult.Failure -> {
                        result.error shouldBe ExternalServiceErrorHMM(message = "Remote error")
                    }

                    is AppResult.Success -> fail("Expected Failure, got Success")
                }
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
                    MovieDetailDomainErrorHMM.CreditErrorHMM("Error with credit")
                )
            )

            val repository = MovieDetailRepositoryImpl(
                movieDetailRemoteDataSource = fakeRemoteDataSource,
                movieDetailLocalDataSource = fakeLocalDataSource
            )

            runTest(dispatcher) {
                val result = repository.fetchMovieCredit(1L, "en")

                result shouldBe Result.Failure(MovieDetailDomainErrorHMM.CreditErrorHMM("Error with credit"))
            }
        }

        test("returns Failure with ExternalServiceError on exception") {
            val fakeLocalDataSource = FakeMovieDetailLocalDataSource()
            val fakeRemoteDataSource = object : MovieDetailRemoteDataSource {
                override suspend fun fetchMovieCredit(
                    movieId: Long,
                    language: String
                ): Result<MovieCastDomainModel?, MovieDetailDomainErrorHMM> {
                    throw RuntimeException("Error on runtime")
                }

                override suspend fun fetchMovieDetail(
                    movieId: Long,
                    language: String
                ): Result<MovieDetailDomainModel?, MovieDetailDomainErrorHMM> = Result.Success(
                    MovieDetailDomainModel()
                )

                override suspend fun fetchMovieTrailer(
                    movieId: Long,
                    language: String
                ): Result<MovieVideoDomainModel?, MovieDetailDomainErrorHMM> =
                    Result.Success(MovieVideoDomainModel("abc123", "", "YouTube"))
            }

            val repository = MovieDetailRepositoryImpl(
                fakeRemoteDataSource, fakeLocalDataSource
            )

            runTest(dispatcher) {
                val result = repository.fetchMovieCredit(1L, "en")

                when (result) {
                    is AppResult.Failure -> {
                        result.error.message shouldBe "Error on runtime"
                    }

                    is AppResult.Success -> fail("Should be Failure")
                }
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

                when (result) {
                    is AppResult.Failure -> {
                        result.error shouldBe UpdateMovieTrailerLocalErrorHMM(
                            "Error while updating movie trailer in DB + Database failure"
                        )
                    }
                }
            }
        }
    }
})
