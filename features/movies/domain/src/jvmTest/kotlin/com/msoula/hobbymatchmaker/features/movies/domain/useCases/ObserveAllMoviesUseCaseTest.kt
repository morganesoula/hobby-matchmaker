package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.errors.MovieErrors
import com.msoula.hobbymatchmaker.features.movies.domain.fakes.FakeMovieRepository
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class ObserveAllMoviesUseCaseTest : FunSpec({
    val dispatcher = StandardTestDispatcher()

    val dummyMovie = MovieDomainModel(
        id = 1,
        title = "Test movie",
        overview = "",
        isFavorite = false,
        localCoverFilePath = "",
        coverFileName = "",
        isSeen = false
    )

    test("should emit Success when repository already has movies") {
        val fakeMovieRepository = FakeMovieRepository(listOf(dummyMovie))
        val fetchMovieUseCase = FetchMoviesUseCase(fakeMovieRepository)
        val observeAllMoviesUseCase =
            ObserveAllMoviesUseCase(fakeMovieRepository, fetchMovieUseCase, dispatcher)

        runTest(dispatcher) {
            launch { fakeMovieRepository.emitMovies(listOf(dummyMovie)) }

            observeAllMoviesUseCase(Parameters.StringParam("fr")).test {
                awaitItem() shouldBe Result.Success(
                    ObserveAllMoviesSuccess.Success(
                        listOf(
                            dummyMovie
                        )
                    )
                )
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("should emit Loading then DataLoadedInDB when DB is empty and fetch succeeds") {
        val fakeMovieRepository = FakeMovieRepository(emptyList())
        val fetchMovieUseCase = FetchMoviesUseCase(fakeMovieRepository)
        val observeAllMoviesUseCase =
            ObserveAllMoviesUseCase(fakeMovieRepository, fetchMovieUseCase, dispatcher)

        runTest(dispatcher) {
            launch { fakeMovieRepository.emitMovies(emptyList()) }

            observeAllMoviesUseCase(Parameters.StringParam("en")).test {
                awaitItem() shouldBe Result.Success(ObserveAllMoviesSuccess.Loading)
                awaitItem() shouldBe Result.Success(ObserveAllMoviesSuccess.DataLoadedInDB)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("should emit Loading then Failure when DB is empty and fetch fails") {
        val fakeMovieRepository = FakeMovieRepository(emptyList())
        fakeMovieRepository.setFetchResult(Result.Failure(MovieErrors.NetworkErrorHMM("No connection")))

        val fetchMovieUseCase = FetchMoviesUseCase(fakeMovieRepository)
        val observeAllMoviesUseCase =
            ObserveAllMoviesUseCase(fakeMovieRepository, fetchMovieUseCase, dispatcher)

        runTest(dispatcher) {
            launch { fakeMovieRepository.emitMovies(emptyList()) }

            observeAllMoviesUseCase(Parameters.StringParam("en")).test {
                awaitItem() shouldBe Result.Success(ObserveAllMoviesSuccess.Loading)
                val result = awaitItem()

                result.shouldBeInstanceOf<Result.Failure>()
                (result.error as? ObserveAllMoviesErrors.NetworkErrorHMM)?.networkErrorMessage shouldBe "No connection"
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
})
