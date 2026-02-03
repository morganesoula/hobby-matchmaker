package com.msoula.hobbymatchmaker.core.database.services

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.msoula.hobbymatchmaker.core.common.DispatcherProvider
import com.msoula.hobbymatchmaker.core.database.HMMDatabase
import com.msoula.hobbymatchmaker.core.database.models.SocialCircleMemberDataEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SocialMemberDAOImpl(
    private val database: HMMDatabase,
    private val dispatcherProvider: DispatcherProvider
) : SocialMemberDAO {
    override fun insertSocialMember(socialCircleMemberDataEntity: SocialCircleMemberDataEntity) {
        database.hmm_databaseQueries.insertSocialMember(
            uid = socialCircleMemberDataEntity.uid,
            member_uid = socialCircleMemberDataEntity.memberUid,
            member_name = socialCircleMemberDataEntity.memberName,
            member_pseudo = socialCircleMemberDataEntity.memberPseudo,
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
            .mapToList(dispatcherProvider.io)
            .map { list ->
                list.map {
                    SocialCircleMemberDataEntity(
                        uid = it.uid,
                        memberUid = it.member_uid,
                        memberName = it.member_name,
                        memberPseudo = it.member_pseudo,
                        memberAvatarUrl = it.member_avatar_url
                    )
                }
            }

    override fun replaceAll(
        ownerUid: String,
        members: List<SocialCircleMemberDataEntity>
    ) {
        database.hmm_databaseQueries.deleteAllSocialMembersByUid(ownerUid)
        members.forEach { insertSocialMember(it) }
    }
}
