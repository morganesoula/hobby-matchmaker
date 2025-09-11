package com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class UserFireStoreModel(
    val uid: String,
    val email: String
)
