package com.msoula.hobbymatchmaker.core.session.data.dataSources.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.safeCall
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.mappers.toUserFireStoreModel
import com.msoula.hobbymatchmaker.core.session.domain.dataSources.SessionRemoteDataSource
import com.msoula.hobbymatchmaker.core.session.domain.errors.SessionErrors
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import kotlinx.coroutines.tasks.await

class SessionRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : SessionRemoteDataSource {

    override suspend fun createUser(user: SessionUserDomainModel): Result<Boolean, SessionErrors.CreateUserError> {
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
    ): Result<Boolean, SessionErrors.CreateUserError> {
        return safeCall(appError = { errorMessage ->
            Log.e("HMM", "Error while saving firestore user online")
            SessionErrors.CreateUserError(errorMessage)
        }) {
            firestore.collection("users").document(uid).set(userFireStoreModel).await()
            true
        }
    }
}
