package com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models

data class SocialInvitationDataModel(
    val id: String = "",
    val fromUid: String = "",
    val fromPseudo: String? = null,
    val toPseudo: String? = null,
    val name: String? = null,
    val status: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long? = null
) {
    companion object {
        val Initial = SocialInvitationDataModel()
    }
}
