package com.msoula.hobbymatchmaker.tests.integration

import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.UIText
import com.msoula.hobbymatchmaker.core.database.services.MovieDAOImpl
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import com.msoula.hobbymatchmaker.tests.helpers.NetAlwaysOff
import com.msoula.hobbymatchmaker.tests.helpers.remoteFail
import com.msoula.hobbymatchmaker.tests.helpers.remoteSuccess
import com.msoula.hobbymatchmaker.tests.helpers.restartKoinFor
import com.msoula.hobbymatchmaker.tests.modules.moviesTestModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.KoinTest

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesIntegrationTest : FunSpec(), KoinTest {
    val dispatcher = UnconfinedTestDispatcher()
    val testScope = TestScope(dispatcher + Job())

    lateinit var baseModule: Module

    init {
        beforeSpec {
            baseModule = moviesTestModule(dispatcher, remoteSuccess())
            startKoin { modules(baseModule) }
            Dispatchers.setMain(dispatcher)
        }

        afterSpec {
            Dispatchers.resetMain()
            stopKoin()
        }

        test("Cold start -> fetch remote (3 pages) -> Success with 3 movies + populated DB") {
            val vm: MovieViewModel = getKoin().get(parameters = { parametersOf(testScope) })
            val dao: MovieDAOImpl = getKoin().get()

            vm.movieState.test {
                awaitItem() shouldBe MovieUiStateModel.Loading

                val success = awaitItem() as MovieUiStateModel.Success
                success.list.shouldHaveSize(3)
                success.list.first().apply {
                    id shouldBe 101L
                    title shouldBe "Alpha"
                    coverFilePath shouldBe "alpha.jpg"
                }

                dao.getMovieById(101L)!!.localCoverFilePath shouldBe "alpha.jpg"
                cancelAndIgnoreRemainingEvents()
            }
        }

        test("Remote failure -> VM Error") {
            restartKoinFor(dispatcher, remoteFail())

            val vm: MovieViewModel = getKoin().get(parameters = { parametersOf(testScope) })

            vm.movieState.test {
                awaitItem() shouldBe MovieUiStateModel.Loading
                val err = awaitItem() as MovieUiStateModel.Error
                (err.errorMessage as UIText.Plain).value shouldBe "err"
                cancelAndIgnoreRemainingEvents()
            }

            restartKoinFor(dispatcher, remoteSuccess())
        }

        test("Toggle favorite (SignedOut) -> updated DB, no error event") {
            val vm: MovieViewModel = getKoin().get(parameters = { parametersOf(testScope) })
            val dao: MovieDAOImpl = getKoin().get()

            vm.movieState.test {
                awaitItem() shouldBe MovieUiStateModel.Loading
                val success = awaitItem() as MovieUiStateModel.Success
                val movie = success.list.first()

                vm.oneTimeEventChannelFlow.test {
                    vm.onCardEvent(CardEventModel.OnDoubleTap(movie.copy(isFavorite = false)))
                    cancelAndIgnoreRemainingEvents()
                }

                dao.getMovieById(movie.id)!!.isFavorite shouldBe 1L
                cancelAndIgnoreRemainingEvents()
            }
        }

        test("Single tap offline & no local synopsis -> NoFetchingDetailPossible") {
            val netOff = module { single<NetworkConnectivityChecker> { NetAlwaysOff } }
            restartKoinFor(dispatcher, remoteSuccess(), netOff)

            val vm: MovieViewModel = getKoin().get(parameters = { parametersOf(testScope) })

            vm.movieState.test {
                awaitItem() shouldBe MovieUiStateModel.Loading
                val success = awaitItem() as MovieUiStateModel.Success
                val movieId = success.list.first().id

                vm.oneTimeEventChannelFlow.test {
                    vm.onCardEvent(
                        CardEventModel.OnSingleTap(
                            movieId = movieId,
                            movieOverview = null
                        )
                    )
                    awaitItem() shouldBe MovieUiEventModel.NoFetchingDetailPossible
                    cancelAndIgnoreRemainingEvents()
                }

                cancelAndIgnoreRemainingEvents()
            }

            restartKoinFor(dispatcher, remoteSuccess())
        }

        test("Single tap online (or local synopsis) -> OnMovieDetailClicked") {
            val vm: MovieViewModel = getKoin().get(parameters = { parametersOf(testScope) })
            vm.movieState.test {
                awaitItem() shouldBe MovieUiStateModel.Loading
                val success = awaitItem() as MovieUiStateModel.Success
                val movieId = success.list.first().id

                vm.oneTimeEventChannelFlow.test {
                    vm.onCardEvent(
                        CardEventModel.OnSingleTap(
                            movieId = movieId,
                            movieOverview = null
                        )
                    )
                    awaitItem() shouldBe MovieUiEventModel.OnMovieDetailClicked(movieId)
                    cancelAndIgnoreRemainingEvents()
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

        test("Authenticated path (override FetchFirebaseUserInfo) -> toggle favorite also call remote(no error)") {
            val authOverride = module {
                single<FetchFirebaseUserInfo> {
                    mockk<FetchFirebaseUserInfo>(relaxed = true).also { uc ->
                        coEvery { uc.invoke() } returns AppResult.Success(
                            AuthState.Authenticated(
                                FirebaseUserInfoDomainModel(
                                    uid = "u1",
                                    email = "",
                                    providers = emptyList()
                                )
                            )
                        )
                    }
                }
            }

            restartKoinFor(dispatcher, remoteSuccess(), authOverride)

            val vm: MovieViewModel = getKoin().get(parameters = { parametersOf(testScope) })
            val dao: MovieDAOImpl = getKoin().get()

            vm.movieState.test {
                awaitItem() shouldBe MovieUiStateModel.Loading
                val success = awaitItem() as MovieUiStateModel.Success
                val movie = success.list.first()

                vm.oneTimeEventChannelFlow.test {
                    vm.onCardEvent(CardEventModel.OnDoubleTap(movie.copy(isFavorite = false)))
                    expectNoEvents()
                    cancelAndIgnoreRemainingEvents()
                }

                dao.getMovieById(movie.id)!!.isFavorite shouldBe 1L
                cancelAndIgnoreRemainingEvents()
            }

            restartKoinFor(dispatcher, remoteSuccess())
        }
    }
}

