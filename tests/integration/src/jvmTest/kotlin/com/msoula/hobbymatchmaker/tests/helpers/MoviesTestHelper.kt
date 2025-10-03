package com.msoula.hobbymatchmaker.tests.helpers

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.common.UIText
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteModel
import com.msoula.hobbymatchmaker.tests.fakes.FakeMovieRemoteDataSource
import com.msoula.hobbymatchmaker.tests.modules.moviesTestModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module

object TestErrorMapper : ErrorMessageMapper {
    override fun toUIText(error: AppError): UIText = UIText.Plain("err")
}

object NetAlwaysOn : NetworkConnectivityChecker {
    override fun hasActiveConnection() = true
}

object NetAlwaysOff : NetworkConnectivityChecker {
    override fun hasActiveConnection() = false
}

fun remoteSuccess(): MovieRemoteDataSource {
    fun movie(id: Int, title: String, poster: String, note: Double) =
        MovieRemoteModel(id, title, poster, note)

    val page1 = listOf(movie(101, "Alpha", "alpha.jpg", 7.5), movie(102, "Beta", "beta.jpg", 6.0))
    val page2 = listOf(movie(201, "Gamma", "gamma.jpg", 8.1))
    val page3 = emptyList<MovieRemoteModel>()
    return FakeMovieRemoteDataSource(pages = listOf(page1, page2, page3))
}

fun remoteFail(): MovieRemoteDataSource =
    FakeMovieRemoteDataSource(failOnFetch = AppError.Network.Http(500))

@OptIn(ExperimentalCoroutinesApi::class)
fun restartKoinFor(
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
