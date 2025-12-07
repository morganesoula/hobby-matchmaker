package com.msoula.hobbymatchmaker.features.social.presentation.models

data class InviteUiModel(
    val ownerId: String = "",
    val inviteId: Long = 0L,
    val guestName: String = "",
    val guestPseudo: String = "",
    val guestAvatarUrl: String = "",
    val inviteTime: String = "",
    val inviteStatus: String = ""
)
