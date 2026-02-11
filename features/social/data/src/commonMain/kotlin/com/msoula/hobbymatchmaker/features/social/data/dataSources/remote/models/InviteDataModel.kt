package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

import kotlin.time.Clock
import kotlin.time.Instant

data class InviteDataModel(
    val inviteId: String = "",
    val fromUid: String = "",
    val fromPseudo: String = "",
    val toPseudo: String = "",
    val name: String? = null,
    val status: InviteStatusData = InviteStatusData.PENDING,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null
) {
    companion object Companion {
        val Initial = InviteDataModel()
    }
}
