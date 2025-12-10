package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.Invite
import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel.Companion.DEFAULT_AVATAR_URL
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialUserSummaryDomainModel
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SocialRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : SocialRemoteDataSource {

    override suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerUid: String?
    ): AppResult<List<SocialMemberDomainModel>, AppError> =
        safeFirebaseCall {
            val searchTerm = pseudo.trim().lowercase()
            if (searchTerm.isEmpty()) return@safeFirebaseCall emptyList()

            val endTerm = searchTerm + '\uf8ff'

            val documents = try {
                firestore
                    .collection("users")
                    .orderBy("information.pseudo", Direction.ASCENDING)
                    .startAt(searchTerm)
                    .endAt(endTerm)
                    .limit(20)
                    .get()
                    .documents
            } catch (e: Exception) {
                Logger.d("SocialRemoteDataSource: Error during Firestore query: ${e.message}")
                throw e
            }

            val results = documents
                .mapNotNull { document ->
                    val uid = document.id
                    val userPseudo = document.get<String?>("information.pseudo")
                    val name = document.get<String?>("information.name")
                    val avatar = document.get<String?>("information.avatarUrl")

                    if (uid == ownerUid) return@mapNotNull null

                    if (userPseudo != null && userPseudo.lowercase().contains(searchTerm)) {
                        SocialUserSummaryDomainModel(
                            uid = uid,
                            name = name,
                            pseudo = userPseudo,
                            avatarUrl = avatar
                        )
                    } else null
                }
                .take(10)

            results.map { member ->
                SocialMemberDomainModel(
                    uid = member.uid,
                    name = member.name,
                    pseudo = member.pseudo,
                    avatarUrl = member.avatarUrl ?: DEFAULT_AVATAR_URL
                )
            }
        }

    @OptIn(ExperimentalTime::class)
    override suspend fun sendInvite(invite: Invite): AppResult<Unit, AppError> =
        safeFirebaseCall {
            firestore.collection("socialInvites").document.set(
                mapOf(
                    "fromUid" to invite.fromUid,
                    "toPseudo" to invite.toPseudo,
                    "name" to invite.name,
                    "status" to invite.status,
                    "createdAt" to invite.createdAt
                ),
                merge = true
            )
        }


    override fun observeIncomingInvites(ownerUid: String): Flow<List<Invite>> {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalTime::class)
    override fun observeSentInvited(ownerUid: String): Flow<List<Invite>> =
        firestore
            .collection("socialInvites")
            .where { "fromUid" equalTo ownerUid }
            .snapshots
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    val fromUid = document.get<String?>("fromUid") ?: return@mapNotNull null
                    val toPseudo = document.get<String?>("toPseudo") ?: return@mapNotNull null
                    val name = document.get<String?>("name") ?: return@mapNotNull null
                    val status = document.get<InviteStatus?>("status") ?: InviteStatus.PENDING
                    val createdAtStr = document.get<String?>("createdAt") ?: return@mapNotNull null
                    val updatedAtStr = document.get<String?>("updatedAt")

                    Invite(
                        inviteId = document.id,
                        fromUid = fromUid,
                        toPseudo = toPseudo,
                        name = name,
                        status = status,
                        createdAt = Instant.parse(createdAtStr),
                        updatedAt = updatedAtStr?.let { Instant.parse(it) }
                    )
                }
            }

    override suspend fun markInviteAsAccepted(inviteId: String): AppResult<Unit, AppError> {
        TODO("Not yet implemented")
    }

    override suspend fun markInviteAsDeclined(inviteId: String): AppResult<Unit, AppError> {
        TODO("Not yet implemented")
    }

    override suspend fun cancelInvitation(inviteId: String): AppResult<Unit, AppError> =
        safeFirebaseCall {
            firestore
                .collection("socialInvites")
                .document(inviteId)
                .delete()
        }

    override suspend fun addToSocialCircle(
        ownerUid: String,
        memberUid: String
    ): AppResult<Unit, AppError> {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromSocialCircle(
        ownerUid: String,
        memberUid: String
    ): AppResult<Unit, AppError> {
        TODO("Not yet implemented")
    }
}
