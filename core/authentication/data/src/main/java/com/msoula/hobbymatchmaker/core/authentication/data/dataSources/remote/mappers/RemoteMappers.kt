package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers

import com.google.firebase.auth.FirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel

fun FirebaseUser.toFirebaseUserInfoDomainModel(): FirebaseUserInfoDomainModel {
    return FirebaseUserInfoDomainModel(
        uid = this.uid,
        email = this.email ?: "",
        providers = this.providerData.map { it.providerId }
    )
}
