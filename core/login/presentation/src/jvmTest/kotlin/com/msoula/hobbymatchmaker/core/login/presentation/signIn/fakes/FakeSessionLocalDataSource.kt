package com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes

import com.msoula.hobbymatchmaker.core.session.domain.dataSources.SessionLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeSessionLocalDataSource : SessionLocalDataSource {
    private val connectedUser = MutableStateFlow(false)

    override suspend fun setIsConnected(isConnected: Boolean) = connectedUser.update { isConnected }
    override fun observeIsConnected(): Flow<Boolean> = connectedUser
}
