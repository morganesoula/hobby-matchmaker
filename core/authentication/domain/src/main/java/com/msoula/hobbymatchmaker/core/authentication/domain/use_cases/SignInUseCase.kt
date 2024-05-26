package com.msoula.hobbymatchmaker.core.authentication.domain.use_cases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SaveAuthenticationStateUseCase

class SignInUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val saveAuthenticationStateUseCase: SaveAuthenticationStateUseCase,
) {
    suspend operator fun invoke(email: String, password: String) =
        authenticationRepository.signInWithEmailAndPassword(email, password)
            .mapSuccess {
                saveAuthenticationStateUseCase(true)
                true
            }
}
