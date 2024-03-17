package com.msoula.hobbymatchmaker.features.movies.domain.data_sources

interface MovieRemoteDataSource {
    suspend fun fetchMovies()
}
