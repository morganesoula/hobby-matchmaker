package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
fun ProfileEditProfilePicture(
    modifier: Modifier = Modifier,
    customAvatarPath: String? = null,
    onAvatarClicked: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = CustomSize.Sixteen),
        contentAlignment = Alignment.Center
    ) {
        ProfileAvatar(avatarUrl = customAvatarPath, onChangePhotoClicked = onAvatarClicked)
    }
}
