package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository

class SignInWithSocialProviderUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(
        providerType: ProviderType,
        credentialProvider: suspend () -> Any?
    ) = authenticationRepository.signInWithSocialProvider(providerType, credentialProvider)
}
