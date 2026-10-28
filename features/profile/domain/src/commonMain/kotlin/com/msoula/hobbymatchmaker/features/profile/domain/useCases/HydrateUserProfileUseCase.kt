package com.msoula.hobbymatchmaker.features.profile.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import kotlinx.coroutines.flow.first

/**
 * Makes sure a local profile exists for [uid], restoring it from Firestore when possible.
 * Succeeds with `true` when the user already has a pseudo, `false` when they still need to pick one.
 * On a remote failure, a default local profile is still created and the failure is returned so the
 * caller can decide not to gate the user while offline.
 */
class HydrateUserProfileUseCase(
    private val userProfileRepository: UserProfileRepository,
    private val createDefaultUserProfileUseCase: CreateDefaultUserProfileUseCase
) {
    suspend operator fun invoke(uid: String, fallbackName: String = ""): AppResult<Boolean, AppError> {
        val localProfile = userProfileRepository.observeCurrentUserProfile(uid).first()
        if (localProfile != null && localProfile.pseudo.isNotBlank()) return AppResult.Success(true)

        return userProfileRepository.fetchRemoteUserProfile(uid)
            .onFailure { createDefaultUserProfileUseCase(uid, fallbackName) }
            .flatMap { remoteProfile ->
                if (remoteProfile == null || remoteProfile.pseudo.isBlank()) {
                    val name = remoteProfile?.name?.takeIf { it.isNotBlank() } ?: fallbackName
                    return@flatMap createDefaultUserProfileUseCase(uid, name).mapSuccess { false }
                }

                userProfileRepository.ensurePseudoReservation(uid, remoteProfile.pseudo)
                    .onFailure { Logger.e("Pseudo reservation backfill failed: $it") }

                userProfileRepository.upsertUserProfile(
                    remoteProfile.copy(likedMoviesCount = localProfile?.likedMoviesCount ?: 0)
                ).mapSuccess { true }
            }
    }
}
