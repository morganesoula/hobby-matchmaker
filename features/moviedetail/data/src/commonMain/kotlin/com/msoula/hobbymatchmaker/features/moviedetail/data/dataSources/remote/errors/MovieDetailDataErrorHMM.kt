package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.errors

import com.msoula.hobbymatchmaker.core.common.HMMAppError

sealed class MovieDetailDataErrorHMM(override val message: String) : HMMAppError {
    data class TrailerErrorHMM(val reason: String) : MovieDetailDataErrorHMM(reason)
    data class CreditErrorHMM(val reason: String) : MovieDetailDataErrorHMM(reason)
    data class Other(val reason: String) : MovieDetailDataErrorHMM(reason)
    data class NoConnectionErrorHMM(val reason: String) : MovieDetailDataErrorHMM(reason)
    data class MovieDetail(val reason: String) : MovieDetailDataErrorHMM(reason)
}
