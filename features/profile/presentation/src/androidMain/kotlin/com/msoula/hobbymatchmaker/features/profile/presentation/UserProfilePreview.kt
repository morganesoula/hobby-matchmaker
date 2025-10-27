package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import java.util.Collections.emptyList

@Preview
@Composable
fun UserProfileScreenContentPreview() {
    UserProfileScreenContent(
        userProfile = UserProfileUiModel(
            name = "Test name preview",
            avatarUrl = "",
            interests = emptyList(),
            moviesLikedCount = 10,
            socialMembersCount = 5,
            socialMembers = emptyList()
        )
    )
}
