package com.msoula.hobbymatchmaker.features.movies.presentation.errors

import com.msoula.hobbymatchmaker.core.common.AppError

data class FetchingMovieError(override val message: String) : AppError
