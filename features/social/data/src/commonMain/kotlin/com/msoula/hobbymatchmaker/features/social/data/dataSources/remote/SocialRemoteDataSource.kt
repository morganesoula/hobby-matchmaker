package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.InviteDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleEntryRemoteDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleMemberRemoteDataModel
import kotlinx.coroutines.flow.Flow

interface SocialRemoteDataSource {
    suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerUid: String?
    ): AppResult<List<SocialCircleMemberRemoteDataModel>, AppError>

    fun observeSocialCircle(uid: String): Flow<List<SocialCircleEntryRemoteDataModel>>
    suspend fun sendInvite(inviteDataModel: InviteDataModel): AppResult<Unit, AppError>
    suspend fun refreshIncomingInvites(ownerUid: String): AppResult<List<InviteDataModel>, AppError>
    suspend fun refreshSentInvites(ownerUid: String): AppResult<List<InviteDataModel>, AppError>
    suspend fun markInviteAsAccepted(inviteId: String): AppResult<Unit, AppError>
    suspend fun markInviteAsDeclined(inviteId: String): AppResult<Unit, AppError>
    suspend fun cancelInvitation(inviteId: String): AppResult<Unit, AppError>
    suspend fun addToSocialCircle(socialCircleMemberDataModel: SocialCircleMemberRemoteDataModel): AppResult<Unit, AppError>
    suspend fun removeFromSocialCircle(
        ownerUid: String,
        memberUid: String
    ): AppResult<Unit, AppError>

    suspend fun acceptInviteAndAddMembers(
        inviteId: String,
        memberAddedToOwnerCircle: SocialCircleMemberRemoteDataModel,
        ownerAddedToMemberCircle: SocialCircleMemberRemoteDataModel
    ): AppResult<Unit, AppError>

    suspend fun checkSocialCircleLimit(
        ownerUid: String,
        invitingMemberUid: String
    ): AppResult<Boolean, AppError>

    suspend fun getSocialCircleSnapshot(uid: String): AppResult<List<SocialCircleEntryRemoteDataModel>, AppError>

    suspend fun updateMovieLikedInCircleEntries(
        ownerUid: String,
        movieId: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError>

    fun observeIncomingInvites(ownerUid: String): Flow<List<InviteDataModel>>
    fun observeSentInvites(ownerUid: String): Flow<List<InviteDataModel>>
}
