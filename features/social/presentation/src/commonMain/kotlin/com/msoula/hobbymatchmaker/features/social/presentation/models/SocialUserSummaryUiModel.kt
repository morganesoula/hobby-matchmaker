package com.msoula.hobbymatchmaker.features.social.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class SocialUserSummaryUiModel(
    val uid: String,
    val pseudo: String,
    val name: String?,
    val avatarUrl: String?,
    val commonMoviesCount: Int?
)
