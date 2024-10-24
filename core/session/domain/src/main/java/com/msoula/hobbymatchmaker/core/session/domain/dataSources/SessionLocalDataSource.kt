package com.msoula.hobbymatchmaker.core.session.domain.dataSources

import kotlinx.coroutines.flow.Flow

interface SessionLocalDataSource {
    suspend fun setIsConnected(isConnected: Boolean)
    fun observeIsConnected(): Flow<Boolean>
}
