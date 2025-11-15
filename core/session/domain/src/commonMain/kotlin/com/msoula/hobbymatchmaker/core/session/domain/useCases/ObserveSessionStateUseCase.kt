package com.msoula.hobbymatchmaker.core.session.domain.useCases

import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveSessionStateUseCase(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<SessionState?> =
        sessionRepository.observeCurrentUserUid().map { uid ->
            Logger.d("Inside observeSessionStateUseCase with uid:$uid")
            when {
                uid.isEmpty() -> null
                uid.startsWith("guest:") -> SessionState.Guest(uid)
                else -> SessionState.Authenticated(uid)
            }
        }
}
