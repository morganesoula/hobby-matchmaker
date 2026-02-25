package com.msoula.hobbymatchmaker.core.design.models

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileSocialMember(
    val uid: String = "",
    val name: String? = "",
    val pseudo: String = "",
    val avatarUrl: String? = "",
    val commonMoviesCount: Int? = 0,
    val sharedMovieIds: List<Long>? = emptyList()
) {
    companion object {
        val Initial = ProfileSocialMember()
    }
}
