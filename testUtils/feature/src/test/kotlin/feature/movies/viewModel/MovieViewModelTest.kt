package feature.movies.viewModel

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesErrors
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import com.msoula.hobbymatchmaker.testUtils.common.fakes.FakeResourcesProvider
import feature.MainDispatcherRule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MovieViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val fakeSetMovieFavoriteUseCase = mockk<SetMovieFavoriteUseCase>()
    val mockObserveAllMoviesUseCase = mockk<ObserveAllMoviesUseCase>()
    private val fakeGetUserInfo = mockk<FetchFirebaseUserInfo>()
    private val fakeResourceProvider = FakeResourcesProvider()

    @Test
    fun `should emit Success when data exists in DB`() = runTest(testDispatcher)
    {
        val moviesFlow = MutableStateFlow(
            Result.Success(
                ObserveAllMoviesSuccess.Success(
                    listOf(
                        MovieDomainModel(
                            1,
                            "Test movie"
                        )
                    )
                )
            )
        )

        every { mockObserveAllMoviesUseCase(any()) } returns moviesFlow

        val testMovieViewModel = MovieViewModel(
            fakeSetMovieFavoriteUseCase,
            mockObserveAllMoviesUseCase,
            fakeGetUserInfo,
            testDispatcher,
            fakeResourceProvider
        )

        testMovieViewModel.movieState.test {
            assert(awaitItem() is MovieUiStateModel.Loading)
            val successState = awaitItem() as MovieUiStateModel.Success
            assert(successState.list.first().title == "Test movie")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit Error when data does not exist in DB`() = runTest(testDispatcher) {
        every { mockObserveAllMoviesUseCase(any()) } returns flowOf(
            Result.Failure(
                ObserveAllMoviesErrors.Empty
            )
        )

        val testMovieViewModel = MovieViewModel(
            fakeSetMovieFavoriteUseCase,
            mockObserveAllMoviesUseCase,
            fakeGetUserInfo,
            testDispatcher,
            fakeResourceProvider
        )

        testMovieViewModel.movieState.test {
            assert(awaitItem() is MovieUiStateModel.Loading)
            assert(awaitItem() is MovieUiStateModel.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should switch favorite value when toggleFavorite is called`() = runTest(testDispatcher) {
        val movieId = 1L
        val isFavorite = true
        val fakeUserId = "fake-user-id"

        coEvery { fakeGetUserInfo() } returns FirebaseUserInfoDomainModel(
            fakeUserId,
            "",
            emptyList()
        )

        coEvery { fakeSetMovieFavoriteUseCase(fakeUserId, movieId, !isFavorite) } just Runs

        every { mockObserveAllMoviesUseCase(any()) } returns flowOf()

        val testMovieViewModel = MovieViewModel(
            fakeSetMovieFavoriteUseCase,
            mockObserveAllMoviesUseCase,
            fakeGetUserInfo,
            testDispatcher,
            fakeResourceProvider
        )

        testMovieViewModel.onCardEvent(
            CardEventModel.OnDoubleTap(
                MovieUiModel(
                    movieId,
                    title = "Fake title movie",
                    isFavorite = isFavorite,
                    overview = null,
                    coverFilePath = ""
                )
            )
        )

        advanceUntilIdle()
        coVerify { fakeSetMovieFavoriteUseCase(fakeUserId, movieId, !isFavorite) }
    }

    @Test
    fun `verify sendOnce is called when singleTap`() = runTest(testDispatcher) {
        every { mockObserveAllMoviesUseCase(any()) } returns flowOf()

        val testMovieViewModel = MovieViewModel(
            fakeSetMovieFavoriteUseCase,
            mockObserveAllMoviesUseCase,
            fakeGetUserInfo,
            testDispatcher,
            fakeResourceProvider
        )

        testMovieViewModel.oneTimeEventChannelFlow.test {
            testMovieViewModel.onCardEvent(CardEventModel.OnSingleTap(1L, ""))
            assert(awaitItem() == MovieUiEventModel.OnMovieDetailClicked(1L))
            cancelAndIgnoreRemainingEvents()
        }
    }
}
