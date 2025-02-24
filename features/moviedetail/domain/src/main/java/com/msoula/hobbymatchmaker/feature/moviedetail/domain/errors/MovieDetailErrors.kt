package com.msoula.hobbymatchmaker.feature.moviedetail.domain.errors

import com.msoula.hobbymatchmaker.core.common.AppError

class FetchMovieDetailRemoteError(override val message: String) : AppError
class FetchMovieCreditRemoteError(override val message: String) : AppError
class FetchMovieTrailerRemoteError(override val message: String) : AppError
class UpdateMovieTrailerLocalError(override val message: String) : AppError
