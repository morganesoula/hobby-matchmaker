package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

import kotlinx.serialization.Serializable

@Serializable
enum class InviteStatusData(
) {
    PENDING, ACCEPTED, DECLINED
}
