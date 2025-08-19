package com.msoula.hobbymatchmaker.features.moviedetail.domain.errors

import com.msoula.hobbymatchmaker.core.common.HMMAppError

sealed class MovieDetailDomainErrorHMM(override val message: String) : HMMAppError {
    data class TrailerErrorHMM(val reason: String) : MovieDetailDomainErrorHMM(reason)
    data class CreditErrorHMM(val reason: String) : MovieDetailDomainErrorHMM(reason)
    data class MovieDetailErrorHMM(val reason: String) : MovieDetailDomainErrorHMM(reason)
    data class Other(val reason: String) : MovieDetailDomainErrorHMM(reason)
    data class NoConnection(val reason: String) : MovieDetailDomainErrorHMM(reason)
    data class ExternalServiceErrorHMM(val reason: String) : MovieDetailDomainErrorHMM(reason)
    data class EmptyDataErrorHMM(val reason: String) : MovieDetailDomainErrorHMM(reason)
}

class UpdateMovieTrailerLocalErrorHMM(override val message: String) : HMMAppError
