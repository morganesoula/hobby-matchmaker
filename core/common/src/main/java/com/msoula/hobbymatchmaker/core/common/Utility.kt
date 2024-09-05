package com.msoula.hobbymatchmaker.core.common

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

fun getDeviceLocale(): String {
    val locale = Locale.getDefault()
    return "${locale.language}-${locale.country}"
}

fun Query.snapshotFlow(): Flow<QuerySnapshot> = callbackFlow {
    val listenerRegistration = addSnapshotListener { value, error ->
        if (error != null) {
            close(error)
            return@addSnapshotListener
        }

        if (value != null) {
            trySend(value)
        }
    }

    awaitClose {
        listenerRegistration.remove()
    }
}
