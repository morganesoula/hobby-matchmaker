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
    val updatedAt: Instant? = null,
    // The other person of the invite (sender when received, recipient when sent); null when unknown
    val counterpartUid: String? = null,
    val counterpartName: String? = null,
    val counterpartAvatarUrl: String? = null,
    val commonMoviesCount: Int = 0
) {
    companion object {
        val Initial = SocialInviteDomainModel()
    }
}
