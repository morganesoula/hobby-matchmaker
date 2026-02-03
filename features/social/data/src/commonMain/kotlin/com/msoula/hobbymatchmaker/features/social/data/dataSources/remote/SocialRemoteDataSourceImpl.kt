package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import com.msoula.hobbymatchmaker.core.user.domain.repositories.UserDataRepository
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.Invite
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleMember
import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialMemberDomainModel
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialUserSummaryDomainModel
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.collections.emptyList
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SocialRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val userDataRepository: UserDataRepository
) : SocialRemoteDataSource {

    private val MAX_CIRCLE_SIZE = 5

    override suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerUid: String?
    ): AppResult<List<SocialMemberDomainModel>, AppError> =
        safeFirebaseCall {
            Logger.d("SocialRemoteDataSource - Pseudo is: $pseudo and ownerUid: $ownerUid")
            val searchTerm = pseudo.trim().lowercase()
            if (searchTerm.isEmpty()) return@safeFirebaseCall emptyList()

            Logger.d("SocialRemoteDataSource - Search term is: $searchTerm")

            val endTerm = searchTerm + '\uf8ff'

            val documents = try {
                val startAtFieldValues = firestore
                    .collection("users")
                    .orderBy("information.pseudoLowercase", Direction.ASCENDING)
                    .startAtFieldValues {
                        arrayOf<Any?>(searchTerm)
                            .forEach { this.add(it) }
                    }
                startAtFieldValues.endAtFieldValues {
                    arrayOf<Any?>(endTerm)
                        .forEach { this.add(it) }
                }
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
                    val moviesLiked = document.get<List<Long>?>("movies")

                    if (uid == ownerUid) return@mapNotNull null

                    if (userPseudo != null && userPseudo.lowercase().contains(searchTerm)) {
                        Logger.d("SocialRemoteDataSource: Found user with pseudo: $userPseudo")
                        SocialUserSummaryDomainModel(
                            uid = uid,
                            name = name,
                            pseudo = userPseudo,
                            avatarUrl = avatar,
                            moviesLiked = moviesLiked
                        )
                    } else null
                }
                .take(10)

            userDataRepository.prefetchUsers(results.map { it.uid })

            results.map { member ->
                SocialMemberDomainModel(
                    uid = member.uid,
                    name = member.name,
                    pseudo = member.pseudo,
                    avatarUrl = member.avatarUrl ?: SocialMemberDomainModel.Initial.avatarUrl,
                    commonMoviesCount = SocialMemberDomainModel.Initial.commonMoviesCount
                )
            }
        }

    override suspend fun findUserByUid(uid: String): AppResult<SocialCircleMember, AppError> =
        safeFirebaseCall {
            val user = userDataRepository.getUser(uid)
                ?: throw Exception("User not found with uid: $uid")

            SocialCircleMember(
                uid = user.uid,
                ownerUid = "",
                pseudo = user.pseudo,
                name = user.name,
                avatarUrl = user.avatarUrl,
                moviesLiked = user.moviesLiked,
                commonMoviesCount = SocialCircleMember.Initial.commonMoviesCount
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSocialCircle(uid: String): Flow<List<SocialMemberDomainModel>> =
        firestore
            .collection("users")
            .document(uid)
            .collection("circle")
            .snapshots
            .flatMapLatest { querySnapshot ->
                val memberData = querySnapshot.documents.map { document ->
                    document.id to (document.get<Int?>("commonMoviesCount") ?: 0)
                }

                if (memberData.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val memberUids = memberData.map { it.first }
                    val commonMoviesCounts = memberData.toMap()

                    userDataRepository.observeUsers(memberUids)
                        .map { usersMap ->
                            memberUids.mapNotNull { memberUid ->
                                usersMap[memberUid]?.let { user ->
                                    SocialMemberDomainModel(
                                        uid = user.uid,
                                        pseudo = user.pseudo,
                                        name = user.name,
                                        avatarUrl = user.avatarUrl
                                            ?: SocialMemberDomainModel.Initial.avatarUrl,
                                        moviesLiked = user.moviesLiked,
                                        commonMoviesCount = commonMoviesCounts[memberUid]
                                            ?: SocialMemberDomainModel.Initial.commonMoviesCount
                                    )
                                }
                            }
                        }
                }
            }

    @OptIn(ExperimentalTime::class)
    override suspend fun sendInvite(invite: Invite): AppResult<Unit, AppError> =
        safeFirebaseCall {
            val inviteId = "${invite.fromUid}_${invite.toPseudo}"
            firestore.collection("socialInvites").document(inviteId).set(
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
            val inviteRef = firestore
                .collection("socialInvites")
                .document(inviteId)

            firestore.runTransaction {
                updateFields(inviteRef) { "status" to InviteStatus.ACCEPTED }
            }
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
                        "addedAt" to FieldValue.serverTimestamp,
                        "commonMoviesCount" to socialCircleMember.commonMoviesCount
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

    override suspend fun acceptInviteAndAddMembers(
        inviteId: String,
        memberAddedToOwnerCircle: SocialCircleMember,
        ownerAddedToMemberCircle: SocialCircleMember
    ): AppResult<Unit, AppError> =
        safeFirebaseCall {
            firestore.runTransaction {
                val inviteRef = firestore
                    .collection("socialInvites")
                    .document(inviteId)

                val ownerCircleRef = firestore
                    .collection("users")
                    .document(memberAddedToOwnerCircle.ownerUid)
                    .collection("circle")
                    .document(memberAddedToOwnerCircle.uid)

                val memberCircleRef = firestore
                    .collection("users")
                    .document(ownerAddedToMemberCircle.ownerUid)
                    .collection("circle")
                    .document(ownerAddedToMemberCircle.uid)

                updateFields(inviteRef) { "status" to InviteStatus.ACCEPTED }

                set(
                    ownerCircleRef,
                    mapOf(
                        "addedAt" to FieldValue.serverTimestamp,
                        "commonMoviesCount" to memberAddedToOwnerCircle.commonMoviesCount,
                    ),
                    merge = true
                )

                set(
                    memberCircleRef,
                    mapOf(
                        "memberPseudo" to ownerAddedToMemberCircle.pseudo,
                        "username" to ownerAddedToMemberCircle.name,
                        "avatarUrl" to ownerAddedToMemberCircle.avatarUrl,
                        "commonMoviesCount" to ownerAddedToMemberCircle.commonMoviesCount,
                        "moviesLiked" to ownerAddedToMemberCircle.moviesLiked
                    ),
                    merge = true
                )
            }
        }

    override suspend fun checkSocialCircleLimit(
        ownerUid: String,
        invitingMemberUid: String
    ): AppResult<Boolean, AppError> =
        safeFirebaseCall {
            val ownerDocument = firestore
                .collection("users")
                .document(ownerUid)
                .get()

            if (!ownerDocument.exists) {
                throw Exception("User not found with uid: $ownerUid")
            }

            val memberDocument = firestore
                .collection("users")
                .document(invitingMemberUid)
                .get()

            if (!memberDocument.exists) {
                throw Exception("User not found with uid: $invitingMemberUid")
            }

            val circleCount = ownerDocument.get<Int?>("circle") ?: 0
            val memberCount = memberDocument.get<Int?>("circle") ?: 0

            circleCount < MAX_CIRCLE_SIZE && memberCount < MAX_CIRCLE_SIZE
        }

    override suspend fun getSocialCircleSnapshot(uid: String): AppResult<List<SocialMemberDomainModel>, AppError> =
        AppResult.Success(observeSocialCircle(uid).first())
}
