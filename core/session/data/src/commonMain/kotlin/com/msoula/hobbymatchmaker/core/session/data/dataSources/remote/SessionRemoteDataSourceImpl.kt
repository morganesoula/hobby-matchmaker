package com.msoula.hobbymatchmaker.core.session.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.safeCall
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.mappers.toUserFireStoreModel
import com.msoula.hobbymatchmaker.core.session.domain.dataSources.SessionRemoteDataSource
import com.msoula.hobbymatchmaker.core.session.domain.errors.SessionErrors
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.FirebaseFirestoreException

class SessionRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : SessionRemoteDataSource {

    override suspend fun createUser(user: SessionUserDomainModel): Result<Boolean, SessionErrors.CreateUserError> {
        val document = firestore.collection("users").document(user.uid).get()

        if (document.exists) {
            //Log.i("HMM", "User already exists - nothing to do here")
            println("User already exists - nothing to do here")
            return Result.Success(true)
        } else {
            val firestoreUser = user.toUserFireStoreModel()

            val firestoreUserMap = hashMapOf(
                "uid" to firestoreUser.uid,
                "email" to firestoreUser.email
            )

            return try {
                saveFireStoreUser(firestoreUser.uid, firestoreUserMap)
            } catch (e: FirebaseFirestoreException) {
                Result.Failure(SessionErrors.CreateUserError.SaveError(e.message ?: ""))
            }
        }
    }

    private suspend fun saveFireStoreUser(
        uid: String,
        userFireStoreModel: HashMap<String, String>
    ): Result<Boolean, SessionErrors.CreateUserError> {
        return safeCall(appError = { errorMessage ->
            //Log.e("HMM", "Error while saving firestore user online")
            println("Error while saving firestore user online")
            SessionErrors.CreateUserError.SaveError(errorMessage)
        }) {
            firestore.collection("users").document(uid).set(userFireStoreModel)
            true
        }
    }
}
