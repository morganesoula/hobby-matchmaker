package com.msoula.hobbymatchmaker.features.profile.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel
import kotlinx.coroutines.flow.Flow

interface SocialRepository {
    fun observeSocialCircleCount(): Flow<Int>
    fun observeSocialCircle(): Flow<List<UserSummaryDomainModel>>
    suspend fun inviteToCircle(currentUserUid: String?, pseudo: String): AppResult<Unit, AppError>
    suspend fun addToCircle(memberUid: String)
    suspend fun removeFromCircle(memberUid: String)
    suspend fun searchUsersByPseudo(
        pseudo: String,
        currentUserUid: String? = null
    ): AppResult<List<String>, AppError>
}
