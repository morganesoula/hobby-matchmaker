package com.msoula.hobbymatchmaker.core.user.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.data.FirestoreUsersCollection
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.core.network.safeNetworkCall
import com.msoula.hobbymatchmaker.core.user.domain.models.UserSummaryDomainModel
import dev.gitlive.firebase.firestore.FieldPath
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val connectivityChecker: NetworkConnectivityChecker
) : UserRemoteDataSource {

    override fun observeUser(uid: String): Flow<UserSummaryDomainModel?> =
        firestore
            .collection(FirestoreUsersCollection)
            .document(uid)
            .snapshots
            .map { doc ->
                if (!doc.exists) return@map null

                UserSummaryDomainModel(
                    uid = uid,
                    pseudo = doc.get<String?>("information.pseudo") ?: return@map null,
                    name = doc.get<String?>("information.name"),
                    avatarUrl = doc.get<String?>("information.avatarUrl"),
                    moviesLiked = doc.get<List<Long>?>("movies") ?: emptyList()
                )
            }

    override suspend fun getUser(uid: String): AppResult<UserSummaryDomainModel?, AppError> =
        safeNetworkCall(connectivityChecker) {
            val doc = firestore
                .collection(FirestoreUsersCollection)
                .document(uid)
                .get()

            if (!doc.exists) null
            else UserSummaryDomainModel(
                uid = uid,
                pseudo = doc.get<String?>("information.pseudo")
                    ?: UserSummaryDomainModel.Initial.pseudo,
                name = doc.get<String?>("information.name"),
                avatarUrl = doc.get<String?>("information.avatarUrl"),
                moviesLiked = doc.get<List<Long>?>("movies") ?: emptyList()
            )
        }

    override suspend fun getUsers(uids: List<String>): AppResult<Map<String, UserSummaryDomainModel>, AppError> =
        safeNetworkCall(connectivityChecker) {
            if (uids.isEmpty()) emptyMap()
            else {
                val documents = firestore
                    .collection(FirestoreUsersCollection)
                    .where {
                        FieldPath.documentId inArray uids
                    }
                    .get()
                    .documents

                documents.mapNotNull { doc ->
                    val uid = doc.id
                    val pseudo = doc.get<String?>("information.pseudo") ?: return@mapNotNull null

                    uid to UserSummaryDomainModel(
                        uid = uid,
                        pseudo = pseudo,
                        name = doc.get<String?>("information.name"),
                        avatarUrl = doc.get<String?>("information.avatarUrl"),
                        moviesLiked = doc.get<List<Long>?>("movies") ?: emptyList()
                    )
                }.toMap()
            }
        }
}
