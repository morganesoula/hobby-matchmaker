package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.MAX_SOCIAL_CIRCLE_SIZE
import com.msoula.hobbymatchmaker.core.common.data.FirestoreBlockedCollection
import com.msoula.hobbymatchmaker.core.common.data.FirestoreCircleCollection
import com.msoula.hobbymatchmaker.core.common.data.FirestoreReportsCollection
import com.msoula.hobbymatchmaker.core.common.data.FirestoreSocialInvitesCollection
import com.msoula.hobbymatchmaker.core.common.data.FirestoreUsersCollection
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.common.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.core.common.safeNetworkCall
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toInviteStatusData
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.BlockedUserRemoteDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.InviteDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.InviteStatusData
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleEntryRemoteDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleMemberRemoteDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.UserReportRemoteDataModel
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.DocumentReference
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val INVITE_COOLDOWN_MILLIS = 30L * 24 * 60 * 60 * 1000

class SocialRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val connectivityChecker: NetworkConnectivityChecker
) : SocialRemoteDataSource {
    override suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerUid: String?
    ): AppResult<List<SocialCircleMemberRemoteDataModel>, AppError> =
        safeNetworkCall(connectivityChecker) {
            val searchTerm = pseudo.trim().lowercase()
            if (searchTerm.isEmpty()) return@safeNetworkCall emptyList()

            val endTerm = searchTerm + '\uf8ff'

            val documents = try {
                val startAtFieldValues = firestore
                    .collection(FirestoreUsersCollection)
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

            // People already in the circle or blocked by the user are not suggested
            val excludedUids = ownerUid?.let { uid ->
                val ownerRef = firestore.collection(FirestoreUsersCollection).document(uid)
                val circleUids = ownerRef.collection(FirestoreCircleCollection).get().documents.map { it.id }
                circleUids.toSet() + blockedUidsOnce(uid)
            }.orEmpty()

            val results = documents
                .mapNotNull { document ->
                    val uid = document.id
                    val userPseudo = document.get<String?>("information.pseudo")
                    val name = document.get<String?>("information.name")
                    val avatar = document.get<String?>("information.avatarUrl")
                    val moviesLiked = document.get<List<Long>?>("movies")

                    if (uid == ownerUid || uid in excludedUids) return@mapNotNull null

                    if (userPseudo != null && userPseudo.lowercase().contains(searchTerm)) {
                        SocialCircleMemberRemoteDataModel(
                            uid = uid,
                            name = name,
                            pseudo = userPseudo,
                            avatarUrl = avatar
                                ?: SocialCircleMemberRemoteDataModel.Initial.avatarUrl,
                            moviesLiked = moviesLiked,
                            commonMoviesCount = SocialCircleMemberRemoteDataModel.Initial.commonMoviesCount
                        )
                    } else null
                }
                .take(10)

            results
        }

    override fun observeSocialCircle(uid: String): Flow<List<SocialCircleEntryRemoteDataModel>> =
        firestore
            .collection(FirestoreUsersCollection)
            .document(uid)
            .collection(FirestoreCircleCollection)
            .snapshots
            .map { querySnapshot ->
                querySnapshot.documents.map { document ->
                    SocialCircleEntryRemoteDataModel(
                        memberUid = document.id,
                        commonMoviesCount = document.get<Int?>("commonMoviesCount") ?: 0,
                        avatarUrl = document.get<String?>("avatarUrl") ?: "",
                        memberPseudo = document.get<String?>("memberPseudo"),
                        memberName = document.get<String?>("username"),
                        moviesLiked = document.get<List<Long>?>("moviesLiked") ?: emptyList(),
                        lastMatchAt = document.get<Long?>("lastMatchAt")
                    )
                }
            }

    @OptIn(ExperimentalTime::class)
    override suspend fun sendInvite(inviteDataModel: InviteDataModel): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            val inviteId = "${inviteDataModel.fromUid}_${inviteDataModel.toPseudo}"
            val inviteRef = firestore.collection(FirestoreSocialInvitesCollection).document(inviteId)
            val existing = inviteRef.get()
            val blockedBy = if (existing.exists) existing.sendBlockingError() else null

            if (blockedBy == null) {
                inviteRef.set(
                    buildMap {
                        put("fromUid", inviteDataModel.fromUid)
                        put("toPseudo", inviteDataModel.toPseudo)
                        put("fromPseudo", inviteDataModel.fromPseudo)
                        put("name", inviteDataModel.name)
                        put("status", inviteDataModel.status)
                        put("createdAt", inviteDataModel.createdAt)
                        // Re-inviting after the cooldown starts a fresh invitation
                        if (existing.exists) put("declinedAt", FieldValue.delete)
                    },
                    merge = true
                )
            }
            blockedBy
        }.flatMap { blockedBy ->
            if (blockedBy == null) AppResult.Success(Unit) else AppResult.Failure(blockedBy)
        }

    // An invitation can only be sent again once a decline is older than the cooldown
    @OptIn(ExperimentalTime::class)
    private fun DocumentSnapshot.sendBlockingError(): AppError? =
        when (get<String?>("status")?.toInviteStatusData()) {
            InviteStatusData.PENDING -> AppError.Social.InviteAlreadyPending
            InviteStatusData.ACCEPTED -> AppError.Social.InviteAlreadyAccepted
            InviteStatusData.DECLINED -> {
                val declinedAt = get<Long?>("declinedAt")
                val nowMillis = Clock.System.now().toEpochMilliseconds()
                if (declinedAt != null && nowMillis < declinedAt + INVITE_COOLDOWN_MILLIS) {
                    AppError.Social.InviteCooldown
                } else null
            }

            null -> null
        }

    @OptIn(ExperimentalTime::class)
    override suspend fun refreshIncomingInvites(ownerUid: String): AppResult<List<InviteDataModel>, AppError> =
        safeNetworkCall(connectivityChecker) {
            val userSnapshot = firestore
                .collection(FirestoreUsersCollection)
                .document(ownerUid)
                .get()

            val pseudo = userSnapshot.get<String?>("information.pseudo")
                ?: return@safeNetworkCall emptyList()

            val documents = firestore
                .collection(FirestoreSocialInvitesCollection)
                .where { "toPseudo" equalTo pseudo }
                .where { "status" equalTo InviteStatusData.PENDING }
                .get()
                .documents

            val blockedUids = blockedUidsOnce(ownerUid)

            documents.mapNotNull { document ->
                val fromUid = document.get<String?>("fromUid") ?: return@mapNotNull null
                if (fromUid in blockedUids) return@mapNotNull null
                val fromPseudo = document.get<String?>("fromPseudo") ?: return@mapNotNull null
                val toPseudo = document.get<String?>("toPseudo") ?: return@mapNotNull null
                val name = document.get<String?>("name") ?: return@mapNotNull null
                val status = document.get<String?>("status") ?: return@mapNotNull null
                val createdAtStr = document.get<String?>("createdAt") ?: return@mapNotNull null
                val updatedAtStr = document.get<String?>("updatedAt")

                InviteDataModel(
                    inviteId = document.id,
                    fromUid = fromUid,
                    fromPseudo = fromPseudo,
                    toPseudo = toPseudo,
                    name = name,
                    status = status.toInviteStatusData(),
                    createdAt = Instant.parse(createdAtStr),
                    updatedAt = updatedAtStr?.let { Instant.parse(it) }
                )
            }
        }

    @OptIn(ExperimentalTime::class)
    override suspend fun refreshSentInvites(ownerUid: String): AppResult<List<InviteDataModel>, AppError> =
        safeNetworkCall(connectivityChecker) {
            val documents = firestore
                .collection(FirestoreSocialInvitesCollection)
                .where { "fromUid" equalTo ownerUid }
                .get()
                .documents

            documents.mapNotNull { document ->
                val fromUid = document.get<String?>("fromUid") ?: return@mapNotNull null
                val fromPseudo = document.get<String?>("fromPseudo") ?: return@mapNotNull null
                val toPseudo = document.get<String?>("toPseudo") ?: return@mapNotNull null
                val name = document.get<String?>("name") ?: return@mapNotNull null
                val status = document.get<String?>("status") ?: return@mapNotNull null
                val createdAtStr = document.get<String?>("createdAt") ?: return@mapNotNull null
                val updatedAtStr = document.get<String?>("updatedAt")

                InviteDataModel(
                    inviteId = document.id,
                    fromUid = fromUid,
                    fromPseudo = fromPseudo,
                    toPseudo = toPseudo,
                    name = name,
                    status = status.toInviteStatusData(),
                    createdAt = Instant.parse(createdAtStr),
                    updatedAt = updatedAtStr?.let { Instant.parse(it) }
                )
            }
        }

    override fun observeIncomingInvites(ownerUid: String): Flow<List<InviteDataModel>> =
        combine(observeIncomingInviteDocuments(ownerUid), observeBlockedUids(ownerUid)) { invites, blockedUids ->
            invites.filterNot { it.fromUid in blockedUids }
        }

    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
    private fun observeIncomingInviteDocuments(ownerUid: String): Flow<List<InviteDataModel>> =
        firestore
            .collection(FirestoreUsersCollection)
            .document(ownerUid)
            .snapshots
            .flatMapLatest { userSnapshot ->
                val pseudo = userSnapshot.get<String?>("information.pseudo")

                if (pseudo == null) {
                    Logger.d("SocialRemoteDataSource: User pseudo not found for uid: $ownerUid")
                    return@flatMapLatest flowOf(emptyList())
                }

                firestore
                    .collection(FirestoreSocialInvitesCollection)
                    .where { "toPseudo" equalTo pseudo }
                    .where { "status" equalTo InviteStatusData.PENDING }
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
                                document.get<String?>("status") ?: return@mapNotNull null
                            val createdAtStr =
                                document.get<String?>("createdAt") ?: return@mapNotNull null
                            val updatedAtStr = document.get<String?>("updatedAt")

                            InviteDataModel(
                                inviteId = document.id,
                                fromUid = fromUid,
                                fromPseudo = fromPseudo,
                                toPseudo = toPseudo,
                                name = name,
                                status = status.toInviteStatusData(),
                                createdAt = Instant.parse(createdAtStr),
                                updatedAt = updatedAtStr?.let { Instant.parse(it) }
                            )
                        }
                    }
            }

    @OptIn(ExperimentalTime::class)
    override fun observeSentInvites(ownerUid: String): Flow<List<InviteDataModel>> =
        firestore
            .collection(FirestoreSocialInvitesCollection)
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
                        document.get<String?>("status") ?: return@mapNotNull null
                    val createdAtStr =
                        document.get<String?>("createdAt") ?: return@mapNotNull null
                    val updatedAtStr = document.get<String?>("updatedAt")

                    InviteDataModel(
                        inviteId = document.id,
                        fromUid = fromUid,
                        fromPseudo = fromPseudo,
                        toPseudo = toPseudo,
                        name = name,
                        status = status.toInviteStatusData(),
                        createdAt = Instant.parse(createdAtStr),
                        updatedAt = updatedAtStr?.let { Instant.parse(it) }
                    )
                }
            }

    override suspend fun markInviteAsAccepted(inviteId: String): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            val inviteRef = firestore
                .collection(FirestoreSocialInvitesCollection)
                .document(inviteId)

            firestore.runTransaction {
                updateFields(inviteRef) { "status" to InviteStatusData.ACCEPTED }
            }
        }

    override suspend fun markInviteAsDeclined(inviteId: String): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            firestore
                .collection(FirestoreSocialInvitesCollection)
                .document(inviteId)
                .update(
                    "status" to InviteStatusData.DECLINED,
                    "declinedAt" to Clock.System.now().toEpochMilliseconds()
                )
        }

    override suspend fun cancelInvitation(inviteId: String): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            firestore
                .collection(FirestoreSocialInvitesCollection)
                .document(inviteId)
                .delete()
        }

    override suspend fun addToSocialCircle(socialCircleMemberDataModel: SocialCircleMemberRemoteDataModel): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            firestore
                .collection(FirestoreUsersCollection)
                .document(socialCircleMemberDataModel.ownerUid)
                .collection(FirestoreCircleCollection)
                .document(socialCircleMemberDataModel.uid)
                .set(
                    mapOf(
                        "addedAt" to FieldValue.serverTimestamp,
                        "commonMoviesCount" to socialCircleMemberDataModel.commonMoviesCount
                    ),
                    merge = true
                )
        }

    override suspend fun removeFromSocialCircle(
        ownerUid: String,
        memberUid: String
    ): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            val relationship = findRelationship(ownerUid, memberUid)

            firestore.batch().apply {
                relationship.refs.forEach { delete(it) }
            }.commit()
        }

    override suspend fun blockUser(ownerUid: String, blockedUid: String): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            val relationship = findRelationship(ownerUid, blockedUid)

            firestore.batch().apply {
                relationship.refs.forEach { delete(it) }
                set(
                    blockedCollection(ownerUid).document(blockedUid),
                    mapOf(
                        "pseudo" to relationship.memberPseudo,
                        "blockedAt" to Clock.System.now().toEpochMilliseconds()
                    )
                )
            }.commit()
        }

    override suspend fun unblockUser(ownerUid: String, blockedUid: String): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            blockedCollection(ownerUid).document(blockedUid).delete()
        }

    override fun observeBlockedUsers(ownerUid: String): Flow<List<BlockedUserRemoteDataModel>> =
        blockedCollection(ownerUid).snapshots.map { snapshot ->
            snapshot.documents.map { document ->
                BlockedUserRemoteDataModel(
                    uid = document.id,
                    pseudo = document.get<String?>("pseudo"),
                    blockedAt = document.get<Long?>("blockedAt")
                )
            }
        }

    override suspend fun reportUser(report: UserReportRemoteDataModel): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            firestore.collection(FirestoreReportsCollection).add(
                buildMap {
                    put("reporterUid", report.reporterUid)
                    put("reportedUid", report.reportedUid)
                    put("reason", report.reason)
                    report.note?.let { put("note", it) }
                    put("createdAt", FieldValue.serverTimestamp)
                }
            )
            Unit
        }

    private fun blockedCollection(ownerUid: String) =
        firestore.collection(FirestoreUsersCollection).document(ownerUid).collection(FirestoreBlockedCollection)

    private suspend fun blockedUidsOnce(ownerUid: String): Set<String> =
        blockedCollection(ownerUid).get().documents.map { it.id }.toSet()

    private fun observeBlockedUids(ownerUid: String): Flow<Set<String>> =
        blockedCollection(ownerUid).snapshots.map { snapshot -> snapshot.documents.map { it.id }.toSet() }

    private suspend fun pseudoOf(uid: String): String? =
        firestore.collection(FirestoreUsersCollection).document(uid).get().get<String?>("information.pseudo")

    /**
     * Everything that ties two users together: both circle entries and the invites sent in either
     * direction, so that either of them can invite the other again later.
     */
    private suspend fun findRelationship(ownerUid: String, memberUid: String): Relationship {
        val usersCollection = firestore.collection(FirestoreUsersCollection)
        val invitesCollection = firestore.collection(FirestoreSocialInvitesCollection)
        val ownerPseudo = pseudoOf(ownerUid)
        val memberPseudo = pseudoOf(memberUid)

        suspend fun invitesBetween(fromUid: String, toPseudo: String?): List<DocumentReference> =
            toPseudo?.let {
                invitesCollection
                    .where { "fromUid" equalTo fromUid }
                    .where { "toPseudo" equalTo it }
                    .get()
                    .documents
                    .map { document -> document.reference }
            }.orEmpty()

        val circleEntries = listOf(
            usersCollection.document(ownerUid).collection(FirestoreCircleCollection).document(memberUid),
            usersCollection.document(memberUid).collection(FirestoreCircleCollection).document(ownerUid)
        )

        return Relationship(
            refs = circleEntries + invitesBetween(ownerUid, memberPseudo) + invitesBetween(memberUid, ownerPseudo),
            memberPseudo = memberPseudo
        )
    }

    private class Relationship(val refs: List<DocumentReference>, val memberPseudo: String?)

    override suspend fun acceptInviteAndAddMembers(
        inviteId: String,
        memberAddedToOwnerCircle: SocialCircleMemberRemoteDataModel,
        ownerAddedToMemberCircle: SocialCircleMemberRemoteDataModel
    ): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            firestore.runTransaction {
                val inviteRef = firestore
                    .collection(FirestoreSocialInvitesCollection)
                    .document(inviteId)

                val ownerCircleRef = firestore
                    .collection(FirestoreUsersCollection)
                    .document(memberAddedToOwnerCircle.ownerUid)
                    .collection(FirestoreCircleCollection)
                    .document(memberAddedToOwnerCircle.uid)

                val memberCircleRef = firestore
                    .collection(FirestoreUsersCollection)
                    .document(ownerAddedToMemberCircle.ownerUid)
                    .collection(FirestoreCircleCollection)
                    .document(ownerAddedToMemberCircle.uid)

                updateFields(inviteRef) { "status" to InviteStatusData.ACCEPTED }

                set(
                    ownerCircleRef,
                    mapOf(
                        "addedAt" to FieldValue.serverTimestamp,
                        "memberPseudo" to memberAddedToOwnerCircle.pseudo,
                        "username" to memberAddedToOwnerCircle.name,
                        "avatarUrl" to memberAddedToOwnerCircle.avatarUrl,
                        "commonMoviesCount" to memberAddedToOwnerCircle.commonMoviesCount,
                        "moviesLiked" to memberAddedToOwnerCircle.moviesLiked
                    ),
                    merge = true
                )

                set(
                    memberCircleRef,
                    mapOf(
                        "addedAt" to FieldValue.serverTimestamp,
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
        safeNetworkCall(connectivityChecker) {
            val ownerLibrary = firestore
                .collection(FirestoreUsersCollection)
                .document(ownerUid)
            val ownerDocument = ownerLibrary.get()

            if (!ownerDocument.exists) {
                throw IllegalArgumentException("User not found with uid: $ownerUid")
            }

            val memberLibrary = firestore
                .collection(FirestoreUsersCollection)
                .document(invitingMemberUid)
            val memberDocument = memberLibrary.get()

            if (!memberDocument.exists) {
                throw IllegalArgumentException("User not found with uid: $invitingMemberUid")
            }

            val ownerCircle = ownerLibrary
                .collection(FirestoreCircleCollection)
                .get()
                .documents
            val memberCircle = memberLibrary
                .collection(FirestoreCircleCollection)
                .get()
                .documents
            ownerCircle.size < MAX_SOCIAL_CIRCLE_SIZE && memberCircle.size < MAX_SOCIAL_CIRCLE_SIZE
        }

    override suspend fun getSocialCircleSnapshot(uid: String): AppResult<List<SocialCircleEntryRemoteDataModel>, AppError> =
        AppResult.Success(observeSocialCircle(uid).first())

    @OptIn(ExperimentalTime::class)
    override suspend fun updateMovieLikedInCircleEntries(
        ownerUid: String,
        movieId: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError> = safeNetworkCall(connectivityChecker) {
        getSocialCircleSnapshot(ownerUid).mapSuccess { list ->
            val now = Clock.System.now().toEpochMilliseconds()

            list.forEach { entry ->
                // Liking a movie the member already likes creates a match on both sides
                val isNewMatch = isFavorite && entry.moviesLiked?.contains(movieId) == true
                val ownerEntryInMemberCircle = firestore
                    .collection(FirestoreUsersCollection)
                    .document(entry.memberUid)
                    .collection(FirestoreCircleCollection)
                    .document(ownerUid)

                ownerEntryInMemberCircle.set(
                    buildMap {
                        put(
                            "moviesLiked",
                            if (isFavorite) FieldValue.arrayUnion(movieId) else FieldValue.arrayRemove(movieId)
                        )
                        if (isNewMatch) put("lastMatchAt", now)
                    }, merge = true
                )

                if (isNewMatch) {
                    firestore
                        .collection(FirestoreUsersCollection)
                        .document(ownerUid)
                        .collection(FirestoreCircleCollection)
                        .document(entry.memberUid)
                        .set(mapOf("lastMatchAt" to now), merge = true)
                }
            }
        }
    }
}
