package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
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
import kotlin.collections.emptyList
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SocialRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : SocialRemoteDataSource {

    private val MAX_CIRCLE_SIZE = 5

    override suspend fun searchUsersByPseudo(
        pseudo: String,
        ownerUid: String?
    ): AppResult<List<SocialCircleMemberRemoteDataModel>, AppError> =
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
            .collection("users")
            .document(uid)
            .collection("circle")
            .snapshots
            .map { querySnapshot ->
                querySnapshot.documents.map { document ->
                    SocialCircleEntryRemoteDataModel(
                        memberUid = document.id,
                        commonMoviesCount = document.get<Int?>("commonMoviesCount") ?: 0
                    )
                }
            }

    @OptIn(ExperimentalTime::class)
    override suspend fun sendInvite(inviteDataModel: InviteDataModel): AppResult<Unit, AppError> =
        safeFirebaseCall {
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
        safeFirebaseCall {
            val userSnapshot = firestore
                .collection("users")
                .document(ownerUid)
                .get()

            val pseudo = userSnapshot.get<String?>("information.pseudo")
                ?: return@safeFirebaseCall emptyList()

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
        safeFirebaseCall {
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
        safeFirebaseCall {
            val inviteRef = firestore
                .collection("socialInvites")
                .document(inviteId)

            firestore.runTransaction {
                updateFields(inviteRef) { "status" to InviteStatusData.ACCEPTED }
            }
        }

    override suspend fun markInviteAsDeclined(inviteId: String): AppResult<Unit, AppError> =
        safeFirebaseCall {
            firestore
                .collection("socialInvites")
                .document(inviteId)
                .update("status" to InviteStatusData.DECLINED)
        }

    override suspend fun cancelInvitation(inviteId: String): AppResult<Unit, AppError> =
        safeFirebaseCall {
            firestore
                .collection("socialInvites")
                .document(inviteId)
                .delete()
        }

    override suspend fun addToSocialCircle(socialCircleMemberDataModel: SocialCircleMemberRemoteDataModel): AppResult<Unit, AppError> =
        safeFirebaseCall {
            firestore
                .collection("users")
                .document(socialCircleMemberDataModel.ownerUid)
                .collection("circle")
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
        memberAddedToOwnerCircle: SocialCircleMemberRemoteDataModel,
        ownerAddedToMemberCircle: SocialCircleMemberRemoteDataModel
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

                updateFields(inviteRef) { "status" to InviteStatusData.ACCEPTED }

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
            val ownerLibrary = firestore
                .collection("users")
                .document(ownerUid)
            val ownerDocument = ownerLibrary.get()

            if (!ownerDocument.exists) {
                throw Exception("User not found with uid: $ownerUid")
            }

            val memberLibrary = firestore
                .collection("users")
                .document(invitingMemberUid)
            val memberDocument = memberLibrary.get()

            if (!memberDocument.exists) {
                throw Exception("User not found with uid: $invitingMemberUid")
            }

            val ownerCircle = ownerLibrary.collection("circle").get().documents
            val memberCircle = memberLibrary.collection("circle").get().documents
            ownerCircle.size < MAX_CIRCLE_SIZE && memberCircle.size < MAX_CIRCLE_SIZE
        }

    override suspend fun getSocialCircleSnapshot(uid: String): AppResult<List<SocialCircleEntryRemoteDataModel>, AppError> =
        AppResult.Success(observeSocialCircle(uid).first())
}
