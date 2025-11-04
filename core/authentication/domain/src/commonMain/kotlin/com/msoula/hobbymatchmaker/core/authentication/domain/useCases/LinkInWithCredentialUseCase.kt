package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository

class LinkInWithCredentialUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(
        providerType: ProviderType,
        credentialProvider: suspend () -> Any?
    ) = authenticationRepository.linkInWithCredential(providerType, credentialProvider)
}
