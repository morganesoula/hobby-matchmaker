package com.msoula.hobbymatchmaker.core.authentication.domain.use_cases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository

class IsFirstSignInUseCase(private val authenticationRepository: AuthenticationRepository) {

    suspend operator fun invoke(uid: String): Boolean {
        return authenticationRepository.isFirstSignIn(uid)
    }
}
