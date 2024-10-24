package com.msoula.hobbymatchmaker.features.movies.domain.useCases.errors

import com.msoula.hobbymatchmaker.core.common.AppError

class LanguageNotSupportedError(override val message: String): AppError
