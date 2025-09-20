package com.msoula.hobbymatchmaker.tests.modules

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import com.msoula.hobbymatchmaker.tests.fakes.FakeMovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.tests.helpers.MovieDetailTestFixtures
import org.koin.dsl.module

val movieDetailTestOverrides = module {
    single<SqlDriver> {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { driver ->
            HMMDatabase.Schema.create(driver)
        }
    }

    single<NetworkConnectivityChecker> {
        object : NetworkConnectivityChecker {
            @Volatile private var online = true
            override fun hasActiveConnection(): Boolean = online
            fun setOnline(v: Boolean) { online = v }
        }
    }

    single<MovieDetailRemoteDataSource> {
        FakeMovieDetailRemoteDataSource(
            detailFixture = MovieDetailTestFixtures.detail(),
            castFixture = MovieDetailTestFixtures.cast(1 to "Actor One", 2 to "Actor Two"),
            videosByLang = mutableMapOf(
                "fr" to MovieDetailTestFixtures.videosFR("abc123"),
                "en" to MovieDetailTestFixtures.videosEN("enKey")
            )
        )
    }
}

val movieDetailTestPresentation = module {
    factory<MovieDetailViewModel> { (movieId: Long) ->
        MovieDetailViewModel(
            movieId = movieId,
            observeMovieDetailUseCase = get(),
            manageMovieTrailerUseCase = get(),
            connectivityCheck = get<NetworkConnectivityChecker>(),
            defaultErrorMessageMapper = get<ErrorMessageMapper>()
        )
    }
}

val coreDbJvmTestModule = module {
    single<HMMDatabase> { HMMDatabase(get<SqlDriver>()) }
}
