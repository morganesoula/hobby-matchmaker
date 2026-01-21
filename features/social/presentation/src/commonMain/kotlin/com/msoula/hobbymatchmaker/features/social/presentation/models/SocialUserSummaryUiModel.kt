package com.msoula.hobbymatchmaker.features.social.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class SocialUserSummaryUiModel(
    val uid: String = "",
    val pseudo: String = "",
    val name: String? = null,
    val avatarUrl: String? = null,
    val commonMoviesCount: Int? = null
) {
    companion object {
        val Initial = SocialUserSummaryUiModel()
    }
}
