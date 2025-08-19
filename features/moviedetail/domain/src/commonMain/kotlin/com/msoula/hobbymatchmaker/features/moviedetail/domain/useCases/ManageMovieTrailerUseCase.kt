package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository

data class MovieTrailerReady(val videoURI: String)
class ManageMovieTrailerUseCase(
    private val movieDetailRepository: MovieDetailRepository,
    private val updateMovieVideoURIUseCase: UpdateMovieVideoURIUseCase
) {
    suspend operator fun invoke(movieId: Long, language: String): R<MovieTrailerReady, AppError> {
        return when (val result = movieDetailRepository.fetchMovieTrailer(movieId, language)) {
            is R.Failure -> result
            is R.Success -> {
                val uri = formatVideoResponse(result.data)
                if (uri.isEmpty()) R.Failure(AppError.Domain.NotFound)
                else when (val saveResult = updateMovieVideoURIUseCase(movieId, uri)) {
                    is R.Success -> R.Success(MovieTrailerReady(uri))
                    is R.Failure -> saveResult
                }
            }
        }
    }

    private fun formatVideoResponse(
        videoResponse: MovieVideoDomainModel?
    ): String =
        videoResponse?.let { videoModel ->
            when (videoModel.site.lowercase()) {
                "youtube" -> videoModel.key
                else -> "https://vimeo.com/${videoModel.key}"
            }
        }.orEmpty()
}
