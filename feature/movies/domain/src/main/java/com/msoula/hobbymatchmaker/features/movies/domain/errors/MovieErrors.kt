package com.msoula.hobbymatchmaker.features.movies.domain.errors

import com.msoula.hobbymatchmaker.core.common.AppError

sealed class MovieErrors(override val message: String) : AppError {
    data object FetchMovieByPageError : MovieErrors(message = "")
    data class NetworkError(val networkErrorMessage: String) : MovieErrors(networkErrorMessage)
    data class ApiError(val apiErrorMessage: String) : MovieErrors(apiErrorMessage)
    data class UnknownError(val unknownErrorMessage: String) : MovieErrors(unknownErrorMessage)
}
