package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.FetchMovieTrailerRemoteError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository

class FetchMovieDetailTrailerUseCase(
    private val movieDetailRepository: MovieDetailRepository
) {
    suspend operator fun invoke(movieId: Long, language: String):
        Result<MovieVideoDomainModel?, FetchMovieTrailerRemoteError> =
        movieDetailRepository.fetchMovieTrailer(movieId, language)
}
