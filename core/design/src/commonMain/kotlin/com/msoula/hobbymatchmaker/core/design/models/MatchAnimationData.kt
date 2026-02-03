package com.msoula.hobbymatchmaker.core.design.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class MatchAnimationData(
    val ownerAvatarUrl: String?,
    val matchingMembers: ImmutableList<MatchingMemberInfo>
) {
    companion object {
        val Empty = MatchAnimationData(null, persistentListOf())
    }

    val matchingMemberNames: String
        get() = matchingMembers.joinToString(", ") { it.displayName }
}

@Immutable
data class MatchingMemberInfo(
    val displayName: String,
    val avatarUrl: String?
)
