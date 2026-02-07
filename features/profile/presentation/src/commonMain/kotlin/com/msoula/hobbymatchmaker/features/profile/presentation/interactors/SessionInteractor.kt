package com.msoula.hobbymatchmaker.features.profile.presentation.interactors

import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveSessionStateUseCase

class SessionInteractor(
    private val observeSessionStateUseCase: ObserveSessionStateUseCase,
) {
    fun observeSessionState() = observeSessionStateUseCase()
}
