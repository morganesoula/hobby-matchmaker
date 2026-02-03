package com.msoula.hobbymatchmaker.features.social.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.database.models.SocialCircleMemberDataEntity
import com.msoula.hobbymatchmaker.core.database.services.SocialMemberDAO
import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialCircleMemberDataEntity
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class SocialLocalDataSourceImpl(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val socialMemberDAO: SocialMemberDAO
) : SocialLocalDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSocialCircle(): Flow<List<SocialMemberDomainModel>> =
        sessionLocalDataSource.observeCurrentUid()
            .filter { it.isNotEmpty() }
            .flatMapLatest { uid ->
                socialMemberDAO.observeUserProfileMembers(uid)
                    .map { entities ->
                        entities.map { entity ->
                            SocialMemberDomainModel(
                                uid = entity.memberUid,
                                pseudo = entity.memberPseudo
                                    ?: SocialMemberDomainModel.Initial.pseudo,
                                name = entity.memberName ?: SocialMemberDomainModel.Initial.name,
                                avatarUrl = entity.memberAvatarUrl
                                    ?: SocialMemberDomainModel.Initial.avatarUrl,
                                commonMoviesCount = SocialMemberDomainModel.Initial.commonMoviesCount
                            )
                        }
                    }
            }

    override suspend fun addToCircle(member: SocialMemberDomainModel): AppResult<Unit, AppError> {
        val ownerUid = sessionLocalDataSource.observeCurrentUid().first()

        if (ownerUid.isEmpty()) return AppResult.Failure(
            AppError.Network.Unknown(
                Throwable("No session UID")
            )
        )

        return try {
            socialMemberDAO.insertSocialMember(
                SocialCircleMemberDataEntity(
                    uid = ownerUid,
                    memberUid = member.uid,
                    memberName = member.name,
                    memberAvatarUrl = member.avatarUrl,
                    memberPseudo = member.pseudo
                )
            )
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(AppError.Network.Unknown(t))
        }
    }

    override suspend fun removeFromCircle(memberUid: String): AppResult<Unit, AppError> {
        val ownerUid = sessionLocalDataSource.observeCurrentUid().first()

        if (ownerUid.isEmpty()) return AppResult.Failure(
            AppError.Network.Unknown(
                Throwable("No session UID")
            )
        )

        return try {
            socialMemberDAO.deleteSocialMember(ownerUid, memberUid)
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(AppError.Network.Unknown(t))
        }
    }

    override suspend fun syncCircle(members: List<SocialMemberDomainModel>) {
        val ownerUId = sessionLocalDataSource.observeCurrentUid().first()

        if (ownerUId.isEmpty()) return

        socialMemberDAO.replaceAll(
            ownerUid = ownerUId,
            members.map { it.toSocialCircleMemberDataEntity(ownerUId) }
        )
    }
}
