package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models

data class PaginatedMovieResult(
    val movies: List<MovieRemoteDataModel> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val hasMore: Boolean = false
) {
    companion object {
        val Initial = PaginatedMovieResult()
    }
}
