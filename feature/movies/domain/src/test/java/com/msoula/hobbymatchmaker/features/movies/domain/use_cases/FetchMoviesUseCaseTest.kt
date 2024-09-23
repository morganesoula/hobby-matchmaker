package com.msoula.hobbymatchmaker.features.movies.domain.use_cases

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.fakes.FakeMovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.fakes.FakeMovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.fakes.FakeMovieRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

@ExperimentalCoroutinesApi
class FetchMoviesUseCaseTest {

    private lateinit var fakeMovieRepository: FakeMovieRepository
    private lateinit var fetchMoviesUseCase: FetchMoviesUseCase

    @org.junit.Before
    fun setUp() {
        val fakeMovieRemoteDataSource = FakeMovieRemoteDataSource()
        val fakeLocalDataSource = FakeMovieLocalDataSource()
        fakeMovieRepository = FakeMovieRepository(fakeMovieRemoteDataSource, fakeLocalDataSource)
        fetchMoviesUseCase = FetchMoviesUseCase(fakeMovieRepository)
    }

    @Test
    fun `fetchMoviesUseCase returns Success when movies are fetched successfully`() = runBlocking {
        val result = fetchMoviesUseCase("fr-FR")
        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun `fetchMoviesUseCase returns Failure when fetching movies fails`() = runBlocking {
        val result = fetchMoviesUseCase("unsupported-language")
        assert(result is Result.Failure)
    }
}
