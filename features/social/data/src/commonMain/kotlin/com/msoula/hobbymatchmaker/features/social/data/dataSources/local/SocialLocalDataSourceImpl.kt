package com.msoula.hobbymatchmaker.features.social.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeCallStorage
import com.msoula.hobbymatchmaker.core.database.services.SocialInvitationDAO
import com.msoula.hobbymatchmaker.core.database.services.SocialMemberDAO
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialCircleMemberDataEntity
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialCircleMemberDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialInvitation
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialInvitationDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialCircleMemberLocalDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialInvitationDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class SocialLocalDataSourceImpl(
    private val socialInvitationDAO: SocialInvitationDAO,
    private val socialMemberDAO: SocialMemberDAO
) : SocialLocalDataSource {

    override fun observeSocialCircle(uid: String?): Flow<List<SocialCircleMemberLocalDataModel>> =
        uid?.let { safeUid ->
            socialMemberDAO.observeUserProfileMembers(safeUid).map { members ->
                members.map { it.toSocialCircleMemberDataModel() }
            }
        } ?: flowOf(emptyList())

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

    override suspend fun removeFromCircle(memberUid: String, ownerUid: String?): AppResult<Unit, AppError> {
        return ownerUid?.let {
            try {
                socialMemberDAO.deleteSocialMember(ownerUid, memberUid)
                AppResult.Success(Unit)
            } catch (t: Throwable) {
                AppResult.Failure(AppError.Network.Unknown(t))
            }
        } ?: AppResult.Failure(
            AppError.Network.Unknown(
                Throwable("No session UID")
            )
        )
    }

    override suspend fun syncCircle(members: List<SocialCircleMemberLocalDataModel>, ownerUid: String?) {
        ownerUid?.let {
            socialMemberDAO.replaceAll(
                ownerUid = it,
                members.map { it.toSocialCircleMemberDataEntity() }
            )
        } ?: return
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
