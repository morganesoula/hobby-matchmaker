package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Invite @OptIn(ExperimentalTime::class) constructor(
    val inviteId: String,
    val fromUid: String,
    val toPseudo: String,
    val name: String?,
    val status: InviteStatus,
    val createdAt: Instant,
    val updatedAt: Instant?
)
