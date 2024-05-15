package com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.mappers

import com.google.firebase.auth.FirebaseAuth
import com.msoula.hobbymatchmaker.core.authentication.domain.models.UserDomainModel

fun FirebaseAuth.toUserDomainModel(): UserDomainModel {
    return UserDomainModel(
        isConnected = this.currentUser != null
    )
}
