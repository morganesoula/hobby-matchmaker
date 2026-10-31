package com.msoula.hobbymatchmaker.core.session.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.data.FirestoreUsersCollection
import com.msoula.hobbymatchmaker.core.common.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.core.common.safeNetworkCall
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.models.UserFireStoreModel
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore

class SessionRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val connectivityChecker: NetworkConnectivityChecker
) : SessionRemoteDataSource {

    override suspend fun createUser(user: UserFireStoreModel): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            val document = firestore
                .collection(FirestoreUsersCollection)
                .document(user.uid)
            val snapshot = document.get()

            if (!snapshot.exists) {
                document.set(mapOf("uid" to user.uid))
            } else {
                Logger.i("User already exists - nothing to do here")
            }
        }

    override suspend fun removeLegacyEmail(uid: String): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            val document = firestore.collection(FirestoreUsersCollection).document(uid)
            val snapshot = document.get()

            // Older versions stored the sign-in email on the user document: no screen reads it
            if (snapshot.exists && snapshot.contains("email")) {
                document.update("email" to FieldValue.delete)
            }
        }
}
