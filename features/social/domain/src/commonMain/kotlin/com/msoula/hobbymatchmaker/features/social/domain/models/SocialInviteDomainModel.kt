package com.msoula.hobbymatchmaker.features.social.domain.models

import kotlin.time.Clock
import kotlin.time.Instant

data class SocialInviteDomainModel(
    val inviteId: String = "",
    val fromUid: String = "",
    val fromPseudo: String? = null,
    val toPseudo: String? = null,
    val name: String? = "",
    val status: InviteStatus = InviteStatus.PENDING,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant? = null
) {
    companion object {
        val Initial = SocialInviteDomainModel()
    }
}
