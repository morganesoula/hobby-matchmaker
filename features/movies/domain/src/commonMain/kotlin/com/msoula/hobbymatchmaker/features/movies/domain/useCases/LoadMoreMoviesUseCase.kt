package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.domain.models.PaginationInfo
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class LoadMoreMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(
        language: String,
        page: Int
    ): AppResult<PaginationInfo, AppError> =
        movieRepository.loadMoreMovies(language, page)
}
