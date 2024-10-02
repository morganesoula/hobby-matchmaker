package com.msoula.hobbymatchmaker.core.session.data.data_sources.remote.mappers

import com.msoula.hobbymatchmaker.core.session.data.data_sources.remote.models.UserFireStoreModel
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel

fun SessionUserDomainModel.toUserFireStoreModel(): UserFireStoreModel {
    return UserFireStoreModel(
        uid = this.uid,
        email = this.email
    )
}
