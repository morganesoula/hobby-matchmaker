package com.msoula.hobbymatchmaker.core.session.domain.repositories

import com.msoula.hobbymatchmaker.core.session.domain.data_sources.SessionLocalDataSource
import com.msoula.hobbymatchmaker.core.session.domain.data_sources.SessionRemoteDataSource
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import kotlinx.coroutines.flow.Flow

class SessionRepository(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val sessionRemoteDataSource: SessionRemoteDataSource
) {
    suspend fun setIsConnected(isConnected: Boolean) =
        sessionLocalDataSource.setIsConnected(isConnected)

    fun observeIsConnected(): Flow<Boolean> =
        sessionLocalDataSource.observeIsConnected()

    suspend fun createUser(user: SessionUserDomainModel) =
        sessionRemoteDataSource.createUser(user)
}
