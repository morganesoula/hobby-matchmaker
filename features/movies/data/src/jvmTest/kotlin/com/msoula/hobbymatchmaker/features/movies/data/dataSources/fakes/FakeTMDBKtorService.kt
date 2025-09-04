package com.msoula.hobbymatchmaker.features.movies.data.dataSources.fakes

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieResponseRemoteModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBKtorErrorHMM
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBKtorService

class FakeTMDBKtorService(
    private val resultPerPage: Map<Int, Result<MovieResponseRemoteModel, TMDBKtorErrorHMM>>
) : TMDBKtorService {

    override suspend fun getMoviesByPopularityDesc(
        language: String,
        page: Int
    ): Result<MovieResponseRemoteModel, TMDBKtorErrorHMM> {
        return resultPerPage[page]
            ?: Result.Success(MovieResponseRemoteModel(results = emptyList()))
    }
}
