package com.msoula.hobbymatchmaker.core.session.domain.repositories

import com.msoula.hobbymatchmaker.core.session.domain.dataSources.SessionLocalDataSource
import com.msoula.hobbymatchmaker.core.session.domain.dataSources.SessionRemoteDataSource
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import kotlinx.coroutines.flow.Flow

class SessionRepositoryImpl(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val sessionRemoteDataSource: SessionRemoteDataSource
) : SessionRepository {
    override suspend fun setIsConnected(isConnected: Boolean) =
        sessionLocalDataSource.setIsConnected(isConnected)

    override fun observeIsConnected(): Flow<Boolean> =
        sessionLocalDataSource.observeIsConnected()

    override suspend fun createUser(user: SessionUserDomainModel) =
        sessionRemoteDataSource.createUser(user)
}
