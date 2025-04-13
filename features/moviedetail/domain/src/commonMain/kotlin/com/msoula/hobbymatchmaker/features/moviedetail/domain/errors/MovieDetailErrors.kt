package com.msoula.hobbymatchmaker.features.moviedetail.domain.errors

import com.msoula.hobbymatchmaker.core.common.AppError

sealed class MovieDetailDomainError(override val message: String) : AppError {
    data class TrailerError(val reason: String) : MovieDetailDomainError(reason)
    data class CreditError(val reason: String) : MovieDetailDomainError(reason)
    data class MovieDetailError(val reason: String) : MovieDetailDomainError(reason)
    data class Other(val reason: String) : MovieDetailDomainError(reason)
    data class NoConnection(val reason: String) : MovieDetailDomainError(reason)
}

class UpdateMovieTrailerLocalError(override val message: String) : AppError
