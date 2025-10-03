package com.msoula.hobbymatchmaker.tests.integration

import app.cash.sqldelight.db.SqlDriver
import app.cash.turbine.test
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.common.di.coreModuleCommon
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.di.coreModuleDAO
import com.msoula.hobbymatchmaker.core.database.services.MovieDAO
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.di.featuresModuleMovieDetailData
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.di.featuresModuleMovieDetailDomain
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.tests.fakes.FakeMovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.tests.modules.coreDbJvmTestModule
import com.msoula.hobbymatchmaker.tests.modules.movieDetailTestOverrides
import com.msoula.hobbymatchmaker.tests.modules.movieDetailTestPresentation
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailIntegrationTest : FunSpec(), KoinTest {
    private val testDispatcher = StandardTestDispatcher()

    val testCoroutinesModule = module {
        single<CoroutineDispatcher> { testDispatcher }

        single<CoroutineDispatcher>(qualifier = named("io")) { testDispatcher }
        single<CoroutineDispatcher>(qualifier = named("default")) { testDispatcher }
        single<CoroutineDispatcher>(qualifier = named("main")) { testDispatcher }

        single<CoroutineDispatcher>(qualifier = named("IO")) { testDispatcher }
        single<CoroutineDispatcher>(qualifier = named("Default")) { testDispatcher }
        single<CoroutineDispatcher>(qualifier = named("Main")) { testDispatcher }
        single<CoroutineDispatcher>(qualifier = named("database")) { testDispatcher }
        single<CoroutineDispatcher>(qualifier = named("db")) { testDispatcher }

        single { CoroutineScope(testDispatcher) }
    }

    override suspend fun beforeSpec(spec: Spec) {
        Dispatchers.setMain(testDispatcher)
        startKoin {
            logger(PrintLogger(Level.DEBUG))
            modules(
                coreModuleCommon,
                coreModuleDAO,
                featuresModuleMovieDetailData,
                featuresModuleMovieDetailDomain,
                movieDetailTestOverrides,
                coreDbJvmTestModule,
                testCoroutinesModule,
                movieDetailTestPresentation
            )
        }

        val k = org.koin.java.KoinJavaComponent.getKoin()

        check(k.getOrNull<ErrorMessageMapper>() != null) { "Missing ErrorMessageMapper" }
        check(k.getOrNull<SqlDriver>() != null) { "Missing SqlDriver" }
        check(k.getOrNull<HMMDatabase>() != null) { "Missing HMMDatabase (JDBC)" }
        check(k.getOrNull<MovieDAO>() != null) { "Missing MovieDAO" }
        check(k.getOrNull<MovieDetailRemoteDataSource>() != null) { "Missing Remote Fake" }
        check(k.getOrNull<NetworkConnectivityChecker>() != null) { "Missing NetworkConnectivityChecker" }
    }

    override suspend fun afterSpec(spec: Spec) {
        stopKoin()
        Dispatchers.resetMain()
    }

    private fun vm(movieId: Long) = MovieDetailViewModel(
        movieId,
        get(),
        get(),
        get<NetworkConnectivityChecker>(),
        get<ErrorMessageMapper>()
    )

    private val remote by inject<MovieDetailRemoteDataSource>()
    private val connectivity by inject<NetworkConnectivityChecker>()

    init {
        test("Cold start → trailer ready (locale-dependent)") {
            runTest {
                val movieId = 603692L
                val viewModel = vm(movieId)

                viewModel.oneTimeEventChannelFlow.test {
                    backgroundScope.launch {
                        viewModel.onEvent(
                            MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                                movieId,
                                false
                            )
                        )
                    }

                    awaitItem() shouldBe MovieDetailUiEventModel.LoadingTrailer
                    when (val event = awaitItem()) {
                        is MovieDetailUiEventModel.OnPlayMovieTrailerReady -> {
                            val uri = event.movieUri
                            if (!(uri.contains("abc123") || uri.contains("enKey"))) {
                                error("Expected YouTube key 'abc123' (FR) or 'enKey' (EN), got: $uri")
                            }
                        }
                        else -> error("Expected OnPlayMovieTrailerReady, got $event")
                    }

                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        test("Fallback EN when FR is empty") {
            runTest {
                val movieId = 603692L
                val viewModel = vm(movieId)

                (remote as FakeMovieDetailRemoteDataSource).clearVideos("fr")

                val events = viewModel.oneTimeEventChannelFlow
                viewModel.onEvent(MovieDetailUiEventModel.OnPlayMovieTrailerClicked(movieId, false))

                events.test {
                    awaitItem() shouldBe MovieDetailUiEventModel.LoadingTrailer
                    val ready = awaitItem() as MovieDetailUiEventModel.OnPlayMovieTrailerReady
                    ready.movieUri.shouldContain("enKey")
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        test("No connection + isVideoURIknown = true → NoConnection") {
            runTest {
                val movieId = 603692L
                val viewModel = vm(movieId)

                connectivity.javaClass.getMethod("setOnline", Boolean::class.java)
                    .invoke(connectivity, false)

                val events = viewModel.oneTimeEventChannelFlow
                viewModel.onEvent(MovieDetailUiEventModel.OnPlayMovieTrailerClicked(movieId, true))

                events.test {
                    awaitItem() shouldBe MovieDetailUiEventModel.NoConnection
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }
}
