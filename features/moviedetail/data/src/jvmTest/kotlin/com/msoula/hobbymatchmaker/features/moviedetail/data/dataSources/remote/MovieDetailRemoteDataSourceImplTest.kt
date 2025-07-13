package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.fakes.FakeMovieDetailKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.fakes.FakeMovieVideosKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.errors.MovieDetailDataError
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieDetailResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideosResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieCreditsKtorError
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieDetailKtorError
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieDetailKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieVideosKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException

class MovieDetailRemoteDataSourceImplTest : FunSpec({
    val dispatcher = StandardTestDispatcher()

    context("fetchMovieDetail - Success") {
        test("returns Success when fetchMovieDetail succeeds") {
            val fakeMovieDetailService = FakeMovieDetailKtorService()
            val fakeMovieVideosService = FakeMovieVideosKtorService()

            val dataSource = MovieDetailRemoteDataSourceImpl(
                fakeMovieDetailService, fakeMovieVideosService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovieDetail(1L, "en")

                result.shouldBeTypeOf<Result.Success<MovieDetailDomainModel>>()
                result.data.title shouldBe "Test movie"
            }
        }
    }

    context("fetchMovieDetail - Failure") {
        test("returns NoConnection error when IOException is thrown") {
            val fakeMovieDetailService = object : MovieDetailKtorService {
                override suspend fun fetchMovieDetail(
                    movieId: Long,
                    language: String
                ): Result<MovieDetailResponseRemoteModel, MovieDetailKtorError> {
                    throw IOException("No network")
                }

                override suspend fun fetchMovieCredits(
                    movieId: Long,
                    language: String
                ): Result<CastResponseRemoteModel, MovieCreditsKtorError> =
                    error("Not used")
            }

            val fakeMovieVideosService = FakeMovieVideosKtorService()

            val dataSource = MovieDetailRemoteDataSourceImpl(
                fakeMovieDetailService, fakeMovieVideosService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovieDetail(1L, "en")

                result shouldBe Result.Failure(MovieDetailDomainError.NoConnection("No network"))
            }
        }

        test("returns MovieDetailError when unknown exception is thrown") {
            val fakeMovieDetailService = object : MovieDetailKtorService {
                override suspend fun fetchMovieDetail(
                    movieId: Long,
                    language: String
                ): Result<MovieDetailResponseRemoteModel, MovieDetailKtorError> {
                    throw IllegalStateException("Unexpected error")
                }

                override suspend fun fetchMovieCredits(
                    movieId: Long,
                    language: String
                ): Result<CastResponseRemoteModel, MovieCreditsKtorError> =
                    error("Not used")
            }

            val fakeMovieVideosService = FakeMovieVideosKtorService()

            val dataSource = MovieDetailRemoteDataSourceImpl(
                fakeMovieDetailService, fakeMovieVideosService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovieDetail(1L, "en")

                result shouldBe Result.Failure(
                    MovieDetailDomainError.MovieDetailError("Unexpected error")
                )
            }
        }
    }

    context("fetchMovieCredit - Success") {
        test("should return Success when fetchMovieCredits succeeds") {
            val fakeMovieDetailService = FakeMovieDetailKtorService()
            val fakeMovieVideosService = FakeMovieVideosKtorService()

            val dataSource = MovieDetailRemoteDataSourceImpl(
                fakeMovieDetailService, fakeMovieVideosService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovieCredit(1L, "en")

                result.shouldBeTypeOf<Result.Success<MovieCastDomainModel>>()

                result.data.cast.first() shouldBe MovieActorDomainModel(
                    id = 1,
                    name = "Actor one",
                    role = "Role one"
                )
            }
        }
    }

    context("fetchMovieCredit - Failure") {
        test("should return NoConnection when there is network") {
            val fakeMovieDetailService = object : MovieDetailKtorService {
                override suspend fun fetchMovieDetail(
                    movieId: Long,
                    language: String
                ): Result<MovieDetailResponseRemoteModel, MovieDetailKtorError> =
                    error("Not used")

                override suspend fun fetchMovieCredits(
                    movieId: Long,
                    language: String
                ): Result<CastResponseRemoteModel, MovieCreditsKtorError> {
                    throw IOException("No network found")
                }
            }
            val fakeMovieVideosService = FakeMovieVideosKtorService()

            val dataSource = MovieDetailRemoteDataSourceImpl(
                fakeMovieDetailService, fakeMovieVideosService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovieCredit(1L, "en")

                result shouldBe Result.Failure(
                    MovieDetailDomainError.NoConnection("No network found")
                )
            }
        }

        test("should return CreditError when movie id is -1") {
            val fakeMovieDetailService = object : MovieDetailKtorService {
                override suspend fun fetchMovieDetail(
                    movieId: Long,
                    language: String
                ): Result<MovieDetailResponseRemoteModel, MovieDetailKtorError> =
                    error("Not used")

                override suspend fun fetchMovieCredits(
                    movieId: Long,
                    language: String
                ): Result<CastResponseRemoteModel, MovieCreditsKtorError> =
                    Result.Failure(MovieCreditsKtorError("Error while fetching credits"))
            }
            val fakeMovieVideosService = FakeMovieVideosKtorService()

            val dataSource = MovieDetailRemoteDataSourceImpl(
                fakeMovieDetailService, fakeMovieVideosService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovieCredit(-1L, "en")

                result shouldBe Result.Failure(
                    MovieDetailDomainError.CreditError("Error while fetching credits")
                )
            }
        }
    }

    context("fetchMovieTrailer - Success") {
        test("should return Success when fetchMovieVideo succeeds") {
            val fakeMovieDetailService = FakeMovieDetailKtorService()
            val fakeMovieVideosService = FakeMovieVideosKtorService()

            val dataSource = MovieDetailRemoteDataSourceImpl(
                fakeMovieDetailService, fakeMovieVideosService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovieTrailer(1L, "en")

                result.shouldBeTypeOf<Result.Success<MovieVideoDomainModel>>()
                result.data.type shouldBe "Trailer"
                result.data.key shouldBe "test key 1"
            }
        }
    }

    context("fetchMovieTrailer - Failure") {
        test("should return NoConnection when no network found") {
            val fakeMovieDetailService = FakeMovieDetailKtorService()
            val fakeMovieVideosService = object : MovieVideosKtorService {
                override suspend fun fetchMovieVideos(
                    movie: Long,
                    language: String
                ): Result<MovieVideosResponseRemoteModel, MovieDetailDataError> {
                    throw IOException("No network found dear")
                }
            }

            val dataSource = MovieDetailRemoteDataSourceImpl(
                fakeMovieDetailService, fakeMovieVideosService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovieTrailer(1L, "en")

                result shouldBe Result.Failure(
                    MovieDetailDomainError.NoConnection(
                        "No network found dear"
                    )
                )
            }
        }

        test("should return TrailerError when error occurred") {
            val fakeMovieDetailService = FakeMovieDetailKtorService()
            val fakeMovieVideosService = object : MovieVideosKtorService {
                override suspend fun fetchMovieVideos(
                    movie: Long,
                    language: String
                ): Result<MovieVideosResponseRemoteModel, MovieDetailDataError> {
                    throw IllegalStateException("Oopsie, something went wrong")
                }
            }

            val dataSource = MovieDetailRemoteDataSourceImpl(
                fakeMovieDetailService, fakeMovieVideosService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovieTrailer(1L, "en")

                result shouldBe Result.Failure(
                    MovieDetailDomainError.TrailerError(
                        "Oopsie, something went wrong"
                    )
                )
            }
        }
    }
})
