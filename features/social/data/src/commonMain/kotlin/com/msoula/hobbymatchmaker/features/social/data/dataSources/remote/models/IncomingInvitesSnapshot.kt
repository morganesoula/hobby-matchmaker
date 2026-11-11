package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

// The pseudo comes from the same fresh read as the invites, so an empty list can still clear the right local rows
data class IncomingInvitesSnapshot(
    val toPseudo: String?,
    val invites: List<InviteDataModel>
)
