package com.msoula.hobbymatchmaker.tests.modules

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.services.MovieDAOImpl
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.MovieLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.data.repositories.MovieRepositoryImpl
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.CheckMovieSynopsisValueUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.FetchMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import com.msoula.hobbymatchmaker.tests.helpers.NetAlwaysOn
import com.msoula.hobbymatchmaker.tests.helpers.TestErrorMapper
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.koin.core.qualifier.named
import org.koin.dsl.module

private fun newDriver(): SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

private fun newDatabase(driver: SqlDriver): HMMDatabase {
    HMMDatabase.Schema.create(driver)
    return HMMDatabase(driver)
}

fun moviesTestModule(
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
