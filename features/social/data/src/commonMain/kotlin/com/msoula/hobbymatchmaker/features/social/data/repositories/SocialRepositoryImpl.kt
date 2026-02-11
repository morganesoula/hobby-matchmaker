package com.msoula.hobbymatchmaker.features.social.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.user.domain.repositories.UserDataRepository
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.SocialLocalDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialCircleMemberLocalDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.SocialRemoteDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toInviteData
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toSocialCircleMember
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toSocialInvitationDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toSocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SocialRepositoryImpl(
    private val socialRemoteDataSource: SocialRemoteDataSource,
    private val socialLocalDataSource: SocialLocalDataSource,
    private val userDataRepository: UserDataRepository
) : SocialRepository {
    override suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerId: String?
    ): AppResult<List<SocialMemberDomainModel>, AppError> =
        socialRemoteDataSource.searchUsersByPseudo(pseudo, ownerId).mapSuccess { members ->
            userDataRepository.prefetchUsers(members.map { it.uid })
            members.map { it.toSocialMemberDomainModel() }
        }

    override suspend fun findUserByUid(uid: String): AppResult<SocialMemberDomainModel, AppError> =
        userDataRepository.getUser(uid)
            .flatMap { member ->
                AppResult.Success(member.toSocialMemberDomainModel())
            }

    override fun observeSocialCircle(uid: String): Flow<List<SocialMemberDomainModel>> =
        socialLocalDataSource.observeSocialCircle()
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

    override suspend fun refreshSocialCircle(uid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource
            .getSocialCircleSnapshot(uid)
            .flatMap { remoteMembers ->
                val users = userDataRepository.getUsers(remoteMembers.map { it.memberUid })
                val localModels = remoteMembers.mapNotNull { member ->
                    users[member.memberUid]?.let { user ->
                        SocialCircleMemberLocalDataModel(
                            ownerUid = uid,
                            memberUid = member.memberUid,
                            memberPseudo = user.pseudo,
                            memberName = user.name,
                            memberAvatarUrl = user.avatarUrl
                        )
                    }
                }
                socialLocalDataSource.syncCircle(localModels)
                AppResult.Success(Unit)
            }

    override fun observeIncomingInvites(toPseudo: String): Flow<List<SocialInviteDomainModel>> {
        return socialLocalDataSource.observeIncomingInvites(toPseudo)
            .map { list ->
                list.map { invite -> invite.toSocialInviteDomainModel() }
            }
    }

    override suspend fun refreshIncomingInvites(ownerUid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.refreshIncomingInvites(ownerUid).flatMap { invites ->
            socialLocalDataSource.upsertIncomingInvites(
                invites.map { it.toSocialInvitationDataModel() }
            )
        }

    override fun observeSentInvites(uid: String): Flow<List<SocialInviteDomainModel>> {
        return socialLocalDataSource.observeSentInvites(uid)
            .map { list ->
                list.map { invite -> invite.toSocialInviteDomainModel() }
            }
    }

    override suspend fun refreshSentInvites(uid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.refreshSentInvites(uid).flatMap { invites ->
            socialLocalDataSource.upsertSentInvites(invites.map { it.toSocialInvitationDataModel() })
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
        socialRemoteDataSource
            .getSocialCircleSnapshot(uid)
            .mapSuccess { members ->
                val memberUIds = members.map { it.memberUid }
                val usersByUid = userDataRepository.getUsers(memberUIds)
                members.mapNotNull { member ->
                    val user = usersByUid[member.memberUid] ?: return@mapNotNull null

                    user.toSocialMemberDomainModel(member.commonMoviesCount)
                }
            }
}
