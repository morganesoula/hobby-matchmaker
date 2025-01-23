package com.msoula.hobbymatchmaker.core.common.module

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.module

val databaseModule = module {
    single<FirebaseFirestore> {
        Firebase.firestore
    }
}
