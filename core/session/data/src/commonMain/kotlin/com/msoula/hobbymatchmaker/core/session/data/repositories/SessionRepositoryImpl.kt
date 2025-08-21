package com.msoula.hobbymatchmaker.core.session.data.repositories

import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSource
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.SessionRemoteDataSource
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.mappers.toUserFireStoreModel
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
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
        sessionRemoteDataSource.createUser(user.toUserFireStoreModel())

    override suspend fun setShouldShowGuestDialog(shouldShow: Boolean) =
        sessionLocalDataSource.setShouldShowGuestDialog(shouldShow)

    override fun observeShouldShowGuestDialog(): Flow<Boolean> =
        sessionLocalDataSource.observeShouldShowGuestDialog()
}
