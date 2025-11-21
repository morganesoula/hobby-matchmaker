package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.CircleWithCustomPhoto
import com.msoula.hobbymatchmaker.core.design.atoms.CircleWithDefaultIcon
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.authentified_edit_button_title
import com.msoula.hobbymatchmaker.core.design.icons.Edit
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun GuestProfileHeader(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    titleHeader: String,
    descriptionHeader: String
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .padding(top = CustomSize.Sixteen)
            .padding(horizontal = CustomSize.Sixteen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircleWithDefaultIcon(
                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = .5f),
                icon = icon,
                iconTint = MaterialTheme.colorScheme.onSurface
            )

            SpacerHeight8()

            Text(
                text = titleHeader,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            SpacerHeight4()

            Text(
                text = descriptionHeader,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AuthentifiedProfileHeader(
    modifier: Modifier = Modifier,
    fullName: String,
    biography: String,
    avatarPath: String? = null,
    onEditProfileClicked: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .padding(top = CustomSize.Sixteen)
            .padding(horizontal = CustomSize.Sixteen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircleWithCustomPhoto(
                customAvatarPath = avatarPath
            )
            SpacerHeight8()
            Text(
                text = fullName,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            SpacerHeight4()
            Text(
                text = biography,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            SpacerHeight16()

            Button(
                onClick = { onEditProfileClicked() },
                modifier = Modifier.border(
                    2.dp, MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(CustomSize.Sixteen)
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Row(
                    modifier = modifier
                        .wrapContentSize()
                        .padding(CustomSize.Four),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Edit,
                        contentDescription = null
                    )

                    Text(
                        text = stringResource(Res.string.authentified_edit_button_title),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
