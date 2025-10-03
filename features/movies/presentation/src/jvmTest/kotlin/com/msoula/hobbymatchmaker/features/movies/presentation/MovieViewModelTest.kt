package com.msoula.hobbymatchmaker.features.movies.presentation

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutSuccess
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.common.UIText
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.CheckMovieSynopsisValueUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
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
class MovieViewModelTest : FunSpec({
    val scheduler = TestCoroutineScheduler()
    val dispatcher = StandardTestDispatcher(scheduler)
    val testScope = TestScope(dispatcher + Job())

    lateinit var observeAllMovies: ObserveAllMoviesUseCase
    lateinit var setFavorite: SetMovieFavoriteUseCase
    lateinit var fetchUser: FetchFirebaseUserInfo
    lateinit var logOut: LogOutUseCase
    lateinit var checkSynopsis: CheckMovieSynopsisValueUseCase
    lateinit var net: NetworkConnectivityChecker
    val mapper: ErrorMessageMapper = object : ErrorMessageMapper {
        override fun toUIText(error: AppError): UIText = UIText.Plain("err")
    }

    lateinit var movieFlow: MutableSharedFlow<AppResult<ObserveAllMoviesSuccess, AppError>>

    fun buildVM(): MovieViewModel {
        movieFlow = MutableSharedFlow(replay = 1)

        observeAllMovies = mockk { every { this@mockk(any()) } returns movieFlow }
        setFavorite = mockk(relaxed = true)
        fetchUser = mockk(relaxed = true)
        logOut = mockk(relaxed = true)
        checkSynopsis = mockk(relaxed = true)
        net = mockk(relaxed = true)

        return MovieViewModel(
            setMovieFavoriteUseCase = setFavorite,
            observeAllMoviesUseCase = observeAllMovies,
            fetchFirebaseUserInfo = fetchUser,
            logOutUseCase = logOut,
            checkMovieSynopsisValueUseCase = checkSynopsis,
            connectivityCheck = net,
            defaultMessageMapper = mapper,
            externalScope = testScope
        )
    }

    suspend fun pump() {
        scheduler.runCurrent()
    }

    beforeSpec { Dispatchers.setMain(dispatcher) }
    afterSpec {
        Dispatchers.resetMain()
        unmockkAll()
    }

    beforeTest { clearAllMocks() }

    test("initial state is Loading") {
        val vm = buildVM()
        vm.movieState.value shouldBe MovieUiStateModel.Loading
    }

    test("movieState -> Success when repository emits movies") {
        val vm = buildVM()
        val movies = listOf(MovieDomainModel(id = 1L, title = "A"))

        vm.movieState.test {
            awaitItem() shouldBe MovieUiStateModel.Loading
            pump()

            movieFlow.emit(AppResult.Success(ObserveAllMoviesSuccess.Success(movies)))
            pump()

            val state = awaitItem() as MovieUiStateModel.Success
            state.list.first().id shouldBe 1L
            state.list.first().title shouldBe "A"
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("movieState stays Loading when DataLoadedInDB") {
        val vm = buildVM()

        vm.movieState.test {
            awaitItem() shouldBe MovieUiStateModel.Loading

            movieFlow.emit(AppResult.Success(ObserveAllMoviesSuccess.DataLoadedInDB))
            pump()

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("movieState -> Error on failure") {
        val vm = buildVM()

        vm.movieState.test {
            awaitItem() shouldBe MovieUiStateModel.Loading
            pump()

            movieFlow.emit(AppResult.Failure(AppError.Domain.NotFound))
            pump()

            val err = awaitItem() as MovieUiStateModel.Error
            (err.errorMessage as UIText.Plain).value shouldBe "err"
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("logOut success emits OnLogOutSuccess") {
        val vm = buildVM()
        coEvery { logOut.invoke() } returns AppResult.Success(LogOutSuccess)

        vm.oneTimeEventChannelFlow.test {
            vm.logOut()
            pump()
            awaitItem() shouldBe MovieUiEventModel.OnLogOutSuccess
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("logOut failure emits OnLogOutFailure with mapped error") {
        val vm = buildVM()
        coEvery { logOut.invoke() } returns AppResult.Failure(AppError.Domain.Forbidden)

        vm.oneTimeEventChannelFlow.test {
            vm.logOut()
            pump()
            val ev = awaitItem() as MovieUiEventModel.OnLogOutFailure
            (ev.error as UIText.Plain).value shouldBe "err"
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("OnSingleTap -> detail when local synopsis available") {
        val vm = buildVM()
        coEvery { checkSynopsis.invoke(42L) } returns AppResult.Success(true)

        val event = vm.handleSingleTap(movieId = 42L)
        event shouldBe MovieUiEventModel.OnMovieDetailClicked(42L)
    }

    test("OnSingleTap -> detail when no local but network ON") {
        val vm = buildVM()
        coEvery { checkSynopsis.invoke(10L) } returns AppResult.Success(false)
        every { net.hasActiveConnection() } returns true

        val event = vm.handleSingleTap(10L)
        event shouldBe MovieUiEventModel.OnMovieDetailClicked(10L)
    }

    test("OnSingleTap -> NoFetchingDetailPossible when no local and network OFF") {
        val vm = buildVM()
        coEvery { checkSynopsis.invoke(10L) } returns AppResult.Success(false)
        every { net.hasActiveConnection() } returns false

        val event = vm.handleSingleTap(10L)
        event shouldBe MovieUiEventModel.NoFetchingDetailPossible
    }

    test("OnDoubleTap authenticated -> favorite ok (no error event) and correct args") {
        val vm = buildVM()
        coEvery { fetchUser.invoke() } returns AppResult.Success(
            AuthState.Authenticated(
                FirebaseUserInfoDomainModel(
                    uid = "u1",
                    email = "",
                    providers = emptyList()
                )
            )
        )
        coEvery { setFavorite.invoke(any(), any(), any()) } returns AppResult.Success(Unit)

        vm.oneTimeEventChannelFlow.test {
            vm.onCardEvent(
                CardEventModel.OnDoubleTap(
                    MovieUiModel(
                        id = 1L,
                        coverFilePath = "",
                        isFavorite = false,
                        title = "t",
                        overview = null,
                        note = 0.0
                    )
                )
            )
            scheduler.advanceUntilIdle()
            expectNoEvents()
            coVerify(exactly = 1) { setFavorite.invoke("u1", 1L, true) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("OnDoubleTap authenticated -> remote failure shows error") {
        val vm = buildVM()
        coEvery { fetchUser.invoke() } returns AppResult.Success(
            AuthState.Authenticated(
                FirebaseUserInfoDomainModel(
                    uid = "u1",
                    email = "",
                    providers = emptyList()
                )
            )
        )
        coEvery {
            setFavorite.invoke(
                any(),
                any(),
                any()
            )
        } returns AppResult.Failure(AppError.Storage.WriteFailed)

        vm.oneTimeEventChannelFlow.test {
            vm.onCardEvent(
                CardEventModel.OnDoubleTap(
                    MovieUiModel(
                        id = 2L,
                        coverFilePath = "",
                        isFavorite = false,
                        title = "t",
                        overview = null,
                        note = 0.0
                    )
                )
            )
            pump()

            val ev = awaitItem() as MovieUiEventModel.ShowError
            (ev.error as UIText.Plain).value shouldBe "err"
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("OnDoubleTap signed out -> only local update (uid empty), no error") {
        val vm = buildVM()
        coEvery { fetchUser.invoke() } returns AppResult.Success(AuthState.SignedOut)
        coEvery { setFavorite.invoke(any(), any(), any()) } returns AppResult.Success(Unit)

        vm.oneTimeEventChannelFlow.test {
            vm.onCardEvent(
                CardEventModel.OnDoubleTap(
                    MovieUiModel(
                        id = 3L,
                        coverFilePath = "",
                        isFavorite = false,
                        title = "t",
                        overview = null,
                        note = 0.0
                    )
                )
            )
            pump()

            coVerify(exactly = 1) { setFavorite.invoke("", 3L, true) }
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    test("OnDoubleTap -> fetch user failure shows error") {
        val vm = buildVM()
        coEvery { fetchUser.invoke() } returns AppResult.Failure(AppError.Network.Unreachable)

        vm.oneTimeEventChannelFlow.test {
            vm.onCardEvent(
                CardEventModel.OnDoubleTap(
                    MovieUiModel(
                        id = 1L,
                        coverFilePath = "",
                        isFavorite = false,
                        title = "t",
                        overview = null,
                        note = 0.0
                    )
                )
            )

            pump()
            val ev = awaitItem() as MovieUiEventModel.ShowError
            (ev.error as UIText.Plain).value shouldBe "err"
            cancelAndIgnoreRemainingEvents()
        }
    }
})
