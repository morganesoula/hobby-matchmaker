package feature.movies.errors

import com.msoula.hobbymatchmaker.core.common.AppError

class LanguageNotSupportedError(override val message: String): AppError
