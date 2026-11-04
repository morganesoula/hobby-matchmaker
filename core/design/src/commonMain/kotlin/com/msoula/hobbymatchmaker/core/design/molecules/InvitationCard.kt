package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth4
import com.msoula.hobbymatchmaker.core.design.atoms.UserAvatar
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapCheck
import com.msoula.hobbymatchmaker.core.design.icons.FeatherClock
import com.msoula.hobbymatchmaker.core.design.icons.LucideFilm
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsClose
import com.msoula.hobbymatchmaker.core.design.social_common_favorite_singular
import com.msoula.hobbymatchmaker.core.design.social_common_favorites_plural
import com.msoula.hobbymatchmaker.core.design.social_received_requests_accept_button_text
import com.msoula.hobbymatchmaker.core.design.social_received_requests_decline_button_text
import com.msoula.hobbymatchmaker.core.design.social_sent_requests_cancel_invitation_text
import com.msoula.hobbymatchmaker.core.design.social_sent_time
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.util.asInviteStatusText
import org.jetbrains.compose.resources.stringResource

@Composable
fun SentInvitationCard(
    modifier: Modifier = Modifier,
    invitationId: String,
    guestAvatarUrl: String,
    guestName: String,
    guestPseudo: String,
    commonMoviesCount: Int = 0,
    inviteStatus: String,
    inviteTime: String,
    isBusy: Boolean = false,
    onCancelInvitationClick: (invitationId: String) -> Unit = {}
) {
    val status = inviteStatus.lowercase()
    val isPending = status == "pending"

    val statusColor = when (status) {
        "pending" -> MaterialTheme.colorScheme.primary
        "declined" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.tertiary
    }

    val statusIcon = when (status) {
        "pending" -> FeatherClock
        "declined" -> MaterialIconsClose
        else -> BootstrapCheck
    }

    GenericCard(
        modifier = modifier.padding(horizontal = CustomSize.Sixteen),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
        ) {
            UserAvatar(avatarUrl = guestAvatarUrl, contentDescription = guestName)

            Column(modifier = Modifier.weight(1f)) {
                InvitationIdentity(guestName = guestName, guestPseudo = guestPseudo)
                CommonFavoritesLabel(commonMoviesCount)
                Text(
                    text = stringResource(Res.string.social_sent_time, inviteTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = CustomSize.Four)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .border(1.dp, statusColor.copy(alpha = .4f), RoundedCornerShape(CustomSize.Sixteen))
                        .background(statusColor.copy(alpha = .15f), RoundedCornerShape(CustomSize.Sixteen))
                        .padding(horizontal = CustomSize.Eight, vertical = CustomSize.Four),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(CustomSize.Four)
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(CustomSize.Sixteen)
                    )
                    Text(
                        text = inviteStatus.asInviteStatusText(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                }

                if (isPending) {
                    TextButton(
                        onClick = { onCancelInvitationClick(invitationId) },
                        enabled = !isBusy,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Icon(
                            imageVector = MaterialIconsClose,
                            contentDescription = null,
                            modifier = Modifier.size(CustomSize.Sixteen)
                        )
                        SpacerWidth4()
                        Text(
                            text = stringResource(Res.string.social_sent_requests_cancel_invitation_text),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReceivedInvitationCard(
    modifier: Modifier = Modifier,
    invitationId: String,
    guestUid: String,
    guestAvatarUrl: String,
    guestName: String,
    guestPseudo: String,
    commonMoviesCount: Int = 0,
    inviteTime: String,
    isBusy: Boolean = false,
    onAcceptInvitationClick: (invitationId: String, guestUid: String) -> Unit = { _, _ -> },
    onDeclineInvitationClick: (invitationId: String) -> Unit = {}
) {
    GenericCard(
        modifier = modifier.padding(horizontal = CustomSize.Sixteen),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
            ) {
                UserAvatar(avatarUrl = guestAvatarUrl, contentDescription = guestName)

                Column(modifier = Modifier.weight(1f)) {
                    InvitationIdentity(guestName = guestName, guestPseudo = guestPseudo)
                    CommonFavoritesLabel(commonMoviesCount)
                }

                Text(
                    text = inviteTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SpacerHeight16()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CustomSize.Eight)
            ) {
                Button(
                    onClick = { onAcceptInvitationClick(invitationId, guestUid) },
                    enabled = !isBusy,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(CustomSize.Sixteen),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = BootstrapCheck,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    SpacerWidth4()
                    Text(
                        text = stringResource(Res.string.social_received_requests_accept_button_text),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Button(
                    onClick = { onDeclineInvitationClick(invitationId) },
                    enabled = !isBusy,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .4f)),
                    shape = RoundedCornerShape(CustomSize.Sixteen),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = MaterialIconsClose,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    SpacerWidth4()
                    Text(
                        text = stringResource(Res.string.social_received_requests_decline_button_text),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// Without a name, the pseudo takes its place so the card never shows an empty title
@Composable
private fun InvitationIdentity(guestName: String, guestPseudo: String) {
    Text(
        text = guestName.ifBlank { "@$guestPseudo" },
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    if (guestName.isNotBlank()) {
        Text(
            text = "@$guestPseudo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CommonFavoritesLabel(count: Int) {
    if (count <= 0) return

    SpacerHeight4()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = LucideFilm,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(CustomSize.Sixteen)
        )
        SpacerWidth4()
        Text(
            text = if (count == 1) {
                stringResource(Res.string.social_common_favorite_singular)
            } else {
                stringResource(Res.string.social_common_favorites_plural, count)
            },
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun ReceivedInvitationCardPreview() {
    ReceivedInvitationCard(
        invitationId = "",
        guestUid = "123",
        guestAvatarUrl = "",
        guestName = "Test received card",
        guestPseudo = "testCard",
        commonMoviesCount = 5,
        inviteTime = "2h"
    )
}

@Preview
@Composable
fun SentInvitationCardPreview() {
    SentInvitationCard(
        invitationId = "",
        guestAvatarUrl = "",
        guestName = "Test name",
        guestPseudo = "pseudo",
        commonMoviesCount = 3,
        inviteStatus = "Pending",
        inviteTime = "1d",
        onCancelInvitationClick = {}
    )
}
