package com.msoula.hobbymatchmaker.core.session.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.models.UserFireStoreModel
import dev.gitlive.firebase.firestore.FirebaseFirestore

class SessionRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : SessionRemoteDataSource {

    override suspend fun createUser(user: UserFireStoreModel): R<Unit, AppError> =
        safeFirebaseCall {
            val document = firestore.collection("users").document(user.uid)
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
