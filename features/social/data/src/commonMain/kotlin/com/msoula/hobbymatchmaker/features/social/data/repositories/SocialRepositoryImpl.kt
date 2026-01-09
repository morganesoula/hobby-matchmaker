package com.msoula.hobbymatchmaker.features.social.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.SocialLocalDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.SocialRemoteDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toInviteData
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toSocialCircleMember
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toSocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toSocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SocialRepositoryImpl(
    private val socialLocalDataSource: SocialLocalDataSource,
    private val socialRemoteDataSource: SocialRemoteDataSource
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
        socialRemoteDataSource.observeSocialCircle(uid)

    override fun observeIncomingInvites(ownerUid: String): Flow<List<SocialInviteDomainModel>> {
        return socialRemoteDataSource.observeIncomingInvites(ownerUid)
            .map { list ->
                list.map { invite -> invite.toSocialInviteDomainModel() }
            }
    }

    override fun observeSentInvites(uid: String): Flow<List<SocialInviteDomainModel>> {
        return socialRemoteDataSource.observeSentInvited(uid)
            .map { list ->
                list.map { invite -> invite.toSocialInviteDomainModel() }
            }
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
}
