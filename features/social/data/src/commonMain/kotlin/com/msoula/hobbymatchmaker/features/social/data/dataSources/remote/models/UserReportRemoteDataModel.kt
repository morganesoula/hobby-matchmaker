package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

data class UserReportRemoteDataModel(
    val reporterUid: String,
    val reportedUid: String,
    val reason: String,
    val note: String? = null
)
