package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

data class BlockedUserRemoteDataModel(
    val uid: String = "",
    val pseudo: String? = null,
    val blockedAt: Long? = null
)
