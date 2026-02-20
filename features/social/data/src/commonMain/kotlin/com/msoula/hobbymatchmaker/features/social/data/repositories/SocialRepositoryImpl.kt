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
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleEntryRemoteDataModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
        channelFlow {
            val remoteDataByMemberUid = mutableMapOf<String, SocialCircleEntryRemoteDataModel>()

            launch {
                socialRemoteDataSource.observeSocialCircle(uid)
                    .collectLatest { remoteMembers ->
                        remoteMembers.forEach { member ->
                            remoteDataByMemberUid[member.memberUid] = member
                        }
                        val localModels = mapRemoteToLocal(remoteMembers, uid)
                        socialLocalDataSource.syncCircle(localModels)
                    }
            }

            socialLocalDataSource.observeSocialCircle()
                .map { entities ->
                    entities.map { entity ->
                        val remoteData = remoteDataByMemberUid[entity.memberUid]
                        SocialMemberDomainModel(
                            uid = entity.memberUid,
                            pseudo = entity.memberPseudo
                                ?: SocialMemberDomainModel.Initial.pseudo,
                            name = entity.memberName ?: SocialMemberDomainModel.Initial.name,
                            avatarUrl = entity.memberAvatarUrl
                                ?: SocialMemberDomainModel.Initial.avatarUrl,
                            commonMoviesCount = remoteData?.commonMoviesCount
                                ?: SocialMemberDomainModel.Initial.commonMoviesCount,
                            moviesLiked = remoteData?.moviesLiked
                                ?: SocialMemberDomainModel.Initial.moviesLiked
                        )
                    }
                }
                .collect { send(it) }
        }

    override suspend fun syncMovieLikedToCircle(uid: String, movieId: Long, isFavorite: Boolean) =
        socialRemoteDataSource.updateMovieLikedInCircleEntries(uid, movieId, isFavorite)

    override suspend fun refreshSocialCircle(uid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource
            .getSocialCircleSnapshot(uid)
            .flatMap { remoteMembers ->
                val localModels = mapRemoteToLocal(remoteMembers, uid)
                socialLocalDataSource.syncCircle(localModels)
                AppResult.Success(Unit)
            }

    private fun mapRemoteToLocal(
        remoteMembers: List<SocialCircleEntryRemoteDataModel>,
        uid: String
    ): List<SocialCircleMemberLocalDataModel> =
        remoteMembers.map { member ->
            SocialCircleMemberLocalDataModel(
                ownerUid = uid,
                memberUid = member.memberUid,
                memberPseudo = member.memberPseudo,
                memberName = member.memberName,
                memberAvatarUrl = member.avatarUrl
            )
        }

    override fun observeIncomingInvites(toPseudo: String): Flow<List<SocialInviteDomainModel>> {
        return socialLocalDataSource.observeIncomingInvites(toPseudo)
            .map { list ->
                list.map { invite -> invite.toSocialInviteDomainModel() }
            }
    }

    override suspend fun refreshIncomingInvites(ownerUid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.refreshIncomingInvites(ownerUid).flatMap { invites ->
            val mapped = invites.map { it.toSocialInvitationDataModel() }
            val toPseudo = mapped.firstOrNull()?.toPseudo
                ?: userDataRepository.getUser(ownerUid)
                    .let { result -> (result as? AppResult.Success)?.data?.pseudo }

            if (toPseudo != null) {
                socialLocalDataSource.replaceIncomingInvites(toPseudo, mapped)
            } else {
                socialLocalDataSource.upsertIncomingInvites(mapped)
            }
        }

    override fun observeSentInvites(uid: String): Flow<List<SocialInviteDomainModel>> {
        return socialLocalDataSource.observeSentInvites(uid)
            .map { list ->
                list.map { invite -> invite.toSocialInviteDomainModel() }
            }
    }

    override suspend fun refreshSentInvites(uid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.refreshSentInvites(uid).flatMap { invites ->
            socialLocalDataSource.replaceSentInvites(
                uid,
                invites.map { it.toSocialInvitationDataModel() })
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
                userDataRepository.invalidateUsers(memberUIds)
                val usersByUid = userDataRepository.getUsers(memberUIds)
                members.mapNotNull { member ->
                    val user = usersByUid[member.memberUid] ?: return@mapNotNull null
                    user.toSocialMemberDomainModel(member.commonMoviesCount)
                }
            }
}
