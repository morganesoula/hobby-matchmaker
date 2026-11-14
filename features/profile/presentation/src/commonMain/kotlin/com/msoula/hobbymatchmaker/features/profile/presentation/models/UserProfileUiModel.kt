package com.msoula.hobbymatchmaker.features.profile.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class UserProfileUiModel(
    val name: String = "",
    val pseudo: String = "",
    val avatarUrl: String? = null,
    val bio: String? = null,
    val interests: List<String>? = null,
    val moviesLikedCount: Int = 0
) {
    companion object {
        val Initial = UserProfileUiModel()
    }
}
