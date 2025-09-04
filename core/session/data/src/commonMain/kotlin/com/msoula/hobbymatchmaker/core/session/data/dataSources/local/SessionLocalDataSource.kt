package com.msoula.hobbymatchmaker.core.session.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import kotlinx.coroutines.flow.Flow

interface SessionLocalDataSource {
    suspend fun setIsConnected(isConnected: Boolean): AppResult<Unit, AppError>
    fun observeIsConnected(): Flow<Boolean>
    suspend fun setShouldShowGuestDialog(shouldShow: Boolean): AppResult<Unit, AppError>
    fun observeShouldShowGuestDialog(): Flow<Boolean>
}
