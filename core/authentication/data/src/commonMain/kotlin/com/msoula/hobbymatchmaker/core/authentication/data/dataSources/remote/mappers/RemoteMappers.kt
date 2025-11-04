package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.core.authentication.data.models.RemoteAuthUser
import com.msoula.hobbymatchmaker.core.authentication.data.models.RemoteAuthUser.Companion.DEFAULT_EMAIL
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthenticatedUser
import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toAuthFirebaseUser(): RemoteAuthUser =
    RemoteAuthUser(
        uid = this.uid,
        email = this.email,
        providers = this.providerData.map { it.providerId }.filterNot { it == "firebase" }
    )

fun FirebaseUser.toAuthFirebaseUserSignedInWith(providerId: String): RemoteAuthUser =
    this.toAuthFirebaseUser().copy(signInProvider = providerId)

fun RemoteAuthUser.toAuthenticatedUser(): AuthenticatedUser =
    AuthenticatedUser(
        uid = this.uid,
        email = this.email ?: DEFAULT_EMAIL,
        providers = this.providers
    )

