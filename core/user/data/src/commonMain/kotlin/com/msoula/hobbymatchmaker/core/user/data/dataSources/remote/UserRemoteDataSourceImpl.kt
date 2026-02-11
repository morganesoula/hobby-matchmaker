package com.msoula.hobbymatchmaker.core.user.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.user.domain.models.UserSummaryDomainModel
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : UserRemoteDataSource {

    override fun observeUser(uid: String): Flow<UserSummaryDomainModel?> =
        firestore
            .collection("users")
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

    override suspend fun getUser(uid: String): AppResult<UserSummaryDomainModel?, AppError> {
        val doc = firestore
            .collection("users")
            .document(uid)
            .get()

        if (!doc.exists) return AppResult.Failure(AppError.Domain.NotFound)

        return AppResult.Success(
            UserSummaryDomainModel(
                uid = uid,
                pseudo = doc.get<String?>("information.pseudo")
                    ?: UserSummaryDomainModel.Initial.pseudo,
                name = doc.get<String?>("information.name"),
                avatarUrl = doc.get<String?>("information.avatarUrl"),
                moviesLiked = doc.get<List<Long>?>("movies") ?: emptyList()
            )
        )
    }

    override suspend fun getUsers(uids: List<String>): Map<String, UserSummaryDomainModel> {
        if (uids.isEmpty()) return emptyMap()

        val documents = firestore
            .collection("users")
            .where { "uid" inArray uids }
            .get()
            .documents

        return documents.mapNotNull { doc ->
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
