package feature.movies.useCases

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.FetchMoviesUseCase
import com.msoula.hobbymatchmaker.testUtils.common.fakes.FakeMovieRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class FetchMoviesUseCaseTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeMovieRepository: FakeMovieRepository
    private lateinit var fetchMoviesUseCase: FetchMoviesUseCase

    @org.junit.Before
    fun setUp() {
        fakeMovieRepository = FakeMovieRepository()
        fetchMoviesUseCase = FetchMoviesUseCase(fakeMovieRepository)
    }

    @Test
    fun `fetchMoviesUseCase returns Failure when fetching movies fails`() = runTest(testDispatcher) {
        val result = fetchMoviesUseCase("unsupported-language")
        assert(result is Result.Failure)
    }

    @Test
    fun `fetchMoviesUseCase returns Success when movies are fetched successfully`() = runTest(testDispatcher) {
        val result = fetchMoviesUseCase("en")
        assert(result is Result.Success)
    }
}
