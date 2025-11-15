package com.msoula.hobbymatchmaker.core.session.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun setIsConnected(isConnected: Boolean): AppResult<Unit, AppError>
    fun observeIsConnected(): Flow<Boolean>
    suspend fun createUser(user: SessionUserDomainModel):
        AppResult<Unit, AppError>

    suspend fun setDontAskGuestDialogValue(dontAsk: Boolean): AppResult<Unit, AppError>
    fun observeDontAskCheckboxValue(): Flow<Boolean>
    suspend fun setCurrentUserUid(uid: String): AppResult<Unit, AppError>
    fun observeCurrentUserUid(): Flow<String>
    suspend fun getCurrentUserUid(): String
    suspend fun clearCurrentUserUid(): AppResult<Unit, AppError>
}
