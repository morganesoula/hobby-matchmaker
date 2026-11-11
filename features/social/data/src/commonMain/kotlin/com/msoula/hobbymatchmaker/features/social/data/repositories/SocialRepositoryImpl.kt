package com.msoula.hobbymatchmaker.features.social.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import com.msoula.hobbymatchmaker.core.user.domain.repositories.UserDataRepository
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.SocialLocalDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.mappers.toSocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models.SocialCircleMemberLocalDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.SocialRemoteDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toBlockedUserDomainModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toInviteData
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toSocialCircleMember
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toSocialInvitationDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toSocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toUserReportData
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleEntryRemoteDataModel
import com.msoula.hobbymatchmaker.features.social.domain.models.BlockedUserDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.UserReportDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import com.msoula.hobbymatchmaker.features.social.domain.utils.SocialMatchingUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SocialRepositoryImpl(
    private val socialRemoteDataSource: SocialRemoteDataSource,
    private val socialLocalDataSource: SocialLocalDataSource,
    private val userDataRepository: UserDataRepository,
    private val sessionRepository: SessionRepository
) : SocialRepository {
    // A pseudo never changes owner, so a resolved uid stays valid for the whole session
    private val uidByPseudo = mutableMapOf<String, String>()
    private val uidByPseudoLock = Mutex()

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

    override suspend fun fetchFreshUserByUid(uid: String): AppResult<SocialMemberDomainModel, AppError> {
        userDataRepository.invalidateCache(uid)
        return findUserByUid(uid)
    }

    override fun observeSocialCircle(uid: String): Flow<List<SocialMemberDomainModel>> =
        channelFlow {
            val remoteDataByMemberUid = mutableMapOf<String, SocialCircleEntryRemoteDataModel>()
            val ownerUid = sessionRepository.getCurrentUserUid()

            launch {
                socialRemoteDataSource.observeSocialCircle(uid)
                    .collectLatest { remoteMembers ->
                        remoteMembers.forEach { member ->
                            remoteDataByMemberUid[member.memberUid] = member
                        }
                        val localModels = mapRemoteToLocal(remoteMembers, uid)
                        socialLocalDataSource.syncCircle(localModels, ownerUid)
                    }
            }

            socialLocalDataSource.observeSocialCircle(ownerUid)
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
                                ?: SocialMemberDomainModel.Initial.moviesLiked,
                            lastMatchAt = remoteData?.lastMatchAt
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
                socialLocalDataSource.syncCircle(localModels, uid)
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

    override fun observeIncomingInvites(
        ownerUid: String,
        toPseudo: String
    ): Flow<List<SocialInviteDomainModel>> =
        channelFlow {
            launch {
                socialRemoteDataSource.observeIncomingInvites(ownerUid)
                    .collectLatest { remoteInvites ->
                        val mapped = remoteInvites.map { it.toSocialInvitationDataModel() }
                        socialLocalDataSource.replaceIncomingInvites(toPseudo, mapped)
                    }
            }

            socialLocalDataSource.observeIncomingInvites(toPseudo)
                .map { list ->
                    list.map { it.toSocialInviteDomainModel() }
                        .withCounterparts(ownerUid) { invite -> invite.fromUid }
                }
                .collect { send(it) }
        }

    override suspend fun refreshIncomingInvites(ownerUid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.refreshIncomingInvites(ownerUid).flatMap { snapshot ->
            val mapped = snapshot.invites.map { it.toSocialInvitationDataModel() }
            // Never read the pseudo from the cached user: it can be stale, and then an empty refresh clears nothing
            val toPseudo = snapshot.toPseudo?.takeIf { it.isNotBlank() }

            if (toPseudo != null) {
                socialLocalDataSource.replaceIncomingInvites(toPseudo, mapped)
            } else {
                socialLocalDataSource.upsertIncomingInvites(mapped)
            }
        }

    override fun observeSentInvites(uid: String): Flow<List<SocialInviteDomainModel>> =
        channelFlow {
            launch {
                socialRemoteDataSource.observeSentInvites(uid)
                    .collectLatest { remoteInvites ->
                        val mapped = remoteInvites.map { it.toSocialInvitationDataModel() }
                        socialLocalDataSource.replaceSentInvites(uid, mapped)
                    }
            }

            socialLocalDataSource.observeSentInvites(uid)
                .map { list ->
                    list.map { invite -> invite.toSocialInviteDomainModel() }
                        .withCounterparts(uid) { invite -> invite.toPseudo?.let { resolveUidByPseudo(it) } }
                }
                .collect { send(it) }
        }

    override suspend fun refreshSentInvites(uid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.refreshSentInvites(uid).flatMap { invites ->
            socialLocalDataSource.replaceSentInvites(
                uid,
                invites.map { it.toSocialInvitationDataModel() })
        }

    /**
     * Fills in who is on the other side of each invite (name, avatar, common favorites). Without a
     * connection, or for an unknown user, the invite is returned as is and the UI falls back to the pseudo.
     */
    private suspend fun List<SocialInviteDomainModel>.withCounterparts(
        ownerUid: String,
        counterpartUidOf: suspend (SocialInviteDomainModel) -> String?
    ): List<SocialInviteDomainModel> {
        if (isEmpty()) return this

        val counterpartUids = map { counterpartUidOf(it)?.takeIf { uid -> uid.isNotBlank() } }
        val users = userDataRepository.getUsers(counterpartUids.filterNotNull().distinct())
            .let { (it as? AppResult.Success)?.data }
            .orEmpty()
        val ownerMovies = (userDataRepository.getUser(ownerUid) as? AppResult.Success)?.data?.moviesLiked

        return mapIndexed { index, invite ->
            val user = counterpartUids[index]?.let { users[it] } ?: return@mapIndexed invite
            invite.copy(
                counterpartUid = user.uid,
                counterpartName = user.name,
                counterpartAvatarUrl = user.avatarUrl,
                commonMoviesCount = SocialMatchingUtils.calculateCommonMoviesCount(ownerMovies, user.moviesLiked)
            )
        }
    }

    private suspend fun resolveUidByPseudo(pseudo: String): String? {
        val key = pseudo.lowercase()
        uidByPseudoLock.withLock { uidByPseudo[key] }?.let { return it }

        val uid = (socialRemoteDataSource.resolveUidByPseudo(pseudo) as? AppResult.Success)?.data ?: return null
        uidByPseudoLock.withLock { uidByPseudo[key] = uid }
        return uid
    }

    override suspend fun sendInvite(invite: SocialInviteDomainModel) =
        socialRemoteDataSource.sendInvite(invite.toInviteData())

    override suspend fun cancelInvite(inviteId: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.cancelInvitation(inviteId)

    override suspend fun dismissDeclinedInvite(inviteId: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.dismissDeclinedInvitation(inviteId)

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

    override suspend fun blockUser(ownerId: String, blockedUid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.blockUser(ownerId, blockedUid)

    override suspend fun unblockUser(ownerId: String, blockedUid: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.unblockUser(ownerId, blockedUid)

    override fun observeBlockedUsers(ownerId: String): Flow<List<BlockedUserDomainModel>> =
        socialRemoteDataSource.observeBlockedUsers(ownerId)
            .map { users -> users.map { it.toBlockedUserDomainModel() } }

    override suspend fun reportUser(report: UserReportDomainModel): AppResult<Unit, AppError> =
        socialRemoteDataSource.reportUser(report.toUserReportData())

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
            .flatMap { members ->
                val memberUIds = members.map { it.memberUid }
                userDataRepository.invalidateUsers(memberUIds)

                userDataRepository.getUsers(memberUIds).mapSuccess { usersMap ->
                    members.mapNotNull { member ->
                        val user = usersMap[member.memberUid] ?: return@mapNotNull null
                        user.toSocialMemberDomainModel(member.commonMoviesCount)
                    }
                }
            }
}
