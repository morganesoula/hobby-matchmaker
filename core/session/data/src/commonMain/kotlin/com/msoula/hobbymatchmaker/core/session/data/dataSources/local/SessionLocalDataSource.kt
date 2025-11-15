package com.msoula.hobbymatchmaker.core.session.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import kotlinx.coroutines.flow.Flow

interface SessionLocalDataSource {
    suspend fun setIsConnected(isConnected: Boolean): AppResult<Unit, AppError>
    fun observeIsConnected(): Flow<Boolean>
    suspend fun setDontAskGuestValue(dontAsk: Boolean): AppResult<Unit, AppError>
    fun observeDontAskCheckboxValue(): Flow<Boolean>
    fun observeCurrentUid(): Flow<String>
    suspend fun setCurrentUid(currentUid: String): AppResult<Unit, AppError>
    suspend fun getCurrentUserUid(): String
    suspend fun clearCurrentUserUid(): AppResult<Unit, AppError>
}
