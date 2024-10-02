package com.msoula.hobbymatchmaker.core.authentication.domain.use_cases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository

class FetchFirebaseUserInfo(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke() = authenticationRepository.fetchFirebaseUserInfo()
}
