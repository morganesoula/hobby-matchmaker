package com.msoula.hobbymatchmaker.core.common

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val databaseModule = module {
    single<FirebaseFirestore> {
        Firebase.firestore
    }
}
