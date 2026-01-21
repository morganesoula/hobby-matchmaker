package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models

data class PaginatedMovieResult(
    val movies: List<MovieRemoteModel>,
    val currentPage: Int,
    val totalPages: Int,
    val hasMore: Boolean
)
