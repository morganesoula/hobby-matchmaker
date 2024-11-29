package feature.moviedetail.useCases

import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.FetchingTrailerError
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.MovieTrailerReady
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.UpdateMovieVideoURIUseCase
import feature.moviedetail.fakes.FakeMovieDetailRepository
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ManageMovieTrailerUseCaseTest {
    private val testDispatcher = StandardTestDispatcher()
    private val fakeMovieDetailRepository = FakeMovieDetailRepository()

    @Test
    fun `should return failure when movieId is null`() = runTest(testDispatcher) {
        val manageMovieTrailerUseCase = ManageMovieTrailerUseCase(
            movieDetailRepository = fakeMovieDetailRepository,
            updateMovieVideoURIUseCase = UpdateMovieVideoURIUseCase(fakeMovieDetailRepository),
            dispatcher = testDispatcher
        )

        fakeMovieDetailRepository.setMovie(MovieDetailDomainModel(id = 25L))

        val results = mutableListOf<Result<MovieTrailerReady, FetchingTrailerError>>()
        manageMovieTrailerUseCase(Parameters.LongStringParam(3L, "en-US")).collect {
            results.add(it)
        }

        assertTrue(results.size == 3)
        assert(results[0] is Result.Loading)
        assert(results[1] is Result.Failure)
        assert((results[1] as Result.Failure).error is FetchingTrailerError)
        assert(results[2] is Result.Failure)
        assert((results[2] as Result.Failure).error is FetchingTrailerError)
    }

    @Test
    fun `should return success when movieId is not null`() = runTest(testDispatcher) {
        val manageMovieTrailerUseCase = ManageMovieTrailerUseCase(
            movieDetailRepository = fakeMovieDetailRepository,
            updateMovieVideoURIUseCase = UpdateMovieVideoURIUseCase(fakeMovieDetailRepository),
            dispatcher = testDispatcher
        )

        fakeMovieDetailRepository.setMovie(MovieDetailDomainModel(id = 1L))

        val results = mutableListOf<Result<MovieTrailerReady, FetchingTrailerError>>()
        manageMovieTrailerUseCase(Parameters.LongStringParam(3L, "en-US")).collect {
            results.add(it)
        }

        assert(results.size == 2)
        assert(results[0] is Result.Loading)
        assert(results[1] is Result.Success)
        assert((results[1] as Result.Success).data.videoURI == "testKey")
    }

    @Test
    fun `should return a failure when movieId is not null and movieResponse is null`() =
        runTest(testDispatcher) {
            val manageMovieTrailerUseCase = ManageMovieTrailerUseCase(
                movieDetailRepository = fakeMovieDetailRepository,
                updateMovieVideoURIUseCase = UpdateMovieVideoURIUseCase(fakeMovieDetailRepository),
                dispatcher = testDispatcher
            )

            fakeMovieDetailRepository.setMovie(MovieDetailDomainModel(id = 50L))

            val results = mutableListOf<Result<MovieTrailerReady, FetchingTrailerError>>()
            manageMovieTrailerUseCase(Parameters.LongStringParam(50L, "en-US")).collect {
                results.add(it)
            }

            assert(results.size == 3)
            assert(results[0] is Result.Loading)
            assert(results[1] is Result.Failure)
            assert(results[2] is Result.Failure)
            assert((results[2] as Result.Failure).error is FetchingTrailerError)
        }
}

