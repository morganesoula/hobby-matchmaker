package com.msoula.hobbymatchmaker.features.movies.presentation

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.CheckMovieSynopsisValueUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesErrors
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.toMovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModelTest : FunSpec({
    val dispatcher = StandardTestDispatcher()

    val setMovieFavoriteUseCase = mockk<SetMovieFavoriteUseCase>(relaxed = true)
    val observeAllMoviesUseCase = mockk<ObserveAllMoviesUseCase>()
    val getUserInfo = mockk<FetchFirebaseUserInfo>()
    val logOutUseCase = mockk<LogOutUseCase>()
    val checkMovieSynopsisValueUseCase = mockk<CheckMovieSynopsisValueUseCase>()
    val connectivityCheck = mockk<NetworkConnectivityChecker>()
    val errorMessageProvider = mockk<ErrorMessageProvider>()

    lateinit var movieViewModel: MovieViewModel

    context("MovieState - Success") {
        test("should emit Success when observeAllMoviesUseCase returns success with data") {
            val movie = MovieDomainModel(id = 1L, title = "Movie test title")
            val successfulResult = Result.Success(ObserveAllMoviesSuccess.Success(listOf(movie)))

            runTest(dispatcher) {
                coEvery { observeAllMoviesUseCase(any()) } returns flowOf(successfulResult)

                movieViewModel = MovieViewModel(
                    setMovieFavoriteUseCase,
                    observeAllMoviesUseCase,
                    getUserInfo,
                    logOutUseCase,
                    checkMovieSynopsisValueUseCase,
                    connectivityCheck,
                    dispatcher,
                    errorMessageProvider
                )

                movieViewModel.movieState.test {
                    awaitItem() shouldBe MovieUiStateModel.Loading
                    awaitItem() shouldBe MovieUiStateModel.Success(listOf(movie.toMovieUiModel()))
                }
            }
        }

        test("should emit Empty when observeAllMoviesUseCase returns DataLoadedInDB") {
            val successfulResult = Result.Success(ObserveAllMoviesSuccess.DataLoadedInDB)

            runTest(dispatcher) {
                coEvery { observeAllMoviesUseCase(any()) } returns flowOf(successfulResult)

                movieViewModel = MovieViewModel(
                    setMovieFavoriteUseCase,
                    observeAllMoviesUseCase,
                    getUserInfo,
                    logOutUseCase,
                    checkMovieSynopsisValueUseCase,
                    connectivityCheck,
                    dispatcher,
                    errorMessageProvider
                )

                movieViewModel.movieState.test {
                    awaitItem() shouldBe MovieUiStateModel.Loading
                    awaitItem() shouldBe MovieUiStateModel.Empty
                }
            }
        }
    }

    context("MovieState - Failure") {
        test("should emit Error when observeAllMoviesUseCase returns failure") {
            val errorResult = Result.Failure(
                ObserveAllMoviesErrors.UnknownErrorHMM("Unknown error dear")
            )

            runTest(dispatcher) {
                coEvery { observeAllMoviesUseCase(any()) } returns flowOf(errorResult)
                coEvery { errorMessageProvider.getMessage(any()) } returns "Unknown error dear"

                movieViewModel = MovieViewModel(
                    setMovieFavoriteUseCase,
                    observeAllMoviesUseCase,
                    getUserInfo,
                    logOutUseCase,
                    checkMovieSynopsisValueUseCase,
                    connectivityCheck,
                    dispatcher,
                    errorMessageProvider
                )

                movieViewModel.movieState.test {
                    awaitItem() shouldBe MovieUiStateModel.Loading
                    advanceUntilIdle()
                    awaitItem() shouldBe MovieUiStateModel.Error("Unknown error dear")
                }
            }
        }
    }

    context("LogOut - Success") {
        test("should send OnLogOutSuccess when log out succeeds") {
            val successfulResult = flowOf(Result.Success(LogOutSuccess))

            runTest(dispatcher) {
                coEvery { logOutUseCase(any()) } returns successfulResult

                movieViewModel = MovieViewModel(
                    setMovieFavoriteUseCase,
                    observeAllMoviesUseCase,
                    getUserInfo,
                    logOutUseCase,
                    checkMovieSynopsisValueUseCase,
                    connectivityCheck,
                    dispatcher,
                    errorMessageProvider
                )

                movieViewModel.oneTimeEventChannelFlow.test {
                    movieViewModel.logOut()

                    awaitItem() shouldBe MovieUiEventModel.OnLogOutSuccess
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }

    context("Logout - Failure") {
        test("should return Error when logOut fails") {
            val errorResult = flowOf(
                Result.Failure(
                    LogOutErrorHMM.UnknownErrorHMM("Error dear while logging out")
                )
            )

            runTest(dispatcher) {
                coEvery { logOutUseCase(any()) } returns errorResult

                movieViewModel = MovieViewModel(
                    setMovieFavoriteUseCase,
                    observeAllMoviesUseCase,
                    getUserInfo,
                    logOutUseCase,
                    checkMovieSynopsisValueUseCase,
                    connectivityCheck,
                    dispatcher,
                    errorMessageProvider
                )

                movieViewModel.oneTimeEventChannelFlow.test {
                    movieViewModel.logOut()

                    awaitItem() shouldBe MovieUiEventModel.OnLogOutFailure(
                        "Error dear while logging out"
                    )
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }

    context("onCardEvent - onDoubleTap") {
        test("should call setMovieFavoriteUseCase with value false -> true") {
            val fakeMovie = MovieDomainModel(
                id = 42L, title = "Random title", isFavorite = true
            )

            val expectedUserId = "user-123"
            val fakeUIModel = fakeMovie.toMovieUiModel()

            coEvery { getUserInfo() } returns FirebaseUserInfoDomainModel(
                uid = expectedUserId, email = "", providers = emptyList()
            )
            coEvery { setMovieFavoriteUseCase(any(), any(), any()) } just Runs

            runTest(dispatcher) {
                movieViewModel = MovieViewModel(
                    setMovieFavoriteUseCase,
                    observeAllMoviesUseCase,
                    getUserInfo,
                    logOutUseCase,
                    checkMovieSynopsisValueUseCase,
                    connectivityCheck,
                    dispatcher,
                    errorMessageProvider
                )

                movieViewModel.onCardEvent(CardEventModel.OnDoubleTap(fakeUIModel))

                advanceUntilIdle()
                coVerify {
                    setMovieFavoriteUseCase(
                        expectedUserId,
                        42L,
                        false
                    )
                }
            }
        }

        test("should call setMovieFavoriteUseCase with value true -> false") {
            val fakeMovie = MovieDomainModel(
                id = 42L, title = "Random title"
            )

            val expectedUserId = "user-123"
            val fakeUIModel = fakeMovie.toMovieUiModel()

            coEvery { getUserInfo() } returns FirebaseUserInfoDomainModel(
                uid = expectedUserId, email = "", providers = emptyList()
            )
            coEvery { setMovieFavoriteUseCase(any(), any(), any()) } just Runs

            runTest(dispatcher) {
                movieViewModel = MovieViewModel(
                    setMovieFavoriteUseCase,
                    observeAllMoviesUseCase,
                    getUserInfo,
                    logOutUseCase,
                    checkMovieSynopsisValueUseCase,
                    connectivityCheck,
                    dispatcher,
                    errorMessageProvider
                )

                movieViewModel.onCardEvent(CardEventModel.OnDoubleTap(fakeUIModel))

                advanceUntilIdle()
                coVerify {
                    setMovieFavoriteUseCase(
                        expectedUserId,
                        42L,
                        true
                    )
                }
            }
        }
    }

    context("onCardEvent - onSingleTap") {
        test("should return NoFetchingDetailPossible when no network and localData") {
            coEvery { checkMovieSynopsisValueUseCase(any()) } returns false
            coEvery { connectivityCheck.hasActiveConnection() } returns false

            runTest(dispatcher) {
                movieViewModel = MovieViewModel(
                    setMovieFavoriteUseCase,
                    observeAllMoviesUseCase,
                    getUserInfo,
                    logOutUseCase,
                    checkMovieSynopsisValueUseCase,
                    connectivityCheck,
                    dispatcher,
                    errorMessageProvider
                )

                movieViewModel.oneTimeEventChannelFlow.test {
                    movieViewModel.onCardEvent(CardEventModel.OnSingleTap(42L, ""))

                    awaitItem() shouldBe MovieUiEventModel.NoFetchingDetailPossible
                }
            }
        }
    }

    context("handleSingleTap") {
        test("should return OnMovieDetailClicked when synopsis exists but has no network") {
            coEvery { checkMovieSynopsisValueUseCase(any()) } returns true
            coEvery { connectivityCheck.hasActiveConnection() } returns false

            runTest(dispatcher) {
                movieViewModel = MovieViewModel(
                    setMovieFavoriteUseCase,
                    observeAllMoviesUseCase,
                    getUserInfo,
                    logOutUseCase,
                    checkMovieSynopsisValueUseCase,
                    connectivityCheck,
                    dispatcher,
                    errorMessageProvider
                )

                val result = movieViewModel.handleSingleTap(42L)
                result shouldBe MovieUiEventModel.OnMovieDetailClicked(42L)
            }
        }

        test("should return OnMovieDetailClicked when has network but no local data") {
            coEvery { checkMovieSynopsisValueUseCase(any()) } returns false
            coEvery { connectivityCheck.hasActiveConnection() } returns true

            runTest(dispatcher) {
                movieViewModel = MovieViewModel(
                    setMovieFavoriteUseCase,
                    observeAllMoviesUseCase,
                    getUserInfo,
                    logOutUseCase,
                    checkMovieSynopsisValueUseCase,
                    connectivityCheck,
                    dispatcher,
                    errorMessageProvider
                )

                val result = movieViewModel.handleSingleTap(42L)
                result shouldBe MovieUiEventModel.OnMovieDetailClicked(42L)
            }
        }

        test("should return OnMovieDetailClicked when has network and local data") {
            coEvery { checkMovieSynopsisValueUseCase(any()) } returns true
            coEvery { connectivityCheck.hasActiveConnection() } returns true

            runTest(dispatcher) {
                movieViewModel = MovieViewModel(
                    setMovieFavoriteUseCase,
                    observeAllMoviesUseCase,
                    getUserInfo,
                    logOutUseCase,
                    checkMovieSynopsisValueUseCase,
                    connectivityCheck,
                    dispatcher,
                    errorMessageProvider
                )

                val result = movieViewModel.handleSingleTap(42L)
                result shouldBe MovieUiEventModel.OnMovieDetailClicked(42L)
            }
        }
    }
})
