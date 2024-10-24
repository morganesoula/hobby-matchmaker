package com.msoula.hobbymatchmaker.features.movies.domain.errors

import com.msoula.hobbymatchmaker.core.common.AppError

sealed class MovieErrors : AppError {
    data object FetchMovieByPageError : MovieErrors()

    override val message: String
        get() = ""
}
