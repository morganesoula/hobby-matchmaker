package com.msoula.hobbymatchmaker.features.social.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class CircleMemberUiModel(
    val uid: String = "",
    val name: String? = null,
    val pseudo: String = "",
    val avatarUrl: String? = null,
    val commonMoviesCount: Int = 0
)
