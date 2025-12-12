package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.Invite
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleMember
import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel.Companion.DEFAULT_AVATAR_URL
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel.Companion.DEFAULT_COMMON_MOVIES_COUNT
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialUserSummaryDomainModel
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

            //TODO Update DEFAULT_COMMON_MOVIES_COUNT?
            results.map { member ->
                SocialMemberDomainModel(
                    uid = member.uid,
                    name = member.name,
                    pseudo = member.pseudo,
                    avatarUrl = member.avatarUrl ?: DEFAULT_AVATAR_URL,
                    commonMoviesCount = DEFAULT_COMMON_MOVIES_COUNT
                )
            }
        }

    override suspend fun findUserByUid(uid: String): AppResult<SocialCircleMember, AppError> =
        safeFirebaseCall {
            val userDoc = firestore
                .collection("users")
                .document(uid)
                .get()

            if (!userDoc.exists) {
                throw Exception("User not found with uid: $uid")
            }

            val pseudo = userDoc.get<String>("information.pseudo")
            val name = userDoc.get<String?>("information.name")
            val avatarUrl = userDoc.get<String?>("information.avatarUrl")

            SocialCircleMember(
                ownerUid = "",
                uid = uid,
                pseudo = pseudo,
                name = name,
                avatarUrl = avatarUrl,
                commonMovieCount = DEFAULT_COMMON_MOVIES_COUNT
            )
        }

    override fun observeSocialCircle(uid: String): Flow<List<SocialMemberDomainModel>> =
        firestore
            .collection("users")
            .document(uid)
            .collection("circle")
            .snapshots
            .map { querySnapshot ->
                querySnapshot.documents.map { document ->
                    val memberUid = document.id
                    val memberPseudo = document.get<String>("memberPseudo")
                    val memberName = document.get<String?>("username")
                    val avatarUrl = document.get<String?>("avatarUrl")
                    val commonMoviesCount = document.get<Int?>("commonMoviesCount")

                    SocialMemberDomainModel(
                        uid = memberUid,
                        pseudo = memberPseudo,
                        name = memberName,
                        avatarUrl = avatarUrl ?: DEFAULT_AVATAR_URL,
                        commonMoviesCount = commonMoviesCount ?: DEFAULT_COMMON_MOVIES_COUNT
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
                    "fromPseudo" to invite.fromPseudo,
                    "name" to invite.name,
                    "status" to invite.status,
                    "createdAt" to invite.createdAt
                ),
                merge = true
            )
        }


    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
    override fun observeIncomingInvites(ownerUid: String): Flow<List<Invite>> =
        firestore
            .collection("users")
            .document(ownerUid)
            .snapshots
            .flatMapLatest { userSnapshot ->
                val pseudo = userSnapshot.get<String?>("information.pseudo")

                if (pseudo == null) {
                    Logger.d("SocialRemoteDataSource: User pseudo not found for uid: $ownerUid")
                    return@flatMapLatest flowOf(emptyList())
                }

                firestore
                    .collection("socialInvites")
                    .where { "toPseudo" equalTo pseudo }
                    .where { "status" equalTo InviteStatus.PENDING }
                    .snapshots
                    .map { querySnapshot ->
                        querySnapshot.documents.mapNotNull { document ->
                            val fromUid =
                                document.get<String?>("fromUid") ?: return@mapNotNull null
                            val fromPseudo =
                                document.get<String?>("fromPseudo") ?: return@mapNotNull null
                            val toPseudo =
                                document.get<String?>("toPseudo") ?: return@mapNotNull null
                            val name = document.get<String?>("name") ?: return@mapNotNull null
                            val status =
                                document.get<InviteStatus?>("status") ?: InviteStatus.PENDING
                            val createdAtStr =
                                document.get<String?>("createdAt") ?: return@mapNotNull null
                            val updatedAtStr = document.get<String?>("updatedAt")

                            Invite(
                                inviteId = document.id,
                                fromUid = fromUid,
                                fromPseudo = fromPseudo,
                                toPseudo = toPseudo,
                                name = name,
                                status = status,
                                createdAt = Instant.parse(createdAtStr),
                                updatedAt = updatedAtStr?.let { Instant.parse(it) }
                            )
                        }
                    }
            }

    @OptIn(ExperimentalTime::class)
    override fun observeSentInvited(ownerUid: String): Flow<List<Invite>> =
        firestore
            .collection("socialInvites")
            .where { "fromUid" equalTo ownerUid }
            .snapshots
            .map { querySnapshot ->
                querySnapshot.documents.mapNotNull { document ->
                    val fromUid =
                        document.get<String?>("fromUid") ?: return@mapNotNull null
                    val fromPseudo =
                        document.get<String?>("fromPseudo") ?: return@mapNotNull null
                    val toPseudo =
                        document.get<String?>("toPseudo") ?: return@mapNotNull null
                    val name = document.get<String?>("name") ?: return@mapNotNull null
                    val status =
                        document.get<InviteStatus?>("status") ?: InviteStatus.PENDING
                    val createdAtStr =
                        document.get<String?>("createdAt") ?: return@mapNotNull null
                    val updatedAtStr = document.get<String?>("updatedAt")

                    Invite(
                        inviteId = document.id,
                        fromUid = fromUid,
                        fromPseudo = fromPseudo,
                        toPseudo = toPseudo,
                        name = name,
                        status = status,
                        createdAt = Instant.parse(createdAtStr),
                        updatedAt = updatedAtStr?.let { Instant.parse(it) }
                    )
                }
            }

    override suspend fun markInviteAsAccepted(inviteId: String): AppResult<Unit, AppError> =
        safeFirebaseCall {
            firestore
                .collection("socialInvites")
                .document(inviteId)
                .update("status" to InviteStatus.ACCEPTED)
        }

    override suspend fun markInviteAsDeclined(inviteId: String): AppResult<Unit, AppError> =
        safeFirebaseCall {
            firestore
                .collection("socialInvites")
                .document(inviteId)
                .update("status" to InviteStatus.DECLINED)
        }

    override suspend fun cancelInvitation(inviteId: String): AppResult<Unit, AppError> =
        safeFirebaseCall {
            firestore
                .collection("socialInvites")
                .document(inviteId)
                .delete()
        }

    override suspend fun addToSocialCircle(socialCircleMember: SocialCircleMember): AppResult<Unit, AppError> =
        safeFirebaseCall {
            firestore
                .collection("users")
                .document(socialCircleMember.ownerUid)
                .collection("circle")
                .document(socialCircleMember.uid)
                .set(
                    mapOf(
                        "memberPseudo" to socialCircleMember.pseudo,
                        "username" to socialCircleMember.name,
                        "avatarUrl" to socialCircleMember.avatarUrl,
                        "commonMoviesCount" to socialCircleMember.commonMovieCount
                    ),
                    merge = true
                )
        }

    override suspend fun removeFromSocialCircle(
        ownerUid: String,
        memberUid: String
    ): AppResult<Unit, AppError> =
        safeFirebaseCall {
            firestore
                .collection("users")
                .document(ownerUid)
                .collection("circle")
                .document(memberUid)
                .delete()
        }
}
