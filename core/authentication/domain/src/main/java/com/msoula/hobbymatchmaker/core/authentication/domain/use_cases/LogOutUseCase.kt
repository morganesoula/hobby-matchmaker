package com.msoula.hobbymatchmaker.core.authentication.domain.use_cases

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ConnexionMode
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SaveAuthenticationStateUseCase

class LogOutUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val saveAuthenticationStateUseCase: SaveAuthenticationStateUseCase
) {
    suspend operator fun invoke(connexionMode: ConnexionMode) {
        authenticationRepository.logOut(connexionMode).mapSuccess {
            saveAuthenticationStateUseCase(false)
        }
    }
}
