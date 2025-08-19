package com.msoula.hobbymatchmaker.features.movies.presentation.errors

import com.msoula.hobbymatchmaker.core.common.HMMAppError

data class FetchingMovieErrorHMM(override val message: String) : HMMAppError
