package com.msoula.hobbymatchmaker.features.profile.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.msoula.hobbymatchmaker.features.profile.presentation.models.ProfileMode
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel

@Composable
fun AvatarBlock(
    mode: ProfileMode,
    avatarUrl: String?,
    onEvent: (UserProfileUiEventModel) -> Unit
) {
    Box(Modifier.size(96.dp)) {
        CircularAvatar(avatarUrl)
        if (mode == ProfileMode.Edit) {
            SmallFloatingActionButton(
                onClick = { onEvent(UserProfileUiEventModel.OnPickAvatarClicked) },
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {}
        }
    }
}

@Composable
fun CircularAvatar(avatarUrl: String?) {
    SubcomposeAsyncImage(
        model = avatarUrl,
        contentDescription = "User Avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
    )
}
