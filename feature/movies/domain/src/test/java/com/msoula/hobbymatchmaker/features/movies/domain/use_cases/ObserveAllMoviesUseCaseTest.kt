package com.msoula.hobbymatchmaker.features.movies.domain.use_cases

import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.fakes.FakeMovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.fakes.FakeMovieRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObserveAllMoviesUseCaseTest {

    private lateinit var fakeMovieRepository: FakeMovieRepository
    private lateinit var observeMoviesUseCase: ObserveAllMoviesUseCase

    @Before
    fun setUp() {
        val fakeMovieRemoteDataSource = FakeMovieRemoteDataSource()
        fakeMovieRepository = FakeMovieRepository(fakeMovieRemoteDataSource)
        observeMoviesUseCase = ObserveAllMoviesUseCase(fakeMovieRepository)
    }

    @Test
    fun `observeAllMoviesUseCase returns list of movies when list is not empty`() = runBlocking {
        val result = observeMoviesUseCase()
        assertEquals(result.first().size, 2)
    }

    @Test
    fun `observeAllMoviesUseCase returns empty list when list is empty`() = runBlocking {
        fakeMovieRepository.clearData()
        val result = observeMoviesUseCase()
        assertEquals(result.first().size, 0)
    }
}
