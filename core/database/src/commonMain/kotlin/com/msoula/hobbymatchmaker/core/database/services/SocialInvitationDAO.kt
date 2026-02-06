package com.msoula.hobbymatchmaker.core.database.services

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.database.Social_invitation
import kotlinx.coroutines.flow.Flow

interface SocialInvitationDAO {
    fun observeIncomingInvites(toPseudo: String): Flow<List<Social_invitation>>
    fun observeSentInvites(fromUid: String): Flow<List<Social_invitation>>
    suspend fun upsertInvites(invites: List<Social_invitation>): AppResult<Unit, AppError>
}
