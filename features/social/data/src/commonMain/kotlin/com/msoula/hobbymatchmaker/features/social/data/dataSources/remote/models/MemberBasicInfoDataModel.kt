package com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.models

data class MemberBasicInfoDataModel(
    val uid: String = "",
    val pseudo: String = "",
    val name: String? = "",
    val avatarUrl: String? = "",
    val commonMoviesCount: Int? = 0
) {
    companion object Companion {
        val Initial = MemberBasicInfoDataModel()
    }
}
