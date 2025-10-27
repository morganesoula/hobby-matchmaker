package com.msoula.hobbymatchmaker.features.profile.data.dataSources.local

import com.msoula.hobbymatchmaker.core.database.models.SocialCircleMemberDataEntity
import com.msoula.hobbymatchmaker.core.database.services.SocialMemberDAO
import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.models.SocialMemberLocalDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SocialLocalDataSourceImpl(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val socialMemberDAO: SocialMemberDAO
) : SocialLocalDataSource {
    override fun observeSocialCircle(): Flow<List<SocialMemberLocalDataModel>> =
        flow {
            val uid = sessionLocalDataSource.observeCurrentUid().first()
            if (uid.isEmpty()) return@flow emit(emptyList())
            emitAll(
                socialMemberDAO.observeUserProfileMembers(uid)
                    .map { list ->
                        list.map {
                            SocialMemberLocalDataModel(
                                memberId = it.memberUid,
                                name = it.memberName,
                                avatarUrl = it.memberAvatarUrl
                            )
                        }
                    }
            )

        }

    override fun observeSocialCircleCount(): Flow<Int> =
        observeSocialCircle().map { it.size }

    override suspend fun addToCircle(member: SocialMemberLocalDataModel) {
        val uid = sessionLocalDataSource.observeCurrentUid().first()
        if (uid.isEmpty()) return

        socialMemberDAO.insertSocialMember(
            SocialCircleMemberDataEntity(
                uid = uid,
                memberUid = member.memberId,
                memberName = member.name,
                memberAvatarUrl = member.avatarUrl
            )
        )
    }

    override suspend fun removeFromCircle(memberUid: String) {
        val uid = sessionLocalDataSource.observeCurrentUid().first()
        if (uid.isEmpty()) return

        socialMemberDAO.deleteSocialMember(uid, memberUid)
    }
}
