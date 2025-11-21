package com.msoula.hobbymatchmaker.features.profile.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class UserProfileUiModel(
    val name: String,
    val pseudo: String,
    val avatarUrl: String?,
    val bio: String?,
    val interests: List<String>?,
    val moviesLikedCount: Int,
    val socialMembersCount: Int,
    val socialMembers: List<SocialMemberUiModel>
)
