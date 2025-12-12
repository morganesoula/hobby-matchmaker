package com.msoula.hobbymatchmaker.features.social.presentation.models

import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus

data class InviteUiModel(
    val ownerId: String = "",
    val ownerPseudo: String = "",
    val inviteId: String = "",
    val guestUid: String = "",
    val guestName: String = "",
    val guestPseudo: String = "",
    val guestAvatarUrl: String = "",
    val inviteTime: Long = 0L,
    val inviteStatus: InviteStatus = InviteStatus.PENDING
)
