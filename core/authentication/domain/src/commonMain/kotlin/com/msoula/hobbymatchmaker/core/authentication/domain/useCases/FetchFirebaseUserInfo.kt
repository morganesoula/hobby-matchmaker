package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository

class FetchFirebaseUserInfo(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke() = authenticationRepository.fetchFirebaseUserInfo()
}
