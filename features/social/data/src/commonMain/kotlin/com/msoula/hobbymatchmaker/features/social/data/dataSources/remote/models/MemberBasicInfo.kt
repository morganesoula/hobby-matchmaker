package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

data class MemberBasicInfo(
    val uid: String = "",
    val pseudo: String = "",
    val name: String? = "",
    val avatarUrl: String? = "",
    val commonMoviesCount: Int? = 0
) {
    companion object {
        val Initial = MemberBasicInfo()
    }
}
