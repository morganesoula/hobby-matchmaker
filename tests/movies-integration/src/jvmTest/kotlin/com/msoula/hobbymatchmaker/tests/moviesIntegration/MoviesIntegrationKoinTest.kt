package com.msoula.hobbymatchmaker.tests.moviesIntegration

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.common.UIText
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.services.MovieDAOImpl
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.MovieLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteModel
import com.msoula.hobbymatchmaker.features.movies.data.repositories.MovieRepositoryImpl
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.CheckMovieSynopsisValueUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.FetchMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest

private object TestErrorMapper : ErrorMessageMapper {
    override fun toUIText(error: AppError): UIText = UIText.Plain("err")
}

private object NetAlwaysOn : NetworkConnectivityChecker {
    override fun hasActiveConnection() = true
}

private object NetAlwaysOff : NetworkConnectivityChecker {
    override fun hasActiveConnection() = false
}

private class FakeMovieRemoteDataSource(
    private val pages: List<List<MovieRemoteModel>> = emptyList(),
    private val failOnFetch: AppError? = null
) : MovieRemoteDataSource {
    override suspend fun fetchMovies(language: String): AppResult<List<MovieRemoteModel>, AppError> {
        failOnFetch?.let { return AppResult.Failure(it) }
        return AppResult.Success(pages.flatten())
    }

    override suspend fun updateUserFavoriteMovieList(
        uuidUser: String,
        movieId: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError> = AppResult.Success(Unit)

    override suspend fun setUserFavoriteMovies(
        uid: String,
        ids: List<Long>
    ): AppResult<Unit, AppError> = AppResult.Success(Unit)
}

private fun remoteSuccess(): MovieRemoteDataSource {
    fun movie(id: Int, title: String, poster: String, note: Double) =
        MovieRemoteModel(id, title, poster, note)

    val page1 = listOf(movie(101, "Alpha", "alpha.jpg", 7.5), movie(102, "Beta", "beta.jpg", 6.0))
    val page2 = listOf(movie(201, "Gamma", "gamma.jpg", 8.1))
    val page3 = emptyList<MovieRemoteModel>()
    return FakeMovieRemoteDataSource(pages = listOf(page1, page2, page3))
}

private fun remoteFail(): MovieRemoteDataSource =
    FakeMovieRemoteDataSource(failOnFetch = AppError.Network.Http(500))

private fun newDriver(): SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

private fun newDatabase(driver: SqlDriver): HMMDatabase {
    HMMDatabase.Schema.create(driver)
    return HMMDatabase(driver)
}

private fun moviesTestModule(
    dispatcher: CoroutineDispatcher,
    remoteDS: MovieRemoteDataSource
) = module {
    single<CoroutineDispatcher>(named("moviesDispatcher")) { dispatcher }

    single<SqlDriver> { newDriver() }
    single { newDatabase(get()) }
    single { MovieDAOImpl(get()) }
    single<MovieLocalDataSource> { MovieLocalDataSourceImpl(get()) }

    single<MovieRemoteDataSource> { remoteDS }

    single<MovieRepository> { MovieRepositoryImpl(get(), get()) }
    single { FetchMoviesUseCase(get()) }
    single { SetMovieFavoriteUseCase(get()) }
    single { CheckMovieSynopsisValueUseCase(get()) }
    single { ObserveAllMoviesUseCase(get(), get(), get(named("moviesDispatcher"))) }

    single<FetchFirebaseUserInfo> {
        mockk<FetchFirebaseUserInfo>(relaxed = true).also { useCase ->
            coEvery { useCase() } returns AppResult.Success(AuthState.SignedOut)
        }
    }

    single<ErrorMessageMapper> { TestErrorMapper }
    single<NetworkConnectivityChecker> { NetAlwaysOn }
    single<FirebaseFirestore> { mockk(relaxed = true) }
    single<LogOutUseCase> { mockk(relaxed = true) }

    factory { (externalSCope: CoroutineScope) ->
        MovieViewModel(
            setMovieFavoriteUseCase = get(),
            observeAllMoviesUseCase = get(),
            fetchFirebaseUserInfo = get(),
            logOutUseCase = get(),
            checkMovieSynopsisValueUseCase = get(),
            connectivityCheck = get(),
            defaultMessageMapper = get(),
            externalScope = externalSCope
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesIntegrationKoinTest : FunSpec(), KoinTest {
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

@OptIn(ExperimentalCoroutinesApi::class)
private fun restartKoinFor(
    dispatcher: CoroutineDispatcher,
    remote: MovieRemoteDataSource,
    vararg extra: Module
) {
    stopKoin()
    val fresh = moviesTestModule(dispatcher, remote)
    startKoin {
        allowOverride(true)
        modules(listOf(fresh) + extra)
    }
    Dispatchers.setMain(dispatcher)
}

