package com.msoula.hobbymatchmaker.core.session.domain.data_sources

import kotlinx.coroutines.flow.Flow

interface SessionLocalDataSource {
    suspend fun setIsConnected(isConnected: Boolean)
    fun observeIsConnected(): Flow<Boolean>
}
