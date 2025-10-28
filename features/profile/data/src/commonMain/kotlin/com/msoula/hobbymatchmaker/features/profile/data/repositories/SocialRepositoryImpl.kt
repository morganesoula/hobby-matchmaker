package com.msoula.hobbymatchmaker.features.profile.data.repositories

import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.SocialLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.models.SocialMemberLocalDataModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel.Companion.DEFAULT_AVATAR_URL
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserSummaryDomainModel.Companion.DEFAULT_NAME
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.SocialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SocialRepositoryImpl(
    private val socialLocalDataSource: SocialLocalDataSource
) : SocialRepository {

    override fun observeSocialCircleCount(): Flow<Int> =
        socialLocalDataSource.observeSocialCircleCount()

    override fun observeSocialCircle(): Flow<List<UserSummaryDomainModel>> =
        socialLocalDataSource.observeSocialCircle().map { members ->
            if (members.isNotEmpty()) {
                members.map {
                    UserSummaryDomainModel(
                        uid = it.memberId,
                        name = it.name ?: DEFAULT_NAME,
                        avatarUrl = it.avatarUrl ?: DEFAULT_AVATAR_URL
                    )
                }
            } else emptyList()
        }

    override suspend fun addToCircle(memberUid: String) {
        socialLocalDataSource.addToCircle(
            SocialMemberLocalDataModel(memberUid, null, null)
        )
    }

    override suspend fun removeFromCircle(memberUid: String) {
        socialLocalDataSource.removeFromCircle(memberUid)
    }
}
