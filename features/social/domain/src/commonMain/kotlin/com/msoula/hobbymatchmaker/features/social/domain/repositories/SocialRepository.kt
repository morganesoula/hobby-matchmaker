package com.msoula.hobbymatchmaker.features.social.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import kotlinx.coroutines.flow.Flow

interface SocialRepository {
    suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerId: String?
    ): AppResult<List<SocialMemberDomainModel>, AppError>

    fun observeSocialCircle(uid: String): Flow<List<SocialMemberDomainModel>>
    fun observeIncomingInvites(uid: String): Flow<List<SocialInviteDomainModel>>
    fun observeSentInvites(uid: String): Flow<List<SocialInviteDomainModel>>
    suspend fun sendInvite(fromUid: String, toPseudo: String): AppResult<Unit, AppError>
    suspend fun cancelInvite(inviteId: String): AppResult<Unit, AppError>
    suspend fun acceptInvite(inviteId: String): AppResult<Unit, AppError>
    suspend fun declineInvite(inviteId: String): AppResult<Unit, AppError>
    suspend fun removeMember(ownerId: String, memberUid: String): AppResult<Unit, AppError>
}
