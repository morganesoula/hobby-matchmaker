package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.Invite
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleMember
import kotlinx.coroutines.flow.Flow

interface SocialRemoteDataSource {
    suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerUid: String?
    ): AppResult<List<SocialCircleMember>, AppError>

    suspend fun findUserByUid(uid: String): AppResult<SocialCircleMember, AppError>
    fun observeSocialCircle(uid: String): Flow<List<SocialCircleMember>>
    suspend fun sendInvite(invite: Invite): AppResult<Unit, AppError>
    suspend fun refreshIncomingInvites(ownerUid: String): AppResult<List<Invite>, AppError>
    suspend fun refreshSentInvites(ownerUid: String): AppResult<List<Invite>, AppError>
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

    suspend fun checkSocialCircleLimit(
        ownerUid: String,
        invitingMemberUid: String
    ): AppResult<Boolean, AppError>

    suspend fun getSocialCircleSnapshot(uid: String): AppResult<List<SocialCircleMember>, AppError>
    fun observeIncomingInvites(ownerUid: String): Flow<List<Invite>>
    fun observeSentInvites(ownerUid: String): Flow<List<Invite>>
}
