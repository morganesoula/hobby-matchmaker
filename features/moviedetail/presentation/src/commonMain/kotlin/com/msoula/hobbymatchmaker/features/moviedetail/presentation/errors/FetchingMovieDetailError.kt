package com.msoula.hobbymatchmaker.features.moviedetail.presentation.errors

import com.msoula.hobbymatchmaker.core.common.AppError

data class FetchingMovieDetailError(override val message: String): AppError
