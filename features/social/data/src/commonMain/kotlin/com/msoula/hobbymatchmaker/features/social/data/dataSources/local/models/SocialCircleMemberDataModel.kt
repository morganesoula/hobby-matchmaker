package com.msoula.hobbymatchmaker.features.social.data.dataSources.local.models

data class SocialCircleMemberDataModel(
    val ownerUid: String = "",
    val memberUid: String = "",
    val memberPseudo: String? = null,
    val memberName: String? = null,
    val memberAvatarUrl: String? = null
) {
    companion object {
        val Initial = SocialCircleMemberDataModel()
    }
}
