package com.msoula.hobbymatchmaker.features.moviedetail.mappers

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieErrors
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.Res
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.connection_issue
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.credit_error
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.no_data
import org.jetbrains.compose.resources.getString

class MovieDetailErrorMessageProvider : ErrorMessageProvider {
    override suspend fun getMessage(error: AppError): String {
        return when (error) {
            is ObserveMovieErrors.Empty -> getString(Res.string.no_data)
            is ObserveMovieErrors.CreditError -> getString(Res.string.credit_error)
            is ObserveMovieErrors.NoConnection -> getString(Res.string.connection_issue)
            else -> error.message
        }
    }
}
