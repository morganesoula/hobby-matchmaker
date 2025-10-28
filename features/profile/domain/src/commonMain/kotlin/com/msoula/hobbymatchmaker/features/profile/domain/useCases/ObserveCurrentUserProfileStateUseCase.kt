package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import com.msoula.hobbymatchmaker.features.profile.domain.models.ProfileState
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveCurrentUserProfileStateUseCase(
    private val sessionRepository: SessionRepository,
    private val userProfileRepository: UserProfileRepository
) {

    operator fun invoke(): Flow<ProfileState> =
        combine(
            sessionRepository.observeCurrentUserUid(),
            userProfileRepository.observeCurrentUserProfile()
        ) { uid, profile ->
            val session = when {
                uid.isEmpty() -> null
                uid.startsWith("guest:") -> SessionState.Guest(uid)
                else -> SessionState.Authenticated(uid)
            }
            session to profile
        }
            .map { (session, profile) ->
                when (session) {
                    is SessionState.Guest -> {
                        profile?.let {
                            ProfileState.Present(profile)
                        } ?: ProfileState.Incomplete(session.uid)
                    }

                    is SessionState.Authenticated -> {
                        profile?.let {
                            ProfileState.Present(profile)
                        } ?: ProfileState.Incomplete(session.uid)
                    }

                    else -> {
                        Logger.d("Unknown state $session")
                        ProfileState.Incomplete("")
                    }
                }
            }
            .distinctUntilChanged()
}
