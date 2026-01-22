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

    suspend fun findUserByUid(
        uid: String
    ): AppResult<SocialMemberDomainModel?, AppError>

    fun observeSocialCircle(uid: String): Flow<List<SocialMemberDomainModel>>
    fun observeIncomingInvites(ownerUid: String): Flow<List<SocialInviteDomainModel>>
    fun observeSentInvites(uid: String): Flow<List<SocialInviteDomainModel>>
    suspend fun sendInvite(invite: SocialInviteDomainModel): AppResult<Unit, AppError>
    suspend fun cancelInvite(inviteId: String): AppResult<Unit, AppError>
    suspend fun acceptInvite(inviteId: String): AppResult<Unit, AppError>
    suspend fun declineInvite(inviteId: String): AppResult<Unit, AppError>
    suspend fun removeMember(ownerId: String, memberUid: String): AppResult<Unit, AppError>
    suspend fun addMember(
        ownerId: String,
        socialMemberDomainModel: SocialMemberDomainModel
    ): AppResult<Unit, AppError>

    suspend fun acceptInviteAndAddMembers(
        inviteId: String,
        owner: SocialMemberDomainModel,
        member: SocialMemberDomainModel
    ): AppResult<Unit, AppError>

    suspend fun checkSocialCircleLimit(
        ownerUid: String,
        invitingMemberUid: String
    ): AppResult<Boolean, AppError>
}
