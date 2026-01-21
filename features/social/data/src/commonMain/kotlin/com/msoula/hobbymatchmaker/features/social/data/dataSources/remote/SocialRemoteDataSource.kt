package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.Invite
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleMember
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import kotlinx.coroutines.flow.Flow

interface SocialRemoteDataSource {
    suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerUid: String?
    ): AppResult<List<SocialMemberDomainModel>, AppError>

    suspend fun findUserByUid(uid: String): AppResult<SocialCircleMember, AppError>
    fun observeSocialCircle(uid: String): Flow<List<SocialMemberDomainModel>>
    suspend fun sendInvite(invite: Invite): AppResult<Unit, AppError>
    fun observeIncomingInvites(ownerUid: String): Flow<List<Invite>>
    fun observeSentInvited(ownerUid: String): Flow<List<Invite>>
    suspend fun markInviteAsAccepted(inviteId: String): AppResult<Unit, AppError>
    suspend fun markInviteAsDeclined(inviteId: String): AppResult<Unit, AppError>
    suspend fun cancelInvitation(inviteId: String): AppResult<Unit, AppError>
    suspend fun addToSocialCircle(socialCircleMember: SocialCircleMember): AppResult<Unit, AppError>
    suspend fun removeFromSocialCircle(
        ownerUid: String,
        memberUid: String
    ): AppResult<Unit, AppError>

    suspend fun acceptInviteAndAddMembers(
        inviteId: String,
        memberAddedToOwnerCircle: SocialCircleMember,
        ownerAddedToMemberCircle: SocialCircleMember
    ): AppResult<Unit, AppError>
}
