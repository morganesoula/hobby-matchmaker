package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.common.UIText
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.MovieTrailerReady
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailViewStateModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest : FunSpec({

    val scheduler = TestCoroutineScheduler()
    val dispatcher = StandardTestDispatcher(scheduler)
    val testScope = TestScope(dispatcher + Job())

    lateinit var observeMovieDetailUseCase: ObserveMovieDetailUseCase
    lateinit var manageMovieTrailerUseCase: ManageMovieTrailerUseCase
    lateinit var connectivity: NetworkConnectivityChecker
    val errorMapper = object : ErrorMessageMapper {
        override fun toUIText(error: AppError): UIText {
            return UIText.Plain("err")
        }
    }

    lateinit var detailFlow: MutableSharedFlow<AppResult<ObserveMovieSuccess, AppError>>

    suspend fun pump() {
        scheduler.runCurrent()
    }

    fun buildVM(movieId: Long = 42L): MovieDetailViewModel {
        detailFlow = MutableSharedFlow(replay = 1)

        observeMovieDetailUseCase = mockk {
            every { this@mockk(any(), any()) } returns detailFlow
        }
        manageMovieTrailerUseCase = mockk(relaxed = true)
        connectivity = mockk(relaxed = true)

        return MovieDetailViewModel(
            movieId = movieId,
            observeMovieDetailUseCase = observeMovieDetailUseCase,
            manageMovieTrailerUseCase = manageMovieTrailerUseCase,
            connectivityCheck = connectivity,
            defaultErrorMessageMapper = errorMapper,
            externalScope = testScope
        )
    }

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }
    afterSpec {
        Dispatchers.resetMain()
        unmockkAll()
    }
    beforeTest {
        clearAllMocks()
    }


    test("initial state is Loading") {
        val vm = buildVM()
        vm.viewState.value shouldBe MovieDetailViewStateModel.Loading
    }

    test("viewState stays Loading when DataLoadedInDB arrives") {
        val vm = buildVM()

        vm.viewState.test {
            awaitItem() shouldBe MovieDetailViewStateModel.Loading

            pump()
            detailFlow.emit(AppResult.Success(ObserveMovieSuccess.DataLoadedInDB))
            pump()

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("viewState -> Error when upstream emits Failure") {
        val vm = buildVM()

        vm.viewState.test {
            awaitItem() shouldBe MovieDetailViewStateModel.Loading

            pump()
            detailFlow.emit(AppResult.Failure(AppError.Domain.NotFound))
            pump()

            val err = awaitItem() as MovieDetailViewStateModel.Error
            (err.error as UIText.Plain).value shouldBe "err"
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("OnPlayMovieTrailerClicked with isVideoURIknown=true & network ON -> emits Ready(with currentMovie.videoKey or empty)") {
        val vm = buildVM()
        every { connectivity.hasActiveConnection() } returns true

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(
                MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                    movieId = 42L,
                    isVideoURIknown = true
                )
            )
            pump()
            awaitItem() shouldBe MovieDetailUiEventModel.OnPlayMovieTrailerReady("")
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("OnPlayMovieTrailerClicked with isVideoURIknown=true & network OFF -> emits NoConnection") {
        val vm = buildVM()
        every { connectivity.hasActiveConnection() } returns false

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(
                MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                    movieId = 42L,
                    isVideoURIknown = true
                )
            )
            pump()
            awaitItem() shouldBe MovieDetailUiEventModel.NoConnection
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("OnPlayMovieTrailerClicked with isVideoURIknown=false -> emits Loading then NoConnection on connectivity failure") {
        val vm = buildVM()
        coEvery { manageMovieTrailerUseCase.invoke(any(), any()) } returns
            AppResult.Failure(AppError.Network.Unreachable)

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(
                MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                    movieId = 7L,
                    isVideoURIknown = false
                )
            )
            pump()
            awaitItem() shouldBe MovieDetailUiEventModel.LoadingTrailer
            awaitItem() shouldBe MovieDetailUiEventModel.NoConnection
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("OnPlayMovieTrailerClicked with isVideoURIknown=false -> emits Loading then ErrorFetchingTrailer on non-connectivity failure") {
        val vm = buildVM()
        coEvery { manageMovieTrailerUseCase.invoke(any(), any()) } returns
            AppResult.Failure(AppError.Storage.WriteFailed)

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(
                MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                    movieId = 9L,
                    isVideoURIknown = false
                )
            )
            pump()
            awaitItem() shouldBe MovieDetailUiEventModel.LoadingTrailer
            awaitItem() shouldBe MovieDetailUiEventModel.ErrorFetchingTrailer
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("viewState -> Success when ObserveMovieDetail emits Success(domain)") {
        val vm = buildVM()

        vm.viewState.test {
            awaitItem() shouldBe MovieDetailViewStateModel.Loading
            pump()

            val domain = MovieDetailDomainModel(
                id = 123L,
                title = "Blade Runner",
                synopsis = "Replicants...",
                localCoverFilePath = "/local/poster.jpg",
                genre = emptyList(),
                releaseDate = "1982-06-25",
                status = "Released",
                popularity = 42.0,
                cast = emptyList(),
                videoKey = "yt-abc123",
                duration = 117
            )

            detailFlow.emit(AppResult.Success(ObserveMovieSuccess.Success(domain)))
            pump()

            val s = awaitItem() as MovieDetailViewStateModel.Success
            s.movie.id shouldBe 123L
            s.movie.title shouldBe "Blade Runner"
            s.movie.videoKey shouldBe "yt-abc123"
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("OnPlayMovieTrailerClicked isVideoURIknown=false -> emits Loading then Ready(videoURI)") {
        val vm = buildVM()
        coEvery { manageMovieTrailerUseCase.invoke(any(), any()) } returns
            AppResult.Success(MovieTrailerReady("yt-video-key-999"))

        vm.oneTimeEventChannelFlow.test {
            vm.onEvent(
                MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                    movieId = 9L,
                    isVideoURIknown = false
                )
            )
            pump()

            awaitItem() shouldBe MovieDetailUiEventModel.LoadingTrailer
            awaitItem() shouldBe MovieDetailUiEventModel.OnPlayMovieTrailerReady("yt-video-key-999")
            cancelAndIgnoreRemainingEvents()
        }
    }
})
