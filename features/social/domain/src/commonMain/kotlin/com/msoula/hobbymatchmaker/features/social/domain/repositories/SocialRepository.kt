package com.msoula.hobbymatchmaker.features.social.domain.repositories

import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import kotlinx.coroutines.flow.Flow

interface SocialRepository {
    fun observeSocialCircle(uid: String): Flow<List<SocialMemberDomainModel>>
    fun observeIncomingInvites(uid: String): Flow<List<SocialInviteDomainModel>>
    fun observeSentInvites(uid: String): Flow<List<SocialInviteDomainModel>>
    suspend fun sendInvite(fromUid: String, toPseudo: String)
}
