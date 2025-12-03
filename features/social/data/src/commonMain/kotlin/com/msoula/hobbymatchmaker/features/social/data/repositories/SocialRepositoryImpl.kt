package com.msoula.hobbymatchmaker.features.social.data.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.SocialLocalDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.SocialRemoteDataSource
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import kotlinx.coroutines.flow.Flow

class SocialRepositoryImpl(
    private val socialLocalDataSource: SocialLocalDataSource,
    private val socialRemoteDataSource: SocialRemoteDataSource
) : SocialRepository {
    override suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerId: String?
    ): AppResult<List<SocialMemberDomainModel>, AppError> =
        socialRemoteDataSource.searchUsersByPseudo(pseudo, ownerId)

    override fun observeSocialCircle(uid: String): Flow<List<SocialMemberDomainModel>> =
        socialLocalDataSource.observeSocialCircle()

    override fun observeIncomingInvites(uid: String): Flow<List<SocialInviteDomainModel>> =
        socialRemoteDataSource.observeIncomingInvites(uid)

    override fun observeSentInvites(uid: String): Flow<List<SocialInviteDomainModel>> =
        socialRemoteDataSource.observeSentInvited(uid)

    override suspend fun sendInvite(fromUid: String, toPseudo: String) =
        socialRemoteDataSource.sendInvite(fromUid, toPseudo)

    // TODO Check here method called
    override suspend fun cancelInvite(inviteId: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.markInviteAsDeclined(inviteId)

    override suspend fun acceptInvite(inviteId: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.markInviteAsAccepted(inviteId)

    override suspend fun declineInvite(inviteId: String): AppResult<Unit, AppError> =
        socialRemoteDataSource.markInviteAsDeclined(inviteId)

    override suspend fun removeMember(
        ownerId: String,
        memberUid: String
    ): AppResult<Unit, AppError> =
        socialRemoteDataSource.removeFromSocialCircle(ownerId, memberUid)

}
