package com.msoula.hobbymatchmaker.core.authentication.domain.data_sources

import kotlinx.coroutines.flow.Flow

interface AuthenticationLocalDataSource {
    fun observeAuthenticationState(): Flow<Boolean>
}
