package com.msoula.hobbymatchmaker.features.social.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialCircleMemberDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialInvitationDataModel
import kotlinx.coroutines.flow.Flow

interface SocialLocalDataSource {
    fun observeSocialCircle(): Flow<List<SocialCircleMemberDataModel>>
    //suspend fun addToCircle(member: SocialCircleMemberDataEntity): AppResult<Unit, AppError>
    suspend fun removeFromCircle(memberUid: String): AppResult<Unit, AppError>
    suspend fun syncCircle(members: List<SocialCircleMemberDataModel>)
    fun observeIncomingInvites(toPseudo: String): Flow<List<SocialInvitationDataModel>>
    fun observeSentInvites(ownerUid: String): Flow<List<SocialInvitationDataModel>>
    suspend fun upsertIncomingInvites(invites: List<SocialInvitationDataModel>): AppResult<Unit, AppError>
    suspend fun upsertSentInvites(invites: List<SocialInvitationDataModel>): AppResult<Unit, AppError>
}
