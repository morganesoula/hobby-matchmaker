package com.msoula.hobbymatchmaker.core.session.data.models.mappers

import com.msoula.hobbymatchmaker.core.common.models.UserDataStore
import com.msoula.hobbymatchmaker.core.session.domain.models.ConnexionMode
import com.msoula.hobbymatchmaker.core.session.domain.models.UserDomainModel

fun UserDataStore.toUserDomainModel(): UserDomainModel {
    return UserDomainModel(
        email = this.email,
        connexionMode = ConnexionMode.valueOf(this.connexionMode)
    )
}

fun UserDomainModel.toUserDataStore(): UserDataStore {
    return UserDataStore(
        email = this.email,
        connexionMode = this.connexionMode.name
    )
}
