package com.msoula.hobbymatchmaker.core.session.domain.useCases

import com.benasher44.uuid.uuid4
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import kotlin.uuid.ExperimentalUuidApi

class SetCurrentUserProfileUuidUseCase(
    private val sessionRepository: SessionRepository
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(authenticatedUid: String? = null): AppResult<String, AppError> {
        authenticatedUid?.let {
            return sessionRepository
                .setCurrentUserUid(authenticatedUid)
                .mapSuccess {
                    Logger.d("Set current user uid to $authenticatedUid")
                    authenticatedUid
                }
        }

        val existing = sessionRepository.getCurrentUserUid()
        if (existing.isNotBlank()) return AppResult.Success(existing)

        val guest = "guest:${uuid4()}"
        return sessionRepository
            .setCurrentUserUid(guest)
            .mapSuccess { guest }
    }
}
