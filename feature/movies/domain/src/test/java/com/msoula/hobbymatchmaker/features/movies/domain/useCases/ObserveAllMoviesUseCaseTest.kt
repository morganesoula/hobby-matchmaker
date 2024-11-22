package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.fakes.FakeMovieRepositoryBis
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveAllMoviesUseCaseTest {
    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun `should return success when movies are already in DB`() = runTest(testDispatcher) {
        val fakeMovieRepository = FakeMovieRepositoryBis()
        val observeAllMoviesUseCase = ObserveAllMoviesUseCase(
            fakeMovieRepository,
            FetchMoviesUseCase(fakeMovieRepository),
            testDispatcher
        )

        fakeMovieRepository.setMovies(listOf(MovieDomainModel(1, "Test movie")))

        val results = mutableListOf<Result<ObserveAllMoviesSuccess, ObserveAllMoviesErrors>>()
        observeAllMoviesUseCase(Parameters.StringParam("en")).collect {
            results.add(it)
        }

        assertEquals(1, results.size)
        assert(results[0] is Result.Success)
        assert((results[0] as Result.Success).data is ObserveAllMoviesSuccess.Success)
    }

    @Test
    fun `should fetch movies and return success when DB is empty`() = runTest(testDispatcher) {
        val fakeMovieRepository = FakeMovieRepositoryBis()
        val observeAllMoviesUseCase = ObserveAllMoviesUseCase(
            fakeMovieRepository,
            FetchMoviesUseCase(fakeMovieRepository),
            testDispatcher
        )

        fakeMovieRepository.setMovies(emptyList())

        val results = mutableListOf<Result<ObserveAllMoviesSuccess, ObserveAllMoviesErrors>>()
        observeAllMoviesUseCase(Parameters.StringParam("en")).collect {
            results.add(it)
        }

        assertEquals(2, results.size)
        assert(results[0] is Result.Success)
        assert((results[0] as Result.Success).data is ObserveAllMoviesSuccess.Loading)

        assert(results[1] is Result.Success)
        assert((results[1] as Result.Success).data is ObserveAllMoviesSuccess.DataLoadedInDB)
    }

    @Test
    fun `should return network error when movies fails`() = runTest(testDispatcher) {
        val fakeMovieRepository = FakeMovieRepositoryBis()
        val observeAllMoviesUseCase = ObserveAllMoviesUseCase(
            fakeMovieRepository,
            FetchMoviesUseCase(fakeMovieRepository),
            testDispatcher
        )

        fakeMovieRepository.setMovies(emptyList())

        val results = mutableListOf<Result<ObserveAllMoviesSuccess, ObserveAllMoviesErrors>>()
        observeAllMoviesUseCase(Parameters.StringParam("fr")).collect {
            results.add(it)
        }

        assertEquals(2, results.size)
        assert(results[0] is Result.Success)
        assert((results[0] as Result.Success).data is ObserveAllMoviesSuccess.Loading)

        assert(results[1] is Result.Failure)
        assert((results[1] as Result.Failure).error is ObserveAllMoviesErrors.NetworkError)
    }
}
