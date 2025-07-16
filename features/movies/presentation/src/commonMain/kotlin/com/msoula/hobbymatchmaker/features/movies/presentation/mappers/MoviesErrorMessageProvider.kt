package com.msoula.hobbymatchmaker.features.movies.presentation.mappers

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesErrors
import com.msoula.hobbymatchmaker.features.movies.presentation.Res
import com.msoula.hobbymatchmaker.features.movies.presentation.movies_api_error
import com.msoula.hobbymatchmaker.features.movies.presentation.movies_network_error
import com.msoula.hobbymatchmaker.features.movies.presentation.movies_unknown_error
import org.jetbrains.compose.resources.getString

class MoviesErrorMessageProvider : ErrorMessageProvider {
    override suspend fun getMessage(error: AppError): String {
        return when (error) {
            is ObserveAllMoviesErrors.NetworkError -> getString(Res.string.movies_network_error)
            is ObserveAllMoviesErrors.ApiError -> getString(Res.string.movies_api_error)
            is ObserveAllMoviesErrors.UnknownError -> getString(Res.string.movies_unknown_error)
            else -> error.message
        }
    }
}
