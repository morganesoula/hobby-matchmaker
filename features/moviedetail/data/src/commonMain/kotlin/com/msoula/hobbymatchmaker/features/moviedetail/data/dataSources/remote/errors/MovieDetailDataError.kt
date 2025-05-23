package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.errors

import com.msoula.hobbymatchmaker.core.common.AppError

sealed class MovieDetailDataError(override val message: String) : AppError {
    data class TrailerError(val reason: String) : MovieDetailDataError(reason)
    data class CreditError(val reason: String) : MovieDetailDataError(reason)
    data class Other(val reason: String) : MovieDetailDataError(reason)
    data class NoConnectionError(val reason: String) : MovieDetailDataError(reason)
    data class MovieDetail(val reason: String) : MovieDetailDataError(reason)
}
