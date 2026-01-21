package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult

interface MovieSyncPreferences {
    suspend fun getLastSyncTimestamp(): Long
    suspend fun setLastSyncTimestamp(timestamp: Long): AppResult<Unit, AppError>
    suspend fun getLastLoadedPage(): Int
    suspend fun setLastLoadedPage(page: Int): AppResult<Unit, AppError>
}
