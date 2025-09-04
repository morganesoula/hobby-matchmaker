package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.fakes.FakeImageRepository
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.fakes.FakeTMDBKtorService
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieResponseRemoteModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBKtorErrorHMM
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBKtorService
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException

class MovieRemoteSourceImplTest : FunSpec({
    val dispatcher = StandardTestDispatcher()

    val dummyMovie = MovieRemoteModel(
        id = 1,
        title = "Movie 1",
        poster = "/poster1.jpg"
    )

    context("fetchMovies - success") {
        test("FetchMovies should return movies with local path updated") {
            val fakeTMDBKtorService = FakeTMDBKtorService(
                mapOf(
                    1 to Result.Success(MovieResponseRemoteModel(listOf(dummyMovie))),
                    2 to Result.Success(MovieResponseRemoteModel(emptyList())),
                    3 to Result.Success(MovieResponseRemoteModel(emptyList()))
                )
            )

            val fakeImageRepository = FakeImageRepository()
            val mockFirebaseFirestore = mockk<FirebaseFirestore>(relaxed = true)

            val dataSource = MovieRemoteDataSourceImpl(
                imageRepository = fakeImageRepository,
                firestore = mockFirebaseFirestore,
                tmdbKtorService = fakeTMDBKtorService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovies("en")

                result shouldBe Result.Success(
                    listOf(
                        dummyMovie.toMovieDomainModel()
                            .copy(localCoverFilePath = "local/path/poster1.jpg")
                    )
                )
            }
        }
    }

    context("fetch movies - failure") {
        test("FetchMovies should return failure if first page fails") {
            val fakeTMDBKtorService = FakeTMDBKtorService(
                mapOf(
                    1 to Result.Failure(TMDBKtorErrorHMM("TMDB Error on page 1"))
                )
            )

            val fakeImageRepository = FakeImageRepository()
            val mockFirebaseFirestore = mockk<FirebaseFirestore>(relaxed = true)

            val dataSource = MovieRemoteDataSourceImpl(
                imageRepository = fakeImageRepository,
                firestore = mockFirebaseFirestore,
                tmdbKtorService = fakeTMDBKtorService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovies("en")

                result shouldBe Result.Failure(MovieErrors.FetchMovieByPageErrorHMM("TMDB Error on page 1"))
            }
        }

        test("FetchMovies should return failure if second page fails") {
            val fakeTMDBKtorService = FakeTMDBKtorService(
                mapOf(
                    1 to Result.Success(MovieResponseRemoteModel(listOf(dummyMovie))),
                    2 to Result.Failure(TMDBKtorErrorHMM("TMDB Error on page 2"))
                )
            )

            val fakeImageRepository = FakeImageRepository()
            val mockFirebaseFirestore = mockk<FirebaseFirestore>(relaxed = true)

            val dataSource = MovieRemoteDataSourceImpl(
                imageRepository = fakeImageRepository,
                firestore = mockFirebaseFirestore,
                tmdbKtorService = fakeTMDBKtorService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovies("en")

                result shouldBe
                    Result.Failure(MovieErrors.FetchMovieByPageErrorHMM("TMDB Error on page 2"))
            }
        }

        test("FetchMovies should return failure if third page fails") {
            val fakeTMDBKtorService = FakeTMDBKtorService(
                mapOf(
                    1 to Result.Success(MovieResponseRemoteModel(listOf(dummyMovie))),
                    1 to Result.Success(MovieResponseRemoteModel(emptyList())),
                    2 to Result.Failure(TMDBKtorErrorHMM("TMDB Error on page 3"))
                )
            )

            val fakeImageRepository = FakeImageRepository()
            val mockFirebaseFirestore = mockk<FirebaseFirestore>(relaxed = true)

            val dataSource = MovieRemoteDataSourceImpl(
                imageRepository = fakeImageRepository,
                firestore = mockFirebaseFirestore,
                tmdbKtorService = fakeTMDBKtorService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovies("en")

                result shouldBe
                    Result.Failure(MovieErrors.FetchMovieByPageErrorHMM("TMDB Error on page 3"))
            }
        }

        test("FetchMovies should return network error on IOException") {
            val fakeTMDBKtorService = object : TMDBKtorService {
                override suspend fun getMoviesByPopularityDesc(
                    language: String,
                    page: Int
                ): Result<MovieResponseRemoteModel, TMDBKtorErrorHMM> {
                    throw IOException("Network failure")
                }
            }

            val fakeImageRepository = FakeImageRepository()
            val mockFirebaseFirestore = mockk<FirebaseFirestore>(relaxed = true)

            val dataSource = MovieRemoteDataSourceImpl(
                imageRepository = fakeImageRepository,
                firestore = mockFirebaseFirestore,
                tmdbKtorService = fakeTMDBKtorService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovies("en")

                result shouldBe Result.Failure(MovieErrors.NetworkErrorHMM("Network failure"))
            }
        }

        test("FetchMovies should return unknown error on generic exception") {
            val fakeTMDBKtorService = object : TMDBKtorService {
                override suspend fun getMoviesByPopularityDesc(
                    language: String,
                    page: Int
                ): Result<MovieResponseRemoteModel, TMDBKtorErrorHMM> {
                    throw RuntimeException("Unexpected failure")
                }
            }

            val fakeImageRepository = FakeImageRepository()
            val mockFirebaseFirestore = mockk<FirebaseFirestore>(relaxed = true)

            val dataSource = MovieRemoteDataSourceImpl(
                imageRepository = fakeImageRepository,
                firestore = mockFirebaseFirestore,
                tmdbKtorService = fakeTMDBKtorService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovies("en")

                result shouldBe Result.Failure(MovieErrors.UnknownErrorHMM("Unexpected failure"))
            }
        }

        test("FetchMovies should skip image download if coverFileName is blank") {
            val noPosterMovie = MovieRemoteModel(
                id = 2,
                title = "No poster movie",
                poster = ""
            )

            val fakeTMDBKtorService = FakeTMDBKtorService(
                mapOf(
                    1 to Result.Success(MovieResponseRemoteModel(listOf(noPosterMovie))),
                    2 to Result.Success(MovieResponseRemoteModel(emptyList())),
                    3 to Result.Success(MovieResponseRemoteModel(emptyList())),
                )
            )

            val fakeImageRepository = object : ImageRepository {
                override suspend fun getRemoteImage(localPosterPath: String): String {
                    error("getRemoteImage should not be called when posterPath is blank")
                }

                override suspend fun downloadImage(remotePosterPath: String): String =
                    "ignored"

                override suspend fun saveRemoteImageAndUpdateMovie(
                    coverFileName: String,
                    updateMovie: suspend (localImagePath: String) -> Unit
                ) = Unit
            }

            val mockFirebaseFirestore = mockk<FirebaseFirestore>(relaxed = true)

            val dataSource = MovieRemoteDataSourceImpl(
                imageRepository = fakeImageRepository,
                firestore = mockFirebaseFirestore,
                tmdbKtorService = fakeTMDBKtorService
            )

            runTest(dispatcher) {
                val result = dataSource.fetchMovies("en")

                result shouldBe Result.Success(listOf(noPosterMovie.toMovieDomainModel()))
            }
        }
    }
})
