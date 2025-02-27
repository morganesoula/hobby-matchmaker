package feature.moviedetail.viewModel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.FetchingTrailerError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.MovieTrailerReady
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieErrors
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailViewStateModel
import feature.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MovieDetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val mockObserveMovieDetailUseCase = mockk<ObserveMovieDetailUseCase>()
    private val mockManageMovieTrailerUseCase = mockk<ManageMovieTrailerUseCase>()

    private val savedStateHandle = SavedStateHandle()

    @Before
    fun setUp() {
        savedStateHandle["movieId"] = 2L
    }

    @Test
    fun `assert onPlayMovieTrailerReady is sent when onPlayTrailerClicked is called and uri is known`() =
        runTest(testDispatcher) {
            val testMovieDetailViewModel = MovieDetailViewModel(
                testDispatcher,
                mockObserveMovieDetailUseCase,
                mockManageMovieTrailerUseCase,
                savedStateHandle
            )

            testMovieDetailViewModel.oneTimeEventChannelFlow.test {
                testMovieDetailViewModel.onEvent(
                    MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                        1L,
                        true
                    )
                )
                assert(awaitItem() == MovieDetailUiEventModel.OnPlayMovieTrailerReady(""))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `assert ErrorFetchingTrailer is sent when ManageMovieTrailerUseCase encounters an error`() =
        runTest(testDispatcher) {
            val testMovieDetailViewModel = MovieDetailViewModel(
                testDispatcher,
                mockObserveMovieDetailUseCase,
                mockManageMovieTrailerUseCase,
                savedStateHandle
            )


            every { mockManageMovieTrailerUseCase(any()) } returns flow {
                emit(
                    Result.Failure(
                        FetchingTrailerError("Error fetching trailer")
                    )
                )
            }

            testMovieDetailViewModel.onEvent(
                MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                    2L,
                    false
                )
            )

            testMovieDetailViewModel.oneTimeEventChannelFlow.test {
                assert(awaitItem() == MovieDetailUiEventModel.ErrorFetchingTrailer("Error fetching trailer"))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `assert OnPlayMovieTrailerReady is sent when ManageMovieTrailerUseCase returns a uri`() =
        runTest(testDispatcher) {
            val testMovieDetailViewModel = MovieDetailViewModel(
                testDispatcher,
                mockObserveMovieDetailUseCase,
                mockManageMovieTrailerUseCase,
                savedStateHandle
            )

            every { mockManageMovieTrailerUseCase(any()) } returns flow {
                emit(Result.Success(MovieTrailerReady("fakeVideoUri")))
            }

            testMovieDetailViewModel.onEvent(
                MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                    2L,
                    false
                )
            )

            testMovieDetailViewModel.oneTimeEventChannelFlow.test {
                assert(awaitItem() == MovieDetailUiEventModel.OnPlayMovieTrailerReady("fakeVideoUri"))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `assert viewState sends Error when ObserveMovieDetailUseCase encounters an error`() =
        runTest(testDispatcher) {
            savedStateHandle["movieId"] = 4L

            every { mockObserveMovieDetailUseCase(any()) } returns flow {
                emit(Result.Loading)
                emit(
                    Result.Failure(
                        ObserveMovieErrors.Error("Error while observing movie")
                    )
                )
            }

            val testMovieDetailViewModel = MovieDetailViewModel(
                testDispatcher,
                mockObserveMovieDetailUseCase,
                mockManageMovieTrailerUseCase,
                savedStateHandle
            )

            testMovieDetailViewModel.viewState.test {
                assertEquals(awaitItem(), MovieDetailViewStateModel.Loading)
                assertEquals(awaitItem(), MovieDetailViewStateModel.Error(""))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `assert viewState sends MovieDetail when ObserveMovieDetailUseCase returns a movie`() =
        runTest(testDispatcher) {
            savedStateHandle["movieId"] = 5L
            every { mockObserveMovieDetailUseCase(any()) } returns flow {
                emit(Result.Loading)
                emit(
                    Result.Success(
                        ObserveMovieSuccess.Success(
                            MovieDetailDomainModel(title = "testTitle")
                        )
                    )
                )
            }

            val testMovieDetailViewModel = MovieDetailViewModel(
                testDispatcher,
                mockObserveMovieDetailUseCase,
                mockManageMovieTrailerUseCase,
                savedStateHandle
            )

            testMovieDetailViewModel.viewState.test {
                assertEquals(awaitItem(), MovieDetailViewStateModel.Loading)
                assertEquals(
                    awaitItem(),
                    MovieDetailViewStateModel.Success(MovieDetailUiModel(title = "testTitle"))
                )
            }
        }
}
