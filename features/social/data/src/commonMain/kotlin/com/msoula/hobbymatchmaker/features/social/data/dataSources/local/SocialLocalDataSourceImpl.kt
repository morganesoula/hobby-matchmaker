package com.msoula.hobbymatchmaker.features.social.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeCallStorage
import com.msoula.hobbymatchmaker.core.database.services.SocialInvitationDAO
import com.msoula.hobbymatchmaker.core.database.services.SocialMemberDAO
import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialCircleMemberDataEntity
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialCircleMemberDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialInvitation
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialInvitationDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialCircleMemberLocalDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialInvitationDataModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class SocialLocalDataSourceImpl(
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val socialInvitationDAO: SocialInvitationDAO,
    private val socialMemberDAO: SocialMemberDAO
) : SocialLocalDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSocialCircle(): Flow<List<SocialCircleMemberLocalDataModel>> =
        sessionLocalDataSource.observeCurrentUid()
            .filter { it.isNotEmpty() }
            .flatMapLatest { uid ->
                socialMemberDAO.observeUserProfileMembers(uid).map { members ->
                    members.map { it.toSocialCircleMemberDataModel() }
                }
            }

    /* override suspend fun addToCircle(member: SocialCircleMemberDataEntity): AppResult<Unit, AppError> {
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
                    memberName = member.memberName,
                    memberAvatarUrl = member.memberAvatarUrl,
                    memberPseudo = member.memberPseudo
                )
            )
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Failure(AppError.Network.Unknown(t))
        }
    } */

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

    override suspend fun syncCircle(members: List<SocialCircleMemberLocalDataModel>) {
        val ownerUId = sessionLocalDataSource.observeCurrentUid().first()

        if (ownerUId.isEmpty()) return

        socialMemberDAO.replaceAll(
            ownerUid = ownerUId,
            members.map { it.toSocialCircleMemberDataEntity() }
        )
    }

    override fun observeIncomingInvites(toPseudo: String): Flow<List<SocialInvitationDataModel>> =
        socialInvitationDAO.observeIncomingInvites(toPseudo).map { invitations ->
            invitations.map { it.toSocialInvitationDataModel() }
        }

    override suspend fun upsertIncomingInvites(invites: List<SocialInvitationDataModel>): AppResult<Unit, AppError> =
        safeCallStorage {
            socialInvitationDAO.upsertInvites(invites.map { it.toSocialInvitation() })
        }

    override fun observeSentInvites(ownerUid: String): Flow<List<SocialInvitationDataModel>> =
        socialInvitationDAO.observeSentInvites(ownerUid).map { invitations ->
            invitations.map { it.toSocialInvitationDataModel() }
        }

    override suspend fun upsertSentInvites(invites: List<SocialInvitationDataModel>): AppResult<Unit, AppError> =
        safeCallStorage {
            socialInvitationDAO.upsertInvites(invites.map { it.toSocialInvitation() })
        }

    override suspend fun replaceSentInvites(
        fromUid: String,
        invites: List<SocialInvitationDataModel>
    ): AppResult<Unit, AppError> =
        socialInvitationDAO.replaceSentInvites(fromUid, invites.map { it.toSocialInvitation() })

    override suspend fun replaceIncomingInvites(
        toPseudo: String,
        invites: List<SocialInvitationDataModel>
    ): AppResult<Unit, AppError> =
        socialInvitationDAO.replaceIncomingInvites(toPseudo, invites.map { it.toSocialInvitation() })
}
