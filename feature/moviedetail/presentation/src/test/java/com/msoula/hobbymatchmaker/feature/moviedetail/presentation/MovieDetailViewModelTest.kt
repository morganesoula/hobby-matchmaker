package com.msoula.hobbymatchmaker.feature.moviedetail.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieVideoDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.FetchMovieDetailTrailerUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.FetchMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.UpdateMovieVideoURIUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.FetchStatusModel
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.MovieDetailViewStateModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    private lateinit var movieDetailViewModel: MovieDetailViewModel
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    private var fetchMovieDetailTrailerUseCase =
        mockk<FetchMovieDetailTrailerUseCase>(relaxed = true)
    private var updateMovieVideoURIUseCase = mockk<UpdateMovieVideoURIUseCase>(relaxed = true)
    private var fetchMovieDetailUseCase = mockk<FetchMovieDetailUseCase>(relaxed = true)
    private var observeMovieDetailUseCase = mockk<ObserveMovieDetailUseCase>(relaxed = true)

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        testScope = TestScope(testDispatcher)

        coEvery { observeMovieDetailUseCase(1) } returns flowOf(MovieDetailDomainModel(synopsis = "Some synopsis"))

        coEvery { updateMovieVideoURIUseCase(1, "video_uri_test") }
        coEvery {
            fetchMovieDetailUseCase(
                1,
                "fr-Fr"
            )
        } returns Result.Success(true)

        coEvery { fetchMovieDetailTrailerUseCase(1, "fr-Fr") } returns Result.Success(
            MovieVideoDomainModel("test_video_key", "Trailer", "Youtube")
        )

        movieDetailViewModel = MovieDetailViewModel(
            fetchMovieDetailTrailerUseCase = fetchMovieDetailTrailerUseCase,
            fetchMovieDetailUseCase = fetchMovieDetailUseCase,
            updateMovieVideoURIUseCase = updateMovieVideoURIUseCase,
            observeMovieDetailUseCase = observeMovieDetailUseCase,
            ioDispatcher = testDispatcher,
            savedStateHandle = SavedStateHandle().apply {
                set("movieId", 1L)
            }
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when synopsis is empty and fetchStatus is an error, should return an Error`() =
        testScope.runTest {
            coEvery { observeMovieDetailUseCase(1) } returns flowOf(MovieDetailDomainModel(title = "Testing title"))

            movieDetailViewModel.fetchStatusFlow.value = FetchStatusModel.Error("Error happened")

            movieDetailViewModel.viewState.test {
                val result = awaitItem()

                if (result is MovieDetailViewStateModel.Error) {
                    assertEquals("Error happened", result.error)
                }
            }
        }

    @Test
    fun `when synopsis is empty and fetchStatus is Loading, should return a Loading`() =
        testScope.runTest {
            coEvery { observeMovieDetailUseCase(1) } returns flowOf(MovieDetailDomainModel(title = "Testing title"))
            movieDetailViewModel.fetchStatusFlow.value = FetchStatusModel.Loading

            movieDetailViewModel.viewState.test {
                val result = awaitItem()

                assertEquals(result, MovieDetailViewStateModel.Loading)
            }
        }

    @Test
    fun `when synopsis is empty and fetchStatus is Success, should return an Empty`() =
        testScope.runTest {
            coEvery { observeMovieDetailUseCase(1) } returns flowOf(MovieDetailDomainModel(title = "Testing title"))
            movieDetailViewModel.fetchStatusFlow.value = FetchStatusModel.Success

            movieDetailViewModel.viewState.test {
                val result = awaitItem()
                assertEquals(result, MovieDetailViewStateModel.Empty)
            }
        }

    @Test
    fun `when synopsis is filled and fetchStatus is not Error, should return data`() =
        testScope.runTest {
            coEvery { observeMovieDetailUseCase(1) } returns flowOf(
                MovieDetailDomainModel(
                    title = "Testing title",
                    synopsis = "Some synopsis"
                )
            )

            movieDetailViewModel.viewState.test {
                val result = awaitItem()
                assertEquals(
                    result,
                    MovieDetailViewStateModel.Success(
                        MovieDetailUiModel(
                            title = "Testing title",
                            synopsis = "Some synopsis"
                        )
                    )
                )
            }
        }
}




