package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthUserRemoteDataModel
import com.msoula.hobbymatchmaker.core.authentication.data.models.ProviderTypeDataModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthenticatedUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toAuthFirebaseUser(): AuthUserRemoteDataModel =
    AuthUserRemoteDataModel(
        uid = this.uid,
        email = this.email,
        providers = this.providerData.map { it.providerId }.filterNot { it == "firebase" }
    )

fun FirebaseUser.toAuthFirebaseUserSignedInWith(providerId: String): AuthUserRemoteDataModel =
    this.toAuthFirebaseUser().copy(signInProvider = providerId)

fun AuthUserRemoteDataModel.toAuthenticatedUser(): AuthenticatedUser =
    AuthenticatedUser(
        uid = this.uid,
        email = this.email,
        providers = this.providers
    )

fun ProviderType.toProviderTypeDataModel(): ProviderTypeDataModel =
    when (this) {
        ProviderType.GOOGLE -> ProviderTypeDataModel.GOOGLE
        ProviderType.FACEBOOK -> ProviderTypeDataModel.FACEBOOK
        ProviderType.APPLE -> ProviderTypeDataModel.APPLE
    }

