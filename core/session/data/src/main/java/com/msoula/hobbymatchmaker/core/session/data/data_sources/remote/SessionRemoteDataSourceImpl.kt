package com.msoula.hobbymatchmaker.core.session.data.data_sources.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.safeFirebaseCall
import com.msoula.hobbymatchmaker.core.session.data.data_sources.remote.errors.SaveFirestoreUserError
import com.msoula.hobbymatchmaker.core.session.data.data_sources.remote.mappers.toUserFireStoreModel
import com.msoula.hobbymatchmaker.core.session.domain.data_sources.SessionRemoteDataSource
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import kotlinx.coroutines.tasks.await

class SessionRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : SessionRemoteDataSource {

    override suspend fun createUser(user: SessionUserDomainModel): Result<Boolean> {
        val document = firestore.collection("users").document(user.uid).get().await()

        if (document.exists()) {
            Log.i("HMM", "User already exists - nothing to do here")
            return Result.Success(true)
        } else {
            val firestoreUser = user.toUserFireStoreModel()

            val firestoreUserMap = hashMapOf(
                "uid" to firestoreUser.uid,
                "email" to firestoreUser.email
            )

            return saveFireStoreUser(firestoreUser.uid, firestoreUserMap)
        }
    }

    private suspend fun saveFireStoreUser(
        uid: String,
        userFireStoreModel: HashMap<String, String>
    ): Result<Boolean> {
        return safeFirebaseCall(appError = { errorMessage ->
            Log.e("HMM", "Error while saving firestore user online")
            SaveFirestoreUserError(errorMessage)
        }) {
            firestore.collection("users").document(uid).set(userFireStoreModel).await()
            true
        }
    }
}
