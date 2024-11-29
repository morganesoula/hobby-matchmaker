package feature.moviedetail.useCases

import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.FetchMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.ObserveMovieErrors
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.ObserveMovieSuccess
import feature.moviedetail.fakes.FakeMovieDetailRepository
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ObserveMovieDetailUseCaseTest {
    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun `should return an empty error when movie is null`() = runTest(testDispatcher) {
        val fakeMovieDetailRepository = FakeMovieDetailRepository()
        val observeMovieDetailUseCase = ObserveMovieDetailUseCase(
            movieDetailRepository = fakeMovieDetailRepository,
            fetchMovieDetailUseCase = FetchMovieDetailUseCase(fakeMovieDetailRepository),
            dispatcher = testDispatcher
        )

        fakeMovieDetailRepository.setMovie(null)

        val results = mutableListOf<Result<ObserveMovieSuccess, ObserveMovieErrors>>()
        observeMovieDetailUseCase(
            Parameters.LongStringParam(
                longValue = 0L,
                stringValue = "en"
            )
        ).collect {
            results.add(it)
        }

        assert(results.size == 2)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Failure)
        assertTrue((results[1] as Result.Failure).error is ObserveMovieErrors.Empty)
    }

    @Test
    fun `should return a success when movie is not null and synopsis is not null`() =
        runTest(testDispatcher) {
            val fakeMovieDetailRepository = FakeMovieDetailRepository()
            val observeMovieDetailUseCase = ObserveMovieDetailUseCase(
                movieDetailRepository = fakeMovieDetailRepository,
                fetchMovieDetailUseCase = FetchMovieDetailUseCase(fakeMovieDetailRepository),
                dispatcher = testDispatcher
            )

            fakeMovieDetailRepository.setMovie(
                MovieDetailDomainModel(
                    1L,
                    synopsis = "Random test synopsis"
                )
            )

            val results = mutableListOf<Result<ObserveMovieSuccess, ObserveMovieErrors>>()
            observeMovieDetailUseCase(Parameters.LongStringParam(1L, "en")).collect {
                results.add(it)
            }

            assert(results.size == 2)
            assertTrue((results[0] is Result.Loading))
            assertTrue(results[1] is Result.Success)
            assertTrue(((results[1] as Result.Success).data is ObserveMovieSuccess.Success))
        }

    @Test
    fun `should return a failure when movie is not null, synopsis is null and fetch fails`() =
        runTest(testDispatcher) {
            val fakeMovieDetailRepository = FakeMovieDetailRepository()
            val observeMovieDetailUseCase = ObserveMovieDetailUseCase(
                movieDetailRepository = fakeMovieDetailRepository,
                fetchMovieDetailUseCase = FetchMovieDetailUseCase(fakeMovieDetailRepository),
                dispatcher = testDispatcher
            )

            fakeMovieDetailRepository.setMovie(MovieDetailDomainModel(id = 2L))

            val results = mutableListOf<Result<ObserveMovieSuccess, ObserveMovieErrors>>()
            observeMovieDetailUseCase(Parameters.LongStringParam(1L, "en")).collect {
                results.add(it)
            }

            assert(results.size == 2)
            assertTrue(results[0] is Result.Loading)
            assertTrue(results[1] is Result.Failure)
            assertTrue((results[1] as Result.Failure).error is ObserveMovieErrors.Error)
        }

    @Test
    fun `should return a success when movie is not null, synopsis is null and fetch succeeds`() =
        runTest(
            testDispatcher
        ) {
            val fakeMovieDetailRepository = FakeMovieDetailRepository()
            val observeMovieDetailUseCase = ObserveMovieDetailUseCase(
                movieDetailRepository = fakeMovieDetailRepository,
                fetchMovieDetailUseCase = FetchMovieDetailUseCase(fakeMovieDetailRepository),
                dispatcher = testDispatcher
            )

            fakeMovieDetailRepository.setMovie(MovieDetailDomainModel(id = 1L))

            val results = mutableListOf<Result<ObserveMovieSuccess, ObserveMovieErrors>>()
            observeMovieDetailUseCase(Parameters.LongStringParam(1L, "en")).collect {
                results.add(it)
            }

            assertTrue(results.size == 2)
            assertTrue(results[0] is Result.Loading)
            assertTrue(results[1] is Result.Success)
            assertTrue((results[1] as Result.Success).data == ObserveMovieSuccess.DataLoadedInDB)
        }
}
