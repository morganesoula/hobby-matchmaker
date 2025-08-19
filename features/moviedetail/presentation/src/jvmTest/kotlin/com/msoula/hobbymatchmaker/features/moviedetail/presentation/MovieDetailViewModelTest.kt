package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.FetchingTrailerErrorHMM
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.MovieTrailerReady
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieErrors
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.fakes.FakeMovieDetailErrorMessageProvider
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailViewStateModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.toMovieDetailUiModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest : FunSpec({

    val dispatcher = StandardTestDispatcher()

    val observeMovieDetailUseCase = mockk<ObserveMovieDetailUseCase>()
    val manageMovieTrailerUseCase = mockk<ManageMovieTrailerUseCase>()
    val connectivityCheck = mockk<NetworkConnectivityChecker>()
    val fakeErrorMessageProvider = FakeMovieDetailErrorMessageProvider()

    lateinit var movieDetailVM: MovieDetailViewModel

    context("viewState - Success") {
        test("should emit Success when observeMovieDetail returns existing movie") {
            val fakeMovie = MovieDetailDomainModel(
                id = 42L, title = "fake movie detail title"
            )
            val successfulResult = flowOf(Result.Success(ObserveMovieSuccess.Success(fakeMovie)))

            coEvery { observeMovieDetailUseCase(any()) } returns successfulResult

            runTest(dispatcher) {
                movieDetailVM = MovieDetailViewModel(
                    42L,
                    dispatcher,
                    observeMovieDetailUseCase,
                    manageMovieTrailerUseCase,
                    connectivityCheck,
                    fakeErrorMessageProvider
                )

                movieDetailVM.viewState.test {
                    awaitItem() shouldBe MovieDetailViewStateModel.Loading
                    awaitItem() shouldBe MovieDetailViewStateModel.Success(fakeMovie.toMovieDetailUiModel())
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        test("should emit Empty when observeMovieDetail returns DataLoadedInDB") {
            val successfulResult = flowOf(Result.Success(ObserveMovieSuccess.DataLoadedInDB))

            coEvery { observeMovieDetailUseCase(any()) } returns successfulResult

            runTest(dispatcher) {
                movieDetailVM = MovieDetailViewModel(
                    42L,
                    dispatcher,
                    observeMovieDetailUseCase,
                    manageMovieTrailerUseCase,
                    connectivityCheck,
                    fakeErrorMessageProvider
                )

                movieDetailVM.viewState.test {
                    awaitItem() shouldBe MovieDetailViewStateModel.Loading
                    awaitItem() shouldBe MovieDetailViewStateModel.Empty
                }
            }
        }
    }

    context("viewState - Failure") {
        test("should emit Error when observeMovieDetail returns failure") {
            val failureResult = flowOf(Result.Failure(ObserveMovieErrors.MovieDetailErrorHMM))

            coEvery { observeMovieDetailUseCase(any()) } returns failureResult

            runTest(dispatcher) {
                movieDetailVM = MovieDetailViewModel(
                    42L,
                    dispatcher,
                    observeMovieDetailUseCase,
                    manageMovieTrailerUseCase,
                    connectivityCheck,
                    fakeErrorMessageProvider
                )

                movieDetailVM.viewState.test {
                    awaitItem() shouldBe MovieDetailViewStateModel.Loading
                    awaitItem() shouldBe MovieDetailViewStateModel.Error("")
                }
            }
        }
    }

    context("onPlayTrailerClicked - Video uri known") {
        test("should emit OnPlayMovieTrailerReady when network available") {
            coEvery { connectivityCheck.hasActiveConnection() } returns true

            runTest(dispatcher) {
                movieDetailVM = MovieDetailViewModel(
                    42L,
                    dispatcher,
                    observeMovieDetailUseCase,
                    manageMovieTrailerUseCase,
                    connectivityCheck,
                    fakeErrorMessageProvider
                )

                movieDetailVM.oneTimeEventChannelFlow.test {
                    movieDetailVM.onPlayTrailerClicked(42L, true)
                    advanceUntilIdle()

                    awaitItem() shouldBe MovieDetailUiEventModel.OnPlayMovieTrailerReady("")
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        test("should emit NoConnection no network available") {
            coEvery { connectivityCheck.hasActiveConnection() } returns false

            runTest(dispatcher) {
                movieDetailVM = MovieDetailViewModel(
                    42L,
                    dispatcher,
                    observeMovieDetailUseCase,
                    manageMovieTrailerUseCase,
                    connectivityCheck,
                    fakeErrorMessageProvider
                )

                movieDetailVM.oneTimeEventChannelFlow.test {
                    movieDetailVM.onPlayTrailerClicked(42L, true)
                    advanceUntilIdle()

                    awaitItem() shouldBe MovieDetailUiEventModel.NoConnection
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }

    context("onPlayTrailerClicked - video uri unknown") {
        test("should emit Success when manageMovieTrailer returns success") {
            val successfulResult = flowOf(
                Result.Success(
                    MovieTrailerReady("random uri")
                )
            )

            coEvery { manageMovieTrailerUseCase(any()) } returns successfulResult

            runTest(dispatcher) {
                movieDetailVM = MovieDetailViewModel(
                    42L,
                    dispatcher,
                    observeMovieDetailUseCase,
                    manageMovieTrailerUseCase,
                    connectivityCheck,
                    fakeErrorMessageProvider
                )

                movieDetailVM.oneTimeEventChannelFlow.test {
                    movieDetailVM.onPlayTrailerClicked(42L, false)
                    awaitItem() shouldBe MovieDetailUiEventModel.OnPlayMovieTrailerReady(
                        "random uri"
                    )
                }
            }
        }

        test("should emit NoConnection when manageMovieTrailer fails with no network") {
            val failureResult = flowOf(
                Result.Failure(FetchingTrailerErrorHMM.NoConnectionErrorHMM("No network!"))
            )

            coEvery { manageMovieTrailerUseCase(any()) } returns failureResult

            runTest(dispatcher) {
                movieDetailVM = MovieDetailViewModel(
                    42L,
                    dispatcher,
                    observeMovieDetailUseCase,
                    manageMovieTrailerUseCase,
                    connectivityCheck,
                    fakeErrorMessageProvider
                )

                movieDetailVM.oneTimeEventChannelFlow.test {
                    movieDetailVM.onPlayTrailerClicked(42L, false)

                    awaitItem() shouldBe MovieDetailUiEventModel.NoConnection
                }
            }
        }

        test("should emit ErrorFetchingTrailer when manageMovieTrailer fails") {
            val failureResult = flowOf(
                Result.Failure(FetchingTrailerErrorHMM.NoTrailerFoundErrorHMM("Error fetching"))
            )

            coEvery { manageMovieTrailerUseCase(any()) } returns failureResult

            runTest(dispatcher) {
                movieDetailVM = MovieDetailViewModel(
                    42L,
                    dispatcher,
                    observeMovieDetailUseCase,
                    manageMovieTrailerUseCase,
                    connectivityCheck,
                    fakeErrorMessageProvider
                )

                movieDetailVM.oneTimeEventChannelFlow.test {
                    movieDetailVM.onPlayTrailerClicked(42L, false)

                    awaitItem() shouldBe MovieDetailUiEventModel.ErrorFetchingTrailer
                }
            }
        }
    }
})
