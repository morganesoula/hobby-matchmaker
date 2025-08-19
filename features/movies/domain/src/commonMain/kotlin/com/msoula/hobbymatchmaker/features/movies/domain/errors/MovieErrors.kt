package com.msoula.hobbymatchmaker.features.movies.domain.errors

import com.msoula.hobbymatchmaker.core.common.HMMAppError

sealed class MovieErrors(override val message: String) : HMMAppError {
    data class FetchMovieByPageErrorHMM(val error: String) : MovieErrors(message = error)
    data class NetworkErrorHMM(val networkErrorMessage: String) : MovieErrors(networkErrorMessage)
    data class ApiErrorHMM(val apiErrorMessage: String) : MovieErrors(apiErrorMessage)
    data class UnknownErrorHMM(val unknownErrorMessage: String) : MovieErrors(unknownErrorMessage)
}
