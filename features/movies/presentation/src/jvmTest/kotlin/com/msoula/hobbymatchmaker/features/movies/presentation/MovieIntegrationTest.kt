package com.msoula.hobbymatchmaker.features.movies.presentation

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepositoryImpl
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.CheckMovieSynopsisValueUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.FetchMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.fakes.FakeConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.presentation.fakes.FakeMovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.presentation.fakes.FakeMovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.MoviesErrorMessageProvider
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.toMovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherRule::class)
class MovieIntegrationTest : FunSpec({
    val dispatcher = MainDispatcherRule().testDispatcher
    lateinit var movieViewModel: MovieViewModel

    val fakeList = listOf(
        MovieDomainModel(
            id = 1L,
            title = "Test title 1",
            coverFileName = "Remote cover file 1",
            localCoverFilePath = "Local cover file 1",
            isFavorite = false,
            isSeen = false,
            overview = "Test overview 1"
        )
    )

    val fakeListToMovieUI = fakeList.map { it.toMovieUiModel() }

    val fakeMovieLocalDataSource = FakeMovieLocalDataSource()
    val fakeMovieRemoteDataSource = FakeMovieRemoteDataSource()

    val movieRepository = MovieRepositoryImpl(
        fakeMovieRemoteDataSource, fakeMovieLocalDataSource
    )

    val setMovieFavoriteUseCase = SetMovieFavoriteUseCase(movieRepository)
    val fetchMoviesUseCase = FetchMoviesUseCase(movieRepository)
    val observeAllMoviesUseCase =
        ObserveAllMoviesUseCase(movieRepository, fetchMoviesUseCase, dispatcher)

    val mockLogOutUseCase = mockk<LogOutUseCase>()
    val mockGetUserInfoUseCase = mockk<FetchFirebaseUserInfo>()

    val checkMovieSynopsisValueUseCase = CheckMovieSynopsisValueUseCase(movieRepository)
    val connectivityCheck = FakeConnectivityChecker()
    val errorMessageProvider = MoviesErrorMessageProvider()

    test("should emit Success.Empty when there is no data yet") {
        runTest {
            movieViewModel = MovieViewModel(
                setMovieFavoriteUseCase,
                observeAllMoviesUseCase,
                mockGetUserInfoUseCase,
                mockLogOutUseCase,
                checkMovieSynopsisValueUseCase,
                connectivityCheck,
                dispatcher,
                errorMessageProvider,
                this.backgroundScope
            )

            movieViewModel.movieState.test {
                awaitItem() shouldBe MovieUiStateModel.Loading
                awaitItem() shouldBe MovieUiStateModel.Empty
            }
        }
    }

    test("should emit Success with data when data is loaded in DB") {
        runTest {
            fakeMovieLocalDataSource.upsertAll(fakeList)

            movieViewModel = MovieViewModel(
                setMovieFavoriteUseCase,
                observeAllMoviesUseCase,
                mockGetUserInfoUseCase,
                mockLogOutUseCase,
                checkMovieSynopsisValueUseCase,
                connectivityCheck,
                dispatcher,
                errorMessageProvider,
                this.backgroundScope
            )

            movieViewModel.movieState.test {
                awaitItem() shouldBe MovieUiStateModel.Loading
                awaitItem() shouldBe MovieUiStateModel.Success(fakeListToMovieUI)
            }
        }
    }

    test("should set movie to favorite value") {
        runTest {
            coEvery { mockGetUserInfoUseCase()?.uid } returns "fake-uid"
            val movie = MovieDomainModel(
                id = 1L,
                title = "Inception",
                coverFileName = "cover file path",
                localCoverFilePath = "local cover file",
                isFavorite = false,
                isSeen = false,
                overview = "Dream within a dream"
            )
            fakeMovieLocalDataSource.upsertAll(listOf(movie))

            movieViewModel = MovieViewModel(
                setMovieFavoriteUseCase,
                observeAllMoviesUseCase,
                mockGetUserInfoUseCase,
                mockLogOutUseCase,
                checkMovieSynopsisValueUseCase,
                connectivityCheck,
                dispatcher,
                errorMessageProvider
            )

            val successState = awaitMovieSuccessState(movieViewModel)
            val movieInState = successState.list.first { it.id == movie.id }

            movieViewModel.onCardEvent(CardEventModel.OnDoubleTap(movieInState))
            advanceUntilIdle()

            val updatedState = awaitMovieSuccessState(movieViewModel)
            val updatedMovie = updatedState.list.first { it.id == movie.id }

            updatedMovie.isFavorite shouldBe true
        }
    }

    test("should emit OnMovieDetailClicked when overview is available and offline") {
        runTest {
            val networkCheckFalse = FakeConnectivityChecker(false)
            fakeMovieLocalDataSource.upsertAll(
                listOf(
                    MovieDomainModel(id = 3L, overview = "fake overview while offline")
                )
            )

            movieViewModel = MovieViewModel(
                setMovieFavoriteUseCase,
                observeAllMoviesUseCase,
                mockGetUserInfoUseCase,
                mockLogOutUseCase,
                checkMovieSynopsisValueUseCase,
                networkCheckFalse,
                dispatcher,
                errorMessageProvider,
                this.backgroundScope
            )

            movieViewModel.onCardEvent(
                CardEventModel.OnSingleTap(
                    3L, "fake overview while offline"
                )
            )
            advanceUntilIdle()

            movieViewModel.oneTimeEventChannelFlow.test {
                awaitItem() shouldBe MovieUiEventModel.OnMovieDetailClicked(3L)
            }
        }
    }

    test("should emit OnMovieDetailClicked when no overview and has network") {
        runTest {
            val networkCheckTrue = FakeConnectivityChecker(true)
            fakeMovieLocalDataSource.upsertAll(
                listOf(
                    MovieDomainModel(id = 4L)
                )
            )

            movieViewModel = MovieViewModel(
                setMovieFavoriteUseCase,
                observeAllMoviesUseCase,
                mockGetUserInfoUseCase,
                mockLogOutUseCase,
                checkMovieSynopsisValueUseCase,
                networkCheckTrue,
                dispatcher,
                errorMessageProvider,
                this.backgroundScope
            )

            movieViewModel.onCardEvent(
                CardEventModel.OnSingleTap(
                    4L, "fake overview while offline"
                )
            )
            advanceUntilIdle()

            movieViewModel.oneTimeEventChannelFlow.test {
                awaitItem() shouldBe MovieUiEventModel.OnMovieDetailClicked(4L)
            }
        }
    }

    test("should emit NoFetchingDetailPossible when no data known and no network") {
        runTest {
            val networkCheckTrue = FakeConnectivityChecker(false)
            fakeMovieLocalDataSource.upsertAll(
                listOf(
                    MovieDomainModel(id = 4L)
                )
            )

            movieViewModel = MovieViewModel(
                setMovieFavoriteUseCase,
                observeAllMoviesUseCase,
                mockGetUserInfoUseCase,
                mockLogOutUseCase,
                checkMovieSynopsisValueUseCase,
                networkCheckTrue,
                dispatcher,
                errorMessageProvider,
                this.backgroundScope
            )

            movieViewModel.onCardEvent(
                CardEventModel.OnSingleTap(
                    4L, ""
                )
            )
            advanceUntilIdle()

            movieViewModel.oneTimeEventChannelFlow.test {
                awaitItem() shouldBe MovieUiEventModel.NoFetchingDetailPossible
            }
        }
    }
})

private suspend fun awaitMovieSuccessState(viewModel: MovieViewModel): MovieUiStateModel.Success {
    return viewModel.movieState.filterIsInstance<MovieUiStateModel.Success>().first()
}
