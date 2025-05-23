package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import dev.gitlive.firebase.auth.AuthCredential

class SignInWithCredentialUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(authCredential: AuthCredential, providerType: ProviderType) =
        authenticationRepository.signInWithCredential(authCredential, providerType)
}
