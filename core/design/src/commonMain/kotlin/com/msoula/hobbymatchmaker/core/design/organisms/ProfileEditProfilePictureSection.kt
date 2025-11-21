package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.CircleWithCustomPhoto
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.edit_profile_edit_photo_title
import com.msoula.hobbymatchmaker.core.design.icons.Camera
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileEditProfilePicture(
    modifier: Modifier = Modifier,
    customAvatarPath: String? = null,
    onAvatarClicked: () -> Unit
) {
    GenericCard(
        modifier = modifier.padding(
            top = CustomSize.Eight,
            start = CustomSize.Eight,
            end = CustomSize.Eight
        ),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(CustomSize.Eight)
        ) {
            Box {
                CircleWithCustomPhoto(
                    customAvatarPath = customAvatarPath,
                    borderColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.surface
                )

                IconButton(
                    onClick = { onAvatarClicked() },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Icon(
                        imageVector = Camera,
                        contentDescription = null,
                        modifier = Modifier
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.primary, CircleShape
                            )
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(CustomSize.Eight),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Text(text = stringResource(Res.string.edit_profile_edit_photo_title))
        }
    }
}

@Preview
@Composable
fun ProfileEditProfilePicturePreview() {
    ProfileEditProfilePicture(onAvatarClicked = {})
}
