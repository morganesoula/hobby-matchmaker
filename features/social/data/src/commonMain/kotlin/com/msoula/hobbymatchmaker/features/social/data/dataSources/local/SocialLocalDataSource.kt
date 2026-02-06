package com.msoula.hobbymatchmaker.features.social.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.database.Social_invitation
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import kotlinx.coroutines.flow.Flow

interface SocialLocalDataSource {
    fun observeSocialCircle(): Flow<List<SocialMemberDomainModel>>
    suspend fun addToCircle(member: SocialMemberDomainModel): AppResult<Unit, AppError>
    suspend fun removeFromCircle(memberUid: String): AppResult<Unit, AppError>
    suspend fun syncCircle(members: List<SocialMemberDomainModel>)
    fun observeIncomingInvites(toPseudo: String): Flow<List<Social_invitation>>
    fun observeSentInvites(ownerUid: String): Flow<List<Social_invitation>>
    suspend fun upsertIncomingInvites(invites: List<Social_invitation>): AppResult<Unit, AppError>
    suspend fun upsertSentInvites(invites: List<Social_invitation>): AppResult<Unit, AppError>
}
