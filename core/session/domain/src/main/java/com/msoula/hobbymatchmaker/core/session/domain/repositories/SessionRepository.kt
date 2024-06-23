package com.msoula.hobbymatchmaker.core.session.domain.repositories

import com.msoula.hobbymatchmaker.core.session.domain.data_sources.SessionLocalDataSource
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import kotlinx.coroutines.flow.Flow

class SessionRepository(private val sessionLocalDataSource: SessionLocalDataSource) {
    suspend fun saveUser(user: SessionUserDomainModel) =
        sessionLocalDataSource.saveUser(user)

    suspend fun clearSessionData() =
        sessionLocalDataSource.clearSessionData()

    suspend fun setIsConnected(isConnected: Boolean) =
        sessionLocalDataSource.setIsConnected(isConnected)

    fun observeIsConnected(): Flow<Boolean> =
        sessionLocalDataSource.observeIsConnected()

    fun getConnexionMode(): Flow<String?> =
        sessionLocalDataSource.getConnexionMode()
}
