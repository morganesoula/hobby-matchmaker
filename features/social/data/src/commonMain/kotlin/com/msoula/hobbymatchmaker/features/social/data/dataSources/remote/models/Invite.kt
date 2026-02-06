package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Invite @OptIn(ExperimentalTime::class) constructor(
    val inviteId: String = "",
    val fromUid: String = "",
    val fromPseudo: String = "",
    val toPseudo: String = "",
    val name: String? = null,
    val status: InviteStatusData = InviteStatusData.PENDING,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null
) {
    companion object {
        val Initial = Invite()
    }
}
