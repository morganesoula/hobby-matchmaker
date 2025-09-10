package com.msoula.hobbymatchmaker.features.movies.data.dataSources.fakes

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieResponseRemoteModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBKtorService

class FakeTMDBKtorService(
    private val resultPerPage: Map<Int, AppResult<MovieResponseRemoteModel, AppError>>
) : TMDBKtorService {

    override suspend fun getMoviesByPopularityDesc(
        language: String,
        page: Int
    ) = resultPerPage[page]
        ?: AppResult.Failure(
            AppError.Network.Unknown(
                RuntimeException(
                    "no stub for page=$page"
                )
            )
        )
}
