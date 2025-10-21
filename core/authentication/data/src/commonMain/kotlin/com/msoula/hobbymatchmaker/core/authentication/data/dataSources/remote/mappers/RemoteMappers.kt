package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthFirebaseUser.Companion.DEFAULT_EMAIL
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toAuthFirebaseUser(): AuthFirebaseUser =
    AuthFirebaseUser(
        uid = this.uid,
        email = this.email,
        providers = this.providerData.map { it.providerId }.filterNot { it == "firebase" }
    )

fun FirebaseUser.toAuthFirebaseUserSignedInWith(providerId: String): AuthFirebaseUser =
    this.toAuthFirebaseUser().copy(signInProvider = providerId)

fun AuthFirebaseUser.toFirebaseUserInfoDomainModel(): FirebaseUserInfoDomainModel =
    FirebaseUserInfoDomainModel(
        uid = this.uid,
        email = this.email ?: DEFAULT_EMAIL,
        providers = this.providers
    )
