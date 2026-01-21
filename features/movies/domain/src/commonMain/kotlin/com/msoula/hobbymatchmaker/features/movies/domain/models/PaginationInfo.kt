package com.msoula.hobbymatchmaker.features.movies.domain.models

data class PaginationInfo(
    val currentPage: Int,
    val totalPages: Int,
    val hasMore: Boolean
)
