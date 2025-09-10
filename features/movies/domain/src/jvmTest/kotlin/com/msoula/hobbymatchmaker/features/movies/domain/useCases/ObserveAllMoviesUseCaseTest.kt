package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.domain.fakes.FakeMovieRepository
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveAllMoviesUseCaseTest : FunSpec({
    val dispatcher = StandardTestDispatcher()

    context("emptyList") {
        test("fetchMoviesUseCase OK -> emit DataLoadedInDB") {
            runTest(dispatcher) {
                val repository = FakeMovieRepository()
                val fetchMoviesUseCase = mockk<FetchMoviesUseCase>()

                coEvery { fetchMoviesUseCase("fr-FR") } returns AppResult.Success(Unit)

                val result = ObserveAllMoviesUseCase(repository, fetchMoviesUseCase, dispatcher)
                val flow = result(language = "fr-FR")

                flow.test {
                    repository.tryEmit(emptyList())
                    advanceUntilIdle()

                    val item = awaitItem()
                    item.shouldBeInstanceOf<AppResult.Success<ObserveAllMoviesSuccess>>()
                    item.data shouldBe ObserveAllMoviesSuccess.DataLoadedInDB

                    coVerify(exactly = 1) { fetchMoviesUseCase("fr-FR") }
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        test("fetchMoviesUseCase KO -> emit Failure") {
            runTest(dispatcher) {
                val repository = FakeMovieRepository()
                val fetchMoviesUseCase = mockk<FetchMoviesUseCase>()

                coEvery { fetchMoviesUseCase("fr-FR") } returns AppResult.Failure(AppError.Network.Timeout)

                val result = ObserveAllMoviesUseCase(repository, fetchMoviesUseCase, dispatcher)
                val flow = result(language = "fr-FR")

                flow.test {
                    repository.tryEmit(emptyList())
                    advanceUntilIdle()

                    val item = awaitItem()
                    item.shouldBeInstanceOf<AppResult.Failure<Any>>()
                    item.error shouldBe AppError.Network.Timeout

                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }

    context("with data") {
        test("emit Success(movies) -> fetch not called") {
            runTest(dispatcher) {
                val repository = FakeMovieRepository()
                val fetchMoviesUseCase = mockk<FetchMoviesUseCase>(relaxed = true)

                val result = ObserveAllMoviesUseCase(repository, fetchMoviesUseCase, dispatcher)
                val flow = result(language = "fr-FR")

                val mockOne = mockk<MovieDomainModel>(relaxed = true)
                val mockTwo = mockk<MovieDomainModel>(relaxed = true)
                val movies = listOf(mockOne, mockTwo)

                flow.test {
                    repository.tryEmit(movies)
                    advanceUntilIdle()

                    val item = awaitItem()
                    item.shouldBeInstanceOf<AppResult.Success<ObserveAllMoviesSuccess>>()
                    item.data shouldBe ObserveAllMoviesSuccess.Success(movies)

                    coVerify(exactly = 0) { fetchMoviesUseCase(any()) }
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        test("distinctUntilChanged -> same non empty-list emitted twice -> one Success and no fetch") {
            runTest(dispatcher) {
                val repository = FakeMovieRepository()
                val fetchMoviesUseCase = mockk<FetchMoviesUseCase>(relaxed = true)

                val result = ObserveAllMoviesUseCase(repository, fetchMoviesUseCase, dispatcher)
                val flow = result(language = "fr-FR")

                val mockOne = mockk<MovieDomainModel>(relaxed = true)
                val movies = listOf(mockOne)

                flow.test {
                    repository.tryEmit(movies)
                    advanceUntilIdle()

                    val firstItem = awaitItem()
                    firstItem shouldBe AppResult.Success(ObserveAllMoviesSuccess.Success(movies))

                    repository.tryEmit(movies)
                    advanceUntilIdle()

                    expectNoEvents()

                    coVerify(exactly = 0) { fetchMoviesUseCase(any()) }
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        test("distinctUntilChanged: two consecutive empty lists -> fetch called once only") {
            runTest(dispatcher) {
                val repository = FakeMovieRepository()
                val fetchMoviesUseCase = mockk<FetchMoviesUseCase>()
                coEvery { fetchMoviesUseCase("fr-FR") } returns AppResult.Success(Unit)

                val result = ObserveAllMoviesUseCase(repository, fetchMoviesUseCase, dispatcher)
                val flow = result(language = "fr-FR")

                flow.test {
                    repository.tryEmit(emptyList())
                    advanceUntilIdle()
                    awaitItem() shouldBe AppResult.Success(ObserveAllMoviesSuccess.DataLoadedInDB)

                    repository.tryEmit(emptyList())
                    advanceUntilIdle()
                    expectNoEvents()

                    coVerify(exactly = 1) { fetchMoviesUseCase("fr-FR") }
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }

})
