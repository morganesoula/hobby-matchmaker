package com.msoula.hobbymatchmaker.core.session.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import kotlinx.coroutines.flow.Flow

interface SessionLocalDataSource {
    suspend fun setIsConnected(isConnected: Boolean): R<Unit, AppError>
    fun observeIsConnected(): Flow<Boolean>
    suspend fun setShouldShowGuestDialog(shouldShow: Boolean): R<Unit, AppError>
    fun observeShouldShowGuestDialog(): Flow<Boolean>
}
