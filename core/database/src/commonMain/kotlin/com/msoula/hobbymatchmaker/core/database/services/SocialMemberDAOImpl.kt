package com.msoula.hobbymatchmaker.core.database.services

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.models.SocialCircleMemberDataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SocialMemberDAOImpl(
    private val database: HMMDatabase
) : SocialMemberDAO {
    override fun insertSocialMember(socialCircleMemberDataEntity: SocialCircleMemberDataEntity) {
        database.hmm_databaseQueries.insertSocialMember(
            uid = socialCircleMemberDataEntity.uid,
            member_uid = socialCircleMemberDataEntity.memberUid,
            member_name = socialCircleMemberDataEntity.memberName,
            member_avatar_url = socialCircleMemberDataEntity.memberAvatarUrl,
            added_at = Clock.System.now().toEpochMilliseconds()
        )
    }

    override fun deleteSocialMember(userProfileUid: String, memberUid: String) {
        database.hmm_databaseQueries.deleteSocialMember(userProfileUid, memberUid)
    }

    override fun observeUserProfileMembers(userProfileUid: String): Flow<List<SocialCircleMemberDataEntity>> =
        database.hmm_databaseQueries.selectSocialMembersByUid(userProfileUid)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.map {
                    SocialCircleMemberDataEntity(
                        uid = it.uid,
                        memberUid = it.member_uid,
                        memberName = it.member_name,
                        memberAvatarUrl = it.member_avatar_url
                    )
                }
            }
}
