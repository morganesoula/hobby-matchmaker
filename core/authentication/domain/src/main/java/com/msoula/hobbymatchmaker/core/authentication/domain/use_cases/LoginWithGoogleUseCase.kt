package com.msoula.hobbymatchmaker.core.authentication.domain.use_cases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository

class LoginWithGoogleUseCase(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke(token: String) =
        authenticationRepository.signInWithCredential(googleToken = token)
}
