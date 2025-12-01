package com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.FirebaseFirestore

class SocialRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : SocialRemoteDataSource {
    override suspend fun searchUsersByPseudo(
        pseudo: String,
        currentUserUid: String?
    ): AppResult<List<String>, AppError> =
        safeFirebaseCall {
            val searchTerm = pseudo.trim().lowercase()

            if (searchTerm.isEmpty()) {
                return@safeFirebaseCall emptyList()
            }

            // Firestore doesn't support "contains" natively
            val endTerm = searchTerm + '\uf8ff'

            val results = firestore.collection("users")
                .orderBy("information.pseudo", Direction.ASCENDING)
                .startAt(searchTerm)
                .endAt(endTerm)
                .limit(20)
                .get()
                .documents
                .mapNotNull { document ->
                    val uid = document.id
                    val userPseudo = document.get<String>("information.pseudo")

                    if (userPseudo != null) {
                        uid to userPseudo
                    } else null
                }
                .filter { (uid, userPseudo) ->
                    uid != currentUserUid &&
                    userPseudo.lowercase().contains(searchTerm)
                }
                .map { (_, userPseudo) -> userPseudo }
                .take(10)

            results
        }
}
