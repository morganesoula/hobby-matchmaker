package com.msoula.hobbymatchmaker.core.session.domain.use_cases

import com.msoula.hobbymatchmaker.core.common.AuthenticationDataStore

class SaveConnexionModeUseCase(private val dataStore: AuthenticationDataStore) {
    suspend operator fun invoke(connexionMode: String) = dataStore.saveConnexionMode(connexionMode)
}
