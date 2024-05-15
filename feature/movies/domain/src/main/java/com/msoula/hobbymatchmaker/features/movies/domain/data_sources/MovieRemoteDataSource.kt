package com.msoula.hobbymatchmaker.features.movies.domain.data_sources

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel

interface MovieRemoteDataSource {
    suspend fun fetchMovies(): Result<List<MovieDomainModel>>
}
