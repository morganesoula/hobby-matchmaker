package com.msoula.hobbymatchmaker.core.authentication.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import dev.gitlive.firebase.auth.AuthCredential

class LinkInWithCredentialUseCase(private val authenticationRepository: AuthenticationRepository) {

    suspend operator fun invoke(authCredential: AuthCredential) =
        authenticationRepository.linkInWithCredential(authCredential)
}
