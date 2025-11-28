package com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import dev.gitlive.firebase.firestore.FirebaseFirestore

class SocialRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : SocialRemoteDataSource {
    override suspend fun searchUsersByPseudo(pseudo: String): AppResult<List<String>, AppError> =
        safeFirebaseCall {
            val searchTerm = pseudo.trim().lowercase()

            firestore.collection("users")
                .get()
                .documents
                .map { document ->
                    document.get<String>("information.pseudo")
                }
                .filter { userPseudo ->
                    userPseudo.lowercase().contains(searchTerm)
                }
                .take(10)
        }
}
