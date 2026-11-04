package com.msoula.hobbymatchmaker.core.design.models

data class Invitation(
    val ownerId: String,
    val invitationId: String,
    val invitationGuestUid: String,
    val invitationGuestName: String,
    val invitationGuestPseudo: String,
    val invitationGuestAvatarUrl: String,
    val invitationTime: String,
    val invitationStatus: String,
    val commonMoviesCount: Int = 0,
    val isBusy: Boolean = false
)
