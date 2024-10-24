package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.features.movies.domain.useCases.fakes.FakeMovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.fakes.FakeMovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.fakes.FakeMovieRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SetMovieFavoriteUseCaseTest {

    private lateinit var fakeMovieRepository: FakeMovieRepository
    private lateinit var setMovieFavoriteUseCase: SetMovieFavoriteUseCase
    private lateinit var fetchMoviesUseCase: FetchMoviesUseCase

    @Before
    fun setUp() {
        val fakeRemoteDataSource = FakeMovieRemoteDataSource()
        val fakeMovieLocalDataSource = FakeMovieLocalDataSource()
        fakeMovieRepository = FakeMovieRepository(fakeRemoteDataSource, fakeMovieLocalDataSource)
        setMovieFavoriteUseCase = SetMovieFavoriteUseCase(fakeMovieRepository)
        fetchMoviesUseCase = FetchMoviesUseCase(fakeMovieRepository)
    }

    @Test
    fun `setMovieFavoriteUseCase updates movie favorite value`() = runBlocking {
        var query = fakeMovieRepository.getFirstElement()
        assertFalse(query.isFavorite)

        setMovieFavoriteUseCase("uuid1", 1, true)
        query = fakeMovieRepository.getFirstElement()

        assertTrue(query.isFavorite)
    }
}
