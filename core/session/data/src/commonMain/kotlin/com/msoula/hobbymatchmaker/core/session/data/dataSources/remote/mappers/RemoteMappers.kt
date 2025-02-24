package com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.mappers

import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.models.UserFireStoreModel
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel

fun SessionUserDomainModel.toUserFireStoreModel(): UserFireStoreModel {
    return UserFireStoreModel(
        uid = this.uid,
        email = this.email
    )
}
