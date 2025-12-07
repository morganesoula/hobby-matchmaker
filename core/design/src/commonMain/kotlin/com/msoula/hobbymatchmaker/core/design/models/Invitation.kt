package com.msoula.hobbymatchmaker.core.design.models

data class Invitation(
    val ownerId: String,
    val invitationId: Long,
    val invitationGuestName: String,
    val invitationGuestPseudo: String,
    val invitationGuestAvatarUrl: String,
    val invitationTime: String,
    val invitationStatus: String
)
