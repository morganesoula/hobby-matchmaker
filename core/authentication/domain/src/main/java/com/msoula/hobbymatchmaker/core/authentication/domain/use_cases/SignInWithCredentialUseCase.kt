package com.msoula.hobbymatchmaker.core.authentication.domain.use_cases

import com.google.firebase.auth.AuthCredential
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository

class SignInWithCredentialUseCase(private val authenticationRepository: AuthenticationRepository) {

    suspend operator fun invoke(authCredential: AuthCredential) =
        authenticationRepository.signInWithCredential(authCredential)
}
