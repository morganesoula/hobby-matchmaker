package com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieVideoDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class ManageMovieTrailerUseCase(
    private val movieDetailRepository: MovieDetailRepository,
    private val updateMovieVideoURIUseCase: UpdateMovieVideoURIUseCase,
    dispatcher: CoroutineDispatcher
) :
    FlowUseCase<Parameters.LongStringParam, MovieTrailerReady, ErrorFetchingTrailer>(
        dispatcher
    ) {

    private val maxAttempt: Int = 2
    private val fallBackLanguages = listOf("en-US", "fr-FR")

    override fun execute(parameters: Parameters.LongStringParam):
        Flow<Result<MovieTrailerReady, ErrorFetchingTrailer>> {
        return channelFlow {
            val movieId = parameters.longValue

            var attempt = 0
            var result: Result<MovieTrailerReady, ErrorFetchingTrailer>

            send(Result.Loading)

            do {
                val languageToUse = fallBackLanguages.getOrNull(attempt) ?: "en-US"
                result = processVideoResponse(movieId, languageToUse)
                send(result)
                attempt++
            } while (result is Result.Failure && attempt < maxAttempt)
        }
    }

    private suspend fun processVideoResponse(
        movieId: Long,
        language: String
    ): Result<MovieTrailerReady, ErrorFetchingTrailer> {
        return when (val fetchResult = movieDetailRepository.fetchMovieTrailer(movieId, language)) {
            is Result.Success -> {
                val uri = formatVideoResponse(fetchResult.data)

                if (uri.isNotEmpty()) {
                    when (val updateResult = updateMovieVideoURIUseCase(movieId, uri)) {
                        is Result.Success -> Result.Success(
                            MovieTrailerReady(
                                uri
                            )
                        )

                        is Result.Failure -> Result.Failure(updateResult.error)
                        is Result.BusinessRuleError -> Result.BusinessRuleError(
                            ErrorFetchingTrailer(updateResult.error.message)
                        )

                        is Result.Loading -> Result.Loading
                    }
                } else {
                    Result.BusinessRuleError(ErrorFetchingTrailer(""))
                }
            }

            is Result.Failure -> Result.Failure(fetchResult.error)
            is Result.BusinessRuleError -> Result.BusinessRuleError(
                ErrorFetchingTrailer(
                    fetchResult.error.message
                )
            )

            is Result.Loading -> Result.Loading
        }
    }

    private fun formatVideoResponse(
        videoResponse: MovieVideoDomainModel?
    ): String {
        return videoResponse?.let { videoModel ->
            when (videoModel.site.lowercase()) {
                "youtube" -> videoModel.key
                else -> "https://vimeo.com/${videoModel.key}"
            }
        } ?: ""
    }
}

data class MovieTrailerReady(val videoURI: String)
data class ErrorFetchingTrailer(val errorMessage: String)
