package com.msoula.hobbymatchmaker.core.design.models

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class RecentMatchMember(
    val uid: String,
    val name: String?,
    val pseudo: String,
    val avatarUrl: String,
    val commonMovies: ImmutableList<MovieCarouselItem>
)
