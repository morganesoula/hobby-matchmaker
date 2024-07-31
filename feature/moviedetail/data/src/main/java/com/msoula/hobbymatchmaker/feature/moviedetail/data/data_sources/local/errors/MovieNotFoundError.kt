package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.local.errors

import com.msoula.hobbymatchmaker.core.common.AppError

class MovieNotFoundError(override val message: String) : AppError
