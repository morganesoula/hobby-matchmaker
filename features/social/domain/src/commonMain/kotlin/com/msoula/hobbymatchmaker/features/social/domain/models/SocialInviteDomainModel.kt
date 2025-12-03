package com.msoula.hobbymatchmaker.features.social.domain.models

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class SocialInviteDomainModel @OptIn(ExperimentalTime::class) constructor(
    val inviteId: String,
    val fromUid: String,
    val toUid: String,
    val status: InviteStatus,
    val createdAt: Instant,
    val updatedAt: Instant
)
