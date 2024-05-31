package com.msoula.hobbymatchmaker.core.session.domain.use_cases

import com.msoula.hobbymatchmaker.core.common.AuthenticationDataStore

class FetchConnexionModeUseCase(private val dataStore: AuthenticationDataStore) {
    operator fun invoke() = dataStore.fetchConnexionMode()
}
