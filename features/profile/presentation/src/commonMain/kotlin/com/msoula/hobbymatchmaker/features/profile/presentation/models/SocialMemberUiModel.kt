package com.msoula.hobbymatchmaker.features.profile.presentation.models

data class SocialMemberUiModel(
    val uid: String = "",
    val name: String? = "",
    val pseudo: String = "",
    val avatarUrl: String? = "",
    val commonMoviesCount: Int? = 0
) {
    companion object {
        val Initial = SocialMemberUiModel()
    }
}
