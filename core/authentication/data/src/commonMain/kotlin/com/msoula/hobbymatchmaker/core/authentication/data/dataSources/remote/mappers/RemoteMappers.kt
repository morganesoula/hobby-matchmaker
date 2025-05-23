package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toFirebaseUserInfoDomainModel(): FirebaseUserInfoDomainModel {
    return FirebaseUserInfoDomainModel(
        uid = this.uid,
        email = this.email ?: "",
        providers = this.providerData.map { it.providerId }
    )
}
