package com.msoula.hobbymatchmaker.core.authentication.data.data_sources.local

import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationLocalDataSource
import com.msoula.hobbymatchmaker.core.common.AuthenticationDataStore
import kotlinx.coroutines.flow.Flow

class AuthenticationLocalDataSourceImpl(private val authenticationDataStore: AuthenticationDataStore) :
    AuthenticationLocalDataSource {

    override fun observeAuthenticationState(): Flow<Boolean> {
        return authenticationDataStore.observeAuthState()
    }
}
