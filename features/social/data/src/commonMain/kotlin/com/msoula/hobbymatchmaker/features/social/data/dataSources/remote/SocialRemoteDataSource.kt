package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import kotlinx.coroutines.flow.Flow

interface SocialRemoteDataSource {
    suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerUid: String?
    ): AppResult<List<SocialMemberDomainModel>, AppError>

    suspend fun sendInvite(fromUid: String, toUid: String): AppResult<Unit, AppError>
    fun observeIncomingInvites(ownerUid: String): Flow<List<SocialInviteDomainModel>>
    fun observeSentInvited(ownerUid: String): Flow<List<SocialInviteDomainModel>>
    suspend fun markInviteAsAccepted(inviteId: String): AppResult<Unit, AppError>
    suspend fun markInviteAsDeclined(inviteId: String): AppResult<Unit, AppError>
    suspend fun addToSocialCircle(ownerUid: String, memberUid: String): AppResult<Unit, AppError>
    suspend fun removeFromSocialCircle(
        ownerUid: String,
        memberUid: String
    ): AppResult<Unit, AppError>
}
