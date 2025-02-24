package com.msoula.hobbymatchmaker.feature.moviedetail.presentation.errors

import com.msoula.hobbymatchmaker.core.common.AppError

data class FetchingMovieDetailError(override val message: String): AppError
