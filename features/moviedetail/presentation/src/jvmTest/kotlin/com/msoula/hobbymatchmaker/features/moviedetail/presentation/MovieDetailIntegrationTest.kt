package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailIntegrationTest : FunSpec({
    /* val dispatcher = UnconfinedTestDispatcher()

    val movieDetailRemoteDataSource = FakeRemoteDataSource()
    val movieDetailLocalDataSource = FakeLocalDataSource()

    val movieDetailRepository =
        MovieDetailRepositoryImpl(movieDetailRemoteDataSource, movieDetailLocalDataSource)

    val observeMovieDetailUseCase =
        ObserveMovieDetailUseCase(movieDetailRepository, dispatcher)
    val updateMovieVideoURIUseCase =
        UpdateMovieVideoURIUseCase(movieDetailRepository)
    val manageMovieTrailerUseCase =
        ManageMovieTrailerUseCase(
            movieDetailRepository, updateMovieVideoURIUseCase, dispatcher
        )
    val connectivityChecker = FakeConnectivityChecker()
    val errorMessageProvider = MovieDetailErrorMessageProvider()

    test("should emit Success.Empty on launch when movie is not in DB") {
        val movieDetailVM = MovieDetailViewModel(
            42L,
            dispatcher,
            observeMovieDetailUseCase,
            manageMovieTrailerUseCase,
            connectivityChecker,
            errorMessageProvider
        )

        runTest {
            movieDetailVM.viewState.test {
                awaitItem() shouldBe MovieDetailViewStateModel.Loading
                awaitItem() shouldBe MovieDetailViewStateModel.Empty
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("should emit Success on launch when movie is in DB") {
        val fakeMovie = MovieDetailDomainModel(
            id = 42L,
            title = "fake title test",
            genre = emptyList(),
            popularity = 4.5,
            releaseDate = "2025-07-22",
            synopsis = "fake synopsis test",
            status = "Released",
            localCoverFilePath = "/fake_local_path",
            videoKey = "/testVideo",
            cast = listOf(MovieActorDomainModel(name = "NO_CAST", role = "MARKER"))
        )

        val movieDetailVM = MovieDetailViewModel(
            42L,
            dispatcher,
            observeMovieDetailUseCase,
            manageMovieTrailerUseCase,
            connectivityChecker,
            errorMessageProvider
        )

        runTest {
            movieDetailLocalDataSource.saveMovieDetail(fakeMovie)
            advanceUntilIdle()

            movieDetailVM.viewState.test {
                awaitItem() shouldBe MovieDetailViewStateModel.Loading
                awaitItem() shouldBe MovieDetailViewStateModel.Success(
                    fakeMovie.toMovieDetailUiModel()
                )

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    test("should emit onPlayMovieTrailerReady when video URI is known and connected") {
        runTest {
            movieDetailLocalDataSource.saveMovieDetail(
                MovieDetailDomainModel(
                    id = 42L,
                    videoKey = "/testKey",
                    synopsis = "valid synopsis"
                )
            )
            advanceUntilIdle()

            val movieDetailVM = MovieDetailViewModel(
                42L,
                dispatcher,
                observeMovieDetailUseCase,
                manageMovieTrailerUseCase,
                connectivityChecker,
                errorMessageProvider
            )

            movieDetailVM.viewState.first {
                it is MovieDetailViewStateModel.Success && it.movie.videoKey == "/testKey"
            }

            movieDetailVM.oneTimeEventChannelFlow.test {
                movieDetailVM.onPlayTrailerClicked(42L, true)
                awaitItem() shouldBe MovieDetailUiEventModel.OnPlayMovieTrailerReady("/testKey")
            }
        }
    }

    test("should emit Failure when not connected") {
        movieDetailLocalDataSource.saveMovieDetail(MovieDetailDomainModel())

        val fakeConnectivityChecker = FakeConnectivityChecker(false)
        val remoteDataSource =
            FakeRemoteDataSource(
                fetchMovieDetailResult =
                    Result.Failure(MovieDetailDomainErrorHMM.NoConnection(""))
            )

        val repository =
            MovieDetailRepositoryImpl(remoteDataSource, movieDetailLocalDataSource)

        val observeUseCase = ObserveMovieDetailUseCase(repository, dispatcher)
        val updateUseCase = UpdateMovieVideoURIUseCase(repository)
        val manageUseCase = ManageMovieTrailerUseCase(repository, updateUseCase, dispatcher)

        val movieDetailVM = MovieDetailViewModel(
            42L,
            dispatcher,
            observeUseCase,
            manageUseCase,
            fakeConnectivityChecker,
            errorMessageProvider
        )

        runTest {
            movieDetailVM.viewState.test {
                awaitItem() shouldBe MovieDetailViewStateModel.Loading
                awaitItem() shouldBe MovieDetailViewStateModel.Error(
                    "Problème de connexion. Veuillez vérifier votre réseau"
                )
            }
        }
    }

    test("should fetch from remote and save if synopsis is blank") {
        movieDetailLocalDataSource.saveMovieDetail(
            MovieDetailDomainModel(id = 42L, synopsis = "")
        )

        val remoteDataSource = FakeRemoteDataSource(
            fetchMovieDetailResult = Result.Success(
                MovieDetailDomainModel(
                    id = 42L,
                    synopsis = "Fetched from remote"
                )
            )
        )

        val repository = MovieDetailRepositoryImpl(remoteDataSource, movieDetailLocalDataSource)
        val observeUseCase = ObserveMovieDetailUseCase(repository, dispatcher)
        val movieDetailVM = MovieDetailViewModel(
            42L,
            dispatcher,
            observeUseCase,
            manageMovieTrailerUseCase,
            connectivityChecker,
            errorMessageProvider
        )

        runTest {
            movieDetailVM.viewState.test {
                awaitItem() shouldBe MovieDetailViewStateModel.Loading
                awaitItem() shouldBe MovieDetailViewStateModel.Success(
                    MovieDetailUiModel(
                        synopsis = "Fetched from remote",
                        id = 42L,
                        cast = mapOf("NO_CAST" to "MARKER")
                    )
                )
            }
        }
    }

    test("should fetch trailer when not known and emit OnPlayMovieTrailerReady") {
        runTest {
            movieDetailLocalDataSource.saveMovieDetail(
                MovieDetailDomainModel(id = 42L, synopsis = "some synopsis")
            )

            val movieDetailVM = MovieDetailViewModel(
                42L,
                dispatcher,
                observeMovieDetailUseCase,
                manageMovieTrailerUseCase,
                connectivityChecker,
                errorMessageProvider
            )

            movieDetailVM.viewState.first {
                it is MovieDetailViewStateModel.Success && it.movie.videoKey.isBlank()
            }

            movieDetailVM.oneTimeEventChannelFlow.test {
                movieDetailVM.onPlayTrailerClicked(42L, false)

                awaitItem() shouldBe MovieDetailUiEventModel.LoadingTrailer
                awaitItem() shouldBe MovieDetailUiEventModel.OnPlayMovieTrailerReady("/testKey")
            }
        }
    }

    test("should emit OnPlayMovieTrailerReady with Vimeo when video site is not YouTube") {
        val remoteDataSource = FakeRemoteDataSource(
            fetchMovieTrailerResult = Result.Success(
                MovieVideoDomainModel(
                    key = "/nonYouTubeKey",
                    type = "video",
                    site = "Vimeo"
                )
            )
        )

        val repository = MovieDetailRepositoryImpl(remoteDataSource, movieDetailLocalDataSource)
        val observeUseCase = ObserveMovieDetailUseCase(repository, dispatcher)
        val updateUseCase = UpdateMovieVideoURIUseCase(repository)
        val manageUseCase = ManageMovieTrailerUseCase(repository, updateUseCase, dispatcher)

        val movieDetailVM = MovieDetailViewModel(
            movieId = 42L,
            ioDispatcher = dispatcher,
            observeMovieDetailUseCase = observeUseCase,
            manageMovieTrailerUseCase = manageUseCase,
            connectivityCheck = connectivityChecker,
            errorMessageProvider = errorMessageProvider
        )

        runTest {
            movieDetailLocalDataSource.saveMovieDetail(
                MovieDetailDomainModel(id = 42L, synopsis = "random synopsis")
            )

            movieDetailVM.oneTimeEventChannelFlow.test {
                movieDetailVM.onPlayTrailerClicked(42L, isVideoURIknown = false)

                awaitItem() shouldBe MovieDetailUiEventModel.LoadingTrailer
                awaitItem() shouldBe MovieDetailUiEventModel.OnPlayMovieTrailerReady(
                    "https://vimeo.com//nonYouTubeKey"
                )
            }
        }
    } */
})
