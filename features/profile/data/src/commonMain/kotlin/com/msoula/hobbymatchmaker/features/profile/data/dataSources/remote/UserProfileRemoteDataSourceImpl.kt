package com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileRemoteDataModel
import dev.gitlive.firebase.firestore.FirebaseFirestore

class UserProfileRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
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

        return safeFirebaseCall {
            firestore.collection("users").document(userProfileRemoteDataModel.uid)
                .set(mapOf("information" to user), merge = true)
        }
    }

    override suspend fun checkIfPseudoIsAvailable(userPseudo: String): AppResult<Boolean, AppError> =
        safeFirebaseCall {
            val existingUser = firestore.collection("users")
                .where { "information.pseudo" equalTo userPseudo }
                .limit(1)
                .get()

            existingUser.documents.isEmpty()
        }
}
