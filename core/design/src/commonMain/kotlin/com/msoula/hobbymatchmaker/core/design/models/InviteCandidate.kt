package com.msoula.hobbymatchmaker.core.design.models

import androidx.compose.runtime.Immutable

@Immutable
data class InviteCandidate(
    val pseudo: String,
    val name: String,
    val avatarUrl: String,
    val isInvited: Boolean = false
)

enum class InviteSearchStatus { Idle, Searching, Done }
