package com.msoula.hobbymatchmaker.core.database.services

import com.msoula.hobbymatchmaker.core.database.models.SocialCircleMemberDataEntity
import kotlinx.coroutines.flow.Flow

interface SocialMemberDAO {
    fun insertSocialMember(socialCircleMemberDataEntity: SocialCircleMemberDataEntity)
    fun deleteSocialMember(userProfileUid: String, memberUid: String)
    fun observeUserProfileMembers(userProfileUid: String): Flow<List<SocialCircleMemberDataEntity>>
    fun replaceAll(
        ownerUid: String,
        members: List<SocialCircleMemberDataEntity>
    )
}
