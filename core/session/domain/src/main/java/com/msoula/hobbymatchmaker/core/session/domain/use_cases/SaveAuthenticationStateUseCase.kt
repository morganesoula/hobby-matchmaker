package com.msoula.hobbymatchmaker.core.session.domain.use_cases

import com.msoula.hobbymatchmaker.core.common.AuthenticationDataStore

class SaveAuthenticationStateUseCase(private val authenticationDataStore: AuthenticationDataStore) {
    suspend operator fun invoke(isConnected: Boolean) =
        authenticationDataStore.saveAuthState(isConnected)
}
