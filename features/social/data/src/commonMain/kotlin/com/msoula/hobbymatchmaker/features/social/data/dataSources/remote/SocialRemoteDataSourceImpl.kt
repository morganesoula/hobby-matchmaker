package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.data.FirestoreCircleCollection
import com.msoula.hobbymatchmaker.core.common.data.FirestoreUsersCollection
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.core.network.safeNetworkCall
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.mappers.toInviteStatusData
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.InviteDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.InviteStatusData
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleEntryRemoteDataModel
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models.SocialCircleMemberRemoteDataModel
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SocialRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val connectivityChecker: NetworkConnectivityChecker
) : SocialRemoteDataSource {
    private val MAX_CIRCLE_SIZE = 5

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

            val results = documents
                .mapNotNull { document ->
                    val uid = document.id
                    val userPseudo = document.get<String?>("information.pseudo")
                    val name = document.get<String?>("information.name")
                    val avatar = document.get<String?>("information.avatarUrl")
                    val moviesLiked = document.get<List<Long>?>("movies")

                    if (uid == ownerUid) return@mapNotNull null

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
                        memberPseudo = document.get<String>("memberPseudo"),
                        memberName = document.get<String?>("username") ?: "",
                        moviesLiked = document.get<List<Long>?>("moviesLiked") ?: emptyList()
                    )
                }
            }

    @OptIn(ExperimentalTime::class)
    override suspend fun sendInvite(inviteDataModel: InviteDataModel): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            val inviteId = "${inviteDataModel.fromUid}_${inviteDataModel.toPseudo}"
            firestore.collection("socialInvites").document(inviteId).set(
                mapOf(
                    "fromUid" to inviteDataModel.fromUid,
                    "toPseudo" to inviteDataModel.toPseudo,
                    "fromPseudo" to inviteDataModel.fromPseudo,
                    "name" to inviteDataModel.name,
                    "status" to inviteDataModel.status,
                    "createdAt" to inviteDataModel.createdAt
                ),
                merge = true
            )
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
                .collection("socialInvites")
                .where { "toPseudo" equalTo pseudo }
                .where { "status" equalTo InviteStatusData.PENDING }
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

    @OptIn(ExperimentalTime::class)
    override suspend fun refreshSentInvites(ownerUid: String): AppResult<List<InviteDataModel>, AppError> =
        safeNetworkCall(connectivityChecker) {
            val documents = firestore
                .collection("socialInvites")
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

    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
    override fun observeIncomingInvites(ownerUid: String): Flow<List<InviteDataModel>> =
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
                    .collection("socialInvites")
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
                .collection("socialInvites")
                .document(inviteId)

            firestore.runTransaction {
                updateFields(inviteRef) { "status" to InviteStatusData.ACCEPTED }
            }
        }

    override suspend fun markInviteAsDeclined(inviteId: String): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            firestore
                .collection("socialInvites")
                .document(inviteId)
                .update("status" to InviteStatusData.DECLINED)
        }

    override suspend fun cancelInvitation(inviteId: String): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            firestore
                .collection("socialInvites")
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
            firestore
                .collection(FirestoreUsersCollection)
                .document(ownerUid)
                .collection(FirestoreCircleCollection)
                .document(memberUid)
                .delete()
        }

    override suspend fun acceptInviteAndAddMembers(
        inviteId: String,
        memberAddedToOwnerCircle: SocialCircleMemberRemoteDataModel,
        ownerAddedToMemberCircle: SocialCircleMemberRemoteDataModel
    ): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            firestore.runTransaction {
                val inviteRef = firestore
                    .collection("socialInvites")
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
            ownerCircle.size < MAX_CIRCLE_SIZE && memberCircle.size < MAX_CIRCLE_SIZE
        }

    override suspend fun getSocialCircleSnapshot(uid: String): AppResult<List<SocialCircleEntryRemoteDataModel>, AppError> =
        AppResult.Success(observeSocialCircle(uid).first())

    override suspend fun updateMovieLikedInCircleEntries(
        ownerUid: String,
        movieId: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError> = safeNetworkCall(connectivityChecker) {
        getSocialCircleSnapshot(ownerUid).mapSuccess { list ->
            list.forEach { entry ->
                firestore
                    .collection(FirestoreUsersCollection)
                    .document(entry.memberUid)
                    .collection(FirestoreCircleCollection)
                    .document(ownerUid)
                    .set(
                        mapOf(
                            "moviesLiked" to if (isFavorite) FieldValue.arrayUnion(movieId) else FieldValue.arrayRemove(
                                movieId
                            )
                        ), merge = true
                    )
            }
        }
    }
}
