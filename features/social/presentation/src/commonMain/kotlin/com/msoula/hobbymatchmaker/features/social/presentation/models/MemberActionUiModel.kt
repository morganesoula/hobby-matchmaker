package com.msoula.hobbymatchmaker.features.social.presentation.models

// A destructive action waiting for the user's confirmation
internal sealed interface MemberActionUiModel {
    val uid: String
    val displayName: String

    data class Remove(override val uid: String, override val displayName: String) : MemberActionUiModel
    data class Block(override val uid: String, override val displayName: String) : MemberActionUiModel
    data class Report(override val uid: String, override val displayName: String) : MemberActionUiModel
}
