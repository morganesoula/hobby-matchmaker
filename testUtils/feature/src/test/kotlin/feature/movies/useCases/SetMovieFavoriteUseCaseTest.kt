package feature.movies.useCases

import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.FetchMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.testUtils.common.fakes.FakeMovieRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SetMovieFavoriteUseCaseTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeMovieRepository: FakeMovieRepository
    private lateinit var setMovieFavoriteUseCase: SetMovieFavoriteUseCase
    private lateinit var fetchMoviesUseCase: FetchMoviesUseCase

    @Before
    fun setUp() {
        fakeMovieRepository = FakeMovieRepository()
        setMovieFavoriteUseCase = SetMovieFavoriteUseCase(fakeMovieRepository)
        fetchMoviesUseCase = FetchMoviesUseCase(fakeMovieRepository)
    }

    @Test
    fun `setMovieFavoriteUseCase updates movie favorite value`() = runTest(testDispatcher) {
        fakeMovieRepository.setMovies(listOf(MovieDomainModel(id = 1)))

        var query = fakeMovieRepository.getFirstElement()
        assertFalse(query.isFavorite)

        setMovieFavoriteUseCase("uuid1", 1, true)
        query = fakeMovieRepository.getFirstElement()

        assertTrue(query.isFavorite)
    }
}
