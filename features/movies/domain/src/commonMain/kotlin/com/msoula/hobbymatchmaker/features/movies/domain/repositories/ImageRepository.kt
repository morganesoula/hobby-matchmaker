package com.msoula.hobbymatchmaker.features.movies.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult

interface ImageRepository {
    suspend fun getRemoteImage(remotePosterPath: String): String?
    suspend fun downloadImage(remotePosterPath: String): String?
    suspend fun saveRemoteImageAndUpdateMovie(
        coverFileName: String,
        updateMovie: suspend (localImagePath: String) -> Unit
    ): AppResult<Unit, AppError>
}
