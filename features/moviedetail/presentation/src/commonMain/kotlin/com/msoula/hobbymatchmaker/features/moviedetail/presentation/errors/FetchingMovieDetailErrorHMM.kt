package com.msoula.hobbymatchmaker.features.moviedetail.presentation.errors

import com.msoula.hobbymatchmaker.core.common.HMMAppError

data class FetchingMovieDetailErrorHMM(override val message: String): HMMAppError
