package com.msoula.hobbymatchmaker.features.movies.presentation

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.FetchMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.FetchStatusModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModelTest {

    private lateinit var viewModel: MovieViewModel
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

    private val setMovieFavoriteUseCase = mockk<SetMovieFavoriteUseCase>()

    private val fetchMoviesUseCase = mockk<FetchMoviesUseCase>(relaxed = true)
    private val observeAllMoviesUseCase = mockk<ObserveAllMoviesUseCase>(relaxed = true)
    private val fetchStatusFlow = MutableStateFlow<FetchStatusModel>(FetchStatusModel.NeverFetched)
    private val getUserInfo = mockk<FetchFirebaseUserInfo>(relaxed = true)

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        testScope = TestScope(testDispatcher)

        coEvery { fetchMoviesUseCase("fr-FR") } returns Result.Success(Unit)

        viewModel = MovieViewModel(
            setMovieFavoriteUseCase = setMovieFavoriteUseCase,
            observeAllMoviesUseCase = observeAllMoviesUseCase,
            fetchMoviesUseCase = fetchMoviesUseCase,
            getUserInfo = getUserInfo,
            ioDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when movies are empty and fetch status is loading, movieState should be Loading`() =
        testScope.runTest {
            fetchStatusFlow.value = FetchStatusModel.Loading

            coEvery { observeAllMoviesUseCase() } returns flowOf(emptyList())

            viewModel.movieState.test {
                assertEquals(MovieUiStateModel.Loading, awaitItem())
            }
        }

    @Test
    fun `when movies are empty and fetch status is error, movieState should be Error`() =
        testScope.runTest {
            val errorMessage = "Error while fetching movies"

            fetchStatusFlow.value = FetchStatusModel.Error(errorMessage)
            coEvery { observeAllMoviesUseCase() } returns flowOf(emptyList())

            viewModel.movieState.test {
                val result = awaitItem()

                if (result is MovieUiStateModel.Error) {
                    assertEquals(MovieUiStateModel.Error(errorMessage), result)
                }
            }
        }

    @Test
    fun `when movies are empty and fetch status is success, movieState should be Empty`() =
        testScope.runTest {
            fetchStatusFlow.value = FetchStatusModel.Success
            coEvery { observeAllMoviesUseCase() } returns flowOf(emptyList())

            viewModel.movieState.test {
                val result = awaitItem()

                if (result is MovieUiStateModel.Success) {
                    assertTrue(result.list.isEmpty())
                }
            }
        }

    @Test
    fun `when movies are present, movieState should be Success with movies list`() =
        testScope.runTest {
            val mockMovies = listOf(
                MovieDomainModel(
                    1,
                    title = "Title 1",
                    isFavorite = false,
                    localCoverFilePath = "",
                    overview = ""
                )
            )
            coEvery { observeAllMoviesUseCase() } returns flowOf(mockMovies)

            fetchStatusFlow.value = FetchStatusModel.Success

            viewModel.movieState.test {
                val result = awaitItem()

                if (result is MovieUiStateModel.Success) {
                    assertTrue(result.list.isNotEmpty())
                    assertEquals(mockMovies.size, result.list.size)
                }
            }
        }

    @Test
    fun `onCardEvent onDoubleTap should toggle movie favorite status`() = testScope.runTest {
        val movie = MovieUiModel(
            id = 1,
            title = "Test Movie",
            isFavorite = false,
            coverFilePath = "",
            overview = ""
        )

        coEvery { observeAllMoviesUseCase() } returns flowOf(
            listOf(
                MovieDomainModel(
                    id = 1,
                    title = "Test Movie",
                    isFavorite = false,
                    localCoverFilePath = "",
                    overview = ""
                )
            )
        )

        coEvery { setMovieFavoriteUseCase("uuidUser1", movie.id, true) } returns Unit
        coEvery { getUserInfo() } returns FirebaseUserInfoDomainModel("uuidUser1", "test@test.fr", null)

        val event = CardEventModel.OnDoubleTap(movie)
        viewModel.onCardEvent(event)

        coVerify { setMovieFavoriteUseCase("uuidUser1", movie.id, true) }
    }
}
