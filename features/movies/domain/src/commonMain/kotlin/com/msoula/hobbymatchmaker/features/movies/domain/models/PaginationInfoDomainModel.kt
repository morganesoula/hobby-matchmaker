package com.msoula.hobbymatchmaker.features.movies.domain.models

data class PaginationInfoDomainModel(
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val hasMore: Boolean = false
) {
    companion object Companion {
        val Initial = PaginationInfoDomainModel()
    }
}
