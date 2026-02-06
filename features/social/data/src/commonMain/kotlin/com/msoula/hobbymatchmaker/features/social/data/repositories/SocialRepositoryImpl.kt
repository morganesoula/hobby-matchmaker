package com.msoula.hobbymatchmaker.features.social.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.SocialLocalDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialInvitation
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.SocialRemoteDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toInviteData
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toSocialCircleMember
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toSocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SocialRepositoryImpl(
    private val socialRemoteDataSource: SocialRemoteDataSource,
    private val socialLocalDataSource: SocialLocalDataSource
) : SocialRepository {
    override suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerId: String?
    ): AppResult<List<SocialMemberDomainModel>, AppError> =
        socialRemoteDataSource.searchUsersByPseudo(pseudo, ownerId)

    override suspend fun findUserByUid(uid: String): AppResult<SocialMemberDomainModel?, AppError> =
        socialRemoteDataSource.findUserByUid(uid).mapSuccess { member ->
            member.toSocialMemberDomainModel()
        }

    override fun observeSocialCircle(uid: String): Flow<List<SocialMemberDomainModel>> =
        socialLocalDataSource.observeSocialCircle()

    override suspend fun refreshSocialCircle(uid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.getSocialCircleSnapshot(uid).flatMap { remoteMembers ->
            socialLocalDataSource.syncCircle(remoteMembers)
            AppResult.Success(Unit)
        }

    override fun observeIncomingInvites(ownerUid: String): Flow<List<SocialInviteDomainModel>> {
        return socialLocalDataSource.observeIncomingInvites(ownerUid)
            .map { list ->
                list.map { invite -> invite.toSocialInviteDomainModel() }
            }
    }

    override suspend fun refreshIncomingInvites(ownerUid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.refreshIncomingInvites(ownerUid).flatMap { invites ->
            socialLocalDataSource.upsertIncomingInvites(invites.map { it.toSocialInvitation() })
        }

    override fun observeSentInvites(uid: String): Flow<List<SocialInviteDomainModel>> {
        return socialLocalDataSource.observeSentInvites(uid)
            .map { list ->
                list.map { invite -> invite.toSocialInviteDomainModel() }
            }
    }

    override suspend fun refreshSentInvites(uid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.refreshSentInvites(uid).flatMap { invites ->
            socialLocalDataSource.upsertSentInvites(invites.map { it.toSocialInvitation() })
        }

    override suspend fun sendInvite(invite: SocialInviteDomainModel) =
        socialRemoteDataSource.sendInvite(invite.toInviteData())

    override suspend fun cancelInvite(inviteId: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.cancelInvitation(inviteId)

    override suspend fun acceptInvite(inviteId: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.markInviteAsAccepted(inviteId)

    override suspend fun declineInvite(inviteId: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.markInviteAsDeclined(inviteId)

    override suspend fun addMember(
        ownerId: String,
        socialMemberDomainModel: SocialMemberDomainModel
    ): AppResult<Unit, AppError> =
        socialRemoteDataSource.addToSocialCircle(
            socialMemberDomainModel.toSocialCircleMember(
                ownerId
            )
        )

    override suspend fun removeMember(
        ownerId: String,
        memberUid: String
    ): AppResult<Unit, AppError> =
        socialRemoteDataSource.removeFromSocialCircle(ownerId, memberUid)

    override suspend fun acceptInviteAndAddMembers(
        inviteId: String,
        owner: SocialMemberDomainModel,
        member: SocialMemberDomainModel
    ): AppResult<Unit, AppError> =
        socialRemoteDataSource.acceptInviteAndAddMembers(
            inviteId = inviteId,
            memberAddedToOwnerCircle = member.toSocialCircleMember(owner.uid),
            ownerAddedToMemberCircle = owner.toSocialCircleMember(member.uid)
        )

    override suspend fun checkSocialCircleLimit(
        ownerUid: String,
        invitingMemberUid: String
    ): AppResult<Boolean, AppError> =
        socialRemoteDataSource.checkSocialCircleLimit(ownerUid, invitingMemberUid)

    override suspend fun getSocialCircleSnapshot(uid: String): AppResult<List<SocialMemberDomainModel>, AppError> =
        socialRemoteDataSource.getSocialCircleSnapshot(uid)
}
