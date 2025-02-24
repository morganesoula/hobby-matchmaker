package com.msoula.hobbymatchmaker.core.session.domain.repositories

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.errors.SessionErrors
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun setIsConnected(isConnected: Boolean)
    fun observeIsConnected(): Flow<Boolean>
    suspend fun createUser(user: SessionUserDomainModel):
        Result<Boolean, SessionErrors.CreateUserError>
}
