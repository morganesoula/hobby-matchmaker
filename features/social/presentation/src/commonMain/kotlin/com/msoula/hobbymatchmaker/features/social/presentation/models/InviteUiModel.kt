package com.msoula.hobbymatchmaker.features.social.presentation.models

data class InviteUiModel(
    val ownerId: String = "",
    val ownerPseudo: String = "",
    val inviteId: String = "",
    val guestUid: String = "",
    val guestName: String = "",
    val guestPseudo: String = "",
    val guestAvatarUrl: String = "",
    val commonMoviesCount: Int = 0,
    val inviteTime: Long = 0L,
    val inviteStatus: InviteStatusUiModel = InviteStatusUiModel.PENDING
) {
    companion object {
        val Initial = InviteUiModel()
    }
}
