package com.msoula.hobbymatchmaker.core.session.domain.data_sources

import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import kotlinx.coroutines.flow.Flow

interface SessionLocalDataSource {
    suspend fun saveUser(user: SessionUserDomainModel)
    suspend fun clearSessionData()
    suspend fun setIsConnected(isConnected: Boolean)
    fun observeIsConnected(): Flow<Boolean>
    fun getConnexionMode(): Flow<String?>
}
