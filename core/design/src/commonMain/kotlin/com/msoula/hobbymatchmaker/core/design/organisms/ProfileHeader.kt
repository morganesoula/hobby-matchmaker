package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.CircleWithCustomPhoto
import com.msoula.hobbymatchmaker.core.design.atoms.CircleWithDefaultIcon
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth8
import com.msoula.hobbymatchmaker.core.design.authentified_edit_button_title
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapCamera
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapPerson
import com.msoula.hobbymatchmaker.core.design.icons.HeroiconsSparkles
import com.msoula.hobbymatchmaker.core.design.icons.LucideCopy
import com.msoula.hobbymatchmaker.core.design.icons.LucidePencil
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsIosShare
import com.msoula.hobbymatchmaker.core.design.models.ProfileHeaderUiModel
import com.msoula.hobbymatchmaker.core.design.profile_bio_placeholder_text
import com.msoula.hobbymatchmaker.core.design.profile_bio_placeholder_title
import com.msoula.hobbymatchmaker.core.design.profile_change_photo
import com.msoula.hobbymatchmaker.core.design.profile_copy_pseudo
import com.msoula.hobbymatchmaker.core.design.profile_share_button
import com.msoula.hobbymatchmaker.core.design.profile_share_hint
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.IconSize
import com.msoula.hobbymatchmaker.core.design.util.dashedBorder
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
    user: ProfileHeaderUiModel,
    showShareHint: Boolean,
    onChangePhotoClicked: () -> Unit,
    onCopyPseudoClicked: () -> Unit,
    onAddBioClicked: () -> Unit,
    onEditProfileClicked: () -> Unit,
    onShareProfileClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = CustomSize.Sixteen)
            .padding(horizontal = CustomSize.TwentyFour),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileAvatar(avatarUrl = user.avatarUrl, onChangePhotoClicked = onChangePhotoClicked)
        SpacerHeight16()

        if (user.name.isNotBlank()) {
            Text(
                text = user.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            SpacerHeight8()
        }

        if (user.pseudo.isNotBlank()) {
            PseudoPill(pseudo = user.pseudo, onClick = onCopyPseudoClicked)
        }

        if (showShareHint) {
            SpacerHeight8()
            Text(
                text = stringResource(Res.string.profile_share_hint),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        SpacerHeight16()

        if (user.biography.isNotBlank()) {
            Text(
                text = user.biography,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .8f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        } else {
            BioPlaceholder(onClick = onAddBioClicked)
        }

        SpacerHeight16()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CustomSize.Eight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onShareProfileClicked,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(CustomSize.Sixteen),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = MaterialIconsIosShare,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.Sixteen)
                )
                SpacerWidth8()
                Text(text = stringResource(Res.string.profile_share_button), maxLines = 1)
            }

            OutlinedButton(
                onClick = onEditProfileClicked,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(CustomSize.Sixteen),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = .4f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Icon(
                    imageVector = LucidePencil,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.Sixteen)
                )
                SpacerWidth8()
                Text(
                    text = stringResource(Res.string.authentified_edit_button_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
internal fun ProfileAvatar(
    avatarUrl: String?,
    onChangePhotoClicked: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val avatarSize = CustomSize.NinetySix + CustomSize.Sixteen

    // No clip on this box: the camera badge sits on the avatar's edge and would be cut in half
    Box(modifier = Modifier.size(avatarSize + CustomSize.Eight)) {
        if (avatarUrl.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .clickable(onClick = onChangePhotoClicked)
                    .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
                    .drawBehind {
                        drawCircle(
                            color = primary.copy(alpha = .6f),
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12.dp.toPx(), 10.dp.toPx()))
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = BootstrapPerson,
                    contentDescription = null,
                    tint = primary.copy(alpha = .7f),
                    modifier = Modifier.size(IconSize.FortyEight)
                )
            }
        } else {
            CircleWithCustomPhoto(
                modifier = Modifier
                    .align(Alignment.Center)
                    .shadow(CustomSize.TwentyFour, CircleShape, ambientColor = primary, spotColor = primary)
                    .clip(CircleShape)
                    .clickable(onClick = onChangePhotoClicked),
                borderColor = primary,
                customAvatarPath = avatarUrl
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(CustomSize.ThirtyTwo)
                .clip(CircleShape)
                .clickable(onClick = onChangePhotoClicked)
                .background(primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = BootstrapCamera,
                contentDescription = stringResource(Res.string.profile_change_photo),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(IconSize.Sixteen)
            )
        }
    }
}

@Composable
internal fun PseudoPill(
    pseudo: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = .3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = CustomSize.Sixteen, vertical = CustomSize.Eight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "@$pseudo",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            SpacerWidth8()
            Icon(
                imageVector = LucideCopy,
                contentDescription = stringResource(Res.string.profile_copy_pseudo),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f),
                modifier = Modifier.size(IconSize.Sixteen)
            )
        }
    }
}

@Composable
internal fun BioPlaceholder(onClick: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CustomSize.Sixteen))
            .clickable(onClick = onClick)
            .dashedBorder(primary.copy(alpha = .4f), CustomSize.Sixteen)
            .padding(CustomSize.Sixteen),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = HeroiconsSparkles,
            contentDescription = null,
            tint = primary,
            modifier = Modifier.size(IconSize.TwentyFour)
        )
        SpacerWidth8()
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.profile_bio_placeholder_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(Res.string.profile_bio_placeholder_text),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = .6f),
                style = MaterialTheme.typography.bodySmall
            )
        }
        SpacerWidth8()
        Icon(
            imageVector = LucidePencil,
            contentDescription = null,
            tint = primary,
            modifier = Modifier.size(IconSize.Sixteen)
        )
    }
}
