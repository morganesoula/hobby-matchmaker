package com.msoula.hobbymatchmaker.feature.moviedetail.presentation

import androidx.compose.runtime.mutableStateOf
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
import io.mockk.mockkClass
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
import org.junit.Assert.*

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

    private var fetchStatusFlow = mutableStateOf<FetchStatusModel>(FetchStatusModel.NeverFetched)

    private var movieIdFlow = mutableStateOf<Long?>(null)

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
    fun `when data exists and fetchStatus is an error, should return an Error`() =
        testScope.runTest {
            val errorMessage = "An error occurred"
            coEvery { observeMovieDetailUseCase(1) } returns flowOf(
                MovieDetailDomainModel(
                    synopsis = "Some synopsis"
                )
            )
            fetchStatusFlow.value = FetchStatusModel.Error(errorMessage)

            movieDetailViewModel.viewState.test {
                val result = awaitItem()
                println("Result: $result")
                assertTrue(result is MovieDetailViewStateModel.Error)
                assertEquals(errorMessage, (result as MovieDetailViewStateModel.Error).error)
            }

        }

    @Test
    fun `when data exists and fetchStatus is success, should return a Success`() =
        testScope.runTest {
            fetchStatusFlow.value = FetchStatusModel.Success

            movieDetailViewModel.viewState.test {
                val result = awaitItem()

                if (result is MovieDetailViewStateModel.Success) {
                    assertEquals(result, MovieDetailViewStateModel.Success(MovieDetailUiModel()))
                }
            }
        }
}
