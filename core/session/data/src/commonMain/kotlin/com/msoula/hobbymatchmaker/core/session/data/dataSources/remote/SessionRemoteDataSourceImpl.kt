package com.msoula.hobbymatchmaker.core.session.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.data.FirestoreUsersCollection
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.core.network.safeNetworkCall
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.models.UserFireStoreModel
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
                val data = mapOf(
                    "uid" to user.uid,
                    "email" to user.email
                )
                document.set(data)
            } else {
                Logger.i("User already exists - nothing to do here")
            }
        }
}
