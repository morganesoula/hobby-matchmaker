package com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.data.FirestoreUsersCollection
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.core.network.safeNetworkCall
import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileRemoteDataModel
import dev.gitlive.firebase.firestore.FirebaseFirestore

class UserProfileRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val connectivityChecker: NetworkConnectivityChecker
) : UserProfileRemoteDataSource {

    override suspend fun syncUserProfile(userProfileRemoteDataModel: UserProfileRemoteDataModel)
        : AppResult<Unit, AppError> {
        val user = hashMapOf(
            "name" to userProfileRemoteDataModel.name,
            "pseudo" to userProfileRemoteDataModel.pseudo,
            "pseudoLowercase" to userProfileRemoteDataModel.pseudo?.lowercase(),
            "avatarUrl" to userProfileRemoteDataModel.avatarUrl,
            "bio" to userProfileRemoteDataModel.bio,
            "interests" to userProfileRemoteDataModel.interests
        )

        return safeNetworkCall(connectivityChecker) {
            firestore
                .collection(FirestoreUsersCollection)
                .document(userProfileRemoteDataModel.uid)
                .set(mapOf("information" to user), merge = true)
        }
    }

    override suspend fun checkIfPseudoIsAvailable(userPseudo: String): AppResult<Boolean, AppError> =
        safeNetworkCall(connectivityChecker) {
            val existingUser = firestore.collection(FirestoreUsersCollection)
                .where { "information.pseudo" equalTo userPseudo }
                .limit(1)
                .get()

            existingUser.documents.isEmpty()
        }
}
