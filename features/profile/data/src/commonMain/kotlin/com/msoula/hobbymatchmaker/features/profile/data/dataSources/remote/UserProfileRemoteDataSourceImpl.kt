package com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.data.FirestorePseudosCollection
import com.msoula.hobbymatchmaker.core.common.data.FirestoreUsersCollection
import com.msoula.hobbymatchmaker.core.common.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.core.common.flatMap
import com.msoula.hobbymatchmaker.core.common.safeNetworkCall
import com.msoula.hobbymatchmaker.features.profile.data.models.UserProfileRemoteDataModel
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore

class UserProfileRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val connectivityChecker: NetworkConnectivityChecker
) : UserProfileRemoteDataSource {

    // Pseudo fields are owned by reservePseudo and never overwritten here
    override suspend fun syncUserProfile(userProfileRemoteDataModel: UserProfileRemoteDataModel)
        : AppResult<Unit, AppError> {
        val user = hashMapOf(
            "name" to userProfileRemoteDataModel.name,
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

    override suspend fun fetchUserProfile(uid: String): AppResult<UserProfileRemoteDataModel?, AppError> =
        safeNetworkCall(connectivityChecker) {
            val doc = firestore
                .collection(FirestoreUsersCollection)
                .document(uid)
                .get()

            if (!doc.exists) null
            else UserProfileRemoteDataModel(
                uid = uid,
                name = doc.get<String?>("information.name"),
                pseudo = doc.get<String?>("information.pseudo"),
                avatarUrl = doc.get<String?>("information.avatarUrl"),
                bio = doc.get<String?>("information.bio"),
                interests = doc.get<List<String>?>("information.interests")
            )
        }

    override suspend fun checkIfPseudoIsAvailable(userPseudo: String): AppResult<Boolean, AppError> =
        safeNetworkCall(connectivityChecker) {
            val pseudoLowercase = userPseudo.lowercase()
            val isReserved = firestore
                .collection(FirestorePseudosCollection)
                .document(pseudoLowercase)
                .get()
                .exists

            // Pseudos saved before the reservation collection existed only live on users/{uid}
            isReserved.not() && firestore
                .collection(FirestoreUsersCollection)
                .where { "information.pseudoLowercase" equalTo pseudoLowercase }
                .limit(1)
                .get()
                .documents
                .isEmpty()
        }

    override suspend fun reservePseudo(uid: String, pseudo: String, name: String): AppResult<Unit, AppError> =
        checkIfPseudoIsAvailableForUser(uid, pseudo).flatMap { isAvailable ->
            if (!isAvailable) return@flatMap AppResult.Failure(AppError.Profile.PseudoTaken)

            safeNetworkCall(connectivityChecker) {
                val pseudoRef = firestore.collection(FirestorePseudosCollection).document(pseudo.lowercase())
                val userRef = firestore.collection(FirestoreUsersCollection).document(uid)

                firestore.runTransaction {
                    val reservation = get(pseudoRef)
                    val ownerUid = if (reservation.exists) reservation.get<String?>("uid") else null
                    if (ownerUid != null && ownerUid != uid) return@runTransaction false

                    set(
                        pseudoRef,
                        mapOf(
                            "uid" to uid,
                            "pseudo" to pseudo,
                            "createdAt" to FieldValue.serverTimestamp
                        )
                    )
                    set(
                        userRef,
                        mapOf(
                            "uid" to uid,
                            "information" to mapOf(
                                "name" to name,
                                "pseudo" to pseudo,
                                "pseudoLowercase" to pseudo.lowercase()
                            )
                        ),
                        merge = true
                    )
                    true
                }
            }.flatMap { isReserved ->
                if (isReserved) AppResult.Success(Unit)
                else AppResult.Failure(AppError.Profile.PseudoTaken)
            }
        }

    override suspend fun ensurePseudoReservation(uid: String, pseudo: String): AppResult<Unit, AppError> =
        safeNetworkCall(connectivityChecker) {
            val pseudoRef = firestore.collection(FirestorePseudosCollection).document(pseudo.lowercase())
            if (!pseudoRef.get().exists) {
                pseudoRef.set(
                    mapOf(
                        "uid" to uid,
                        "pseudo" to pseudo,
                        "createdAt" to FieldValue.serverTimestamp
                    )
                )
            }
        }

    // Legacy users/{uid} docs may hold the pseudo without a reservation: only another uid makes it taken
    private suspend fun checkIfPseudoIsAvailableForUser(uid: String, pseudo: String): AppResult<Boolean, AppError> =
        safeNetworkCall(connectivityChecker) {
            firestore
                .collection(FirestoreUsersCollection)
                .where { "information.pseudoLowercase" equalTo pseudo.lowercase() }
                .limit(2)
                .get()
                .documents
                .none { it.id != uid }
        }
}
