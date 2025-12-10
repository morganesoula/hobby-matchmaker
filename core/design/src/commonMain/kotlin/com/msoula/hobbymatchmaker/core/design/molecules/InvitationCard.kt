package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.CircleWithDefaultIcon
import com.msoula.hobbymatchmaker.core.design.atoms.GenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth4
import com.msoula.hobbymatchmaker.core.design.icons.Check
import com.msoula.hobbymatchmaker.core.design.icons.ChromeClose
import com.msoula.hobbymatchmaker.core.design.icons.Clock
import com.msoula.hobbymatchmaker.core.design.icons.Sparkle
import com.msoula.hobbymatchmaker.core.design.icons.Trash
import com.msoula.hobbymatchmaker.core.design.social_received_requests_accept_button_text
import com.msoula.hobbymatchmaker.core.design.social_received_requests_decline_button_text
import com.msoula.hobbymatchmaker.core.design.social_received_requests_time
import com.msoula.hobbymatchmaker.core.design.social_sent_requests_cancel_invitation_text
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.util.asInviteStatusText
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SentInvitationCard(
    invitationId: String,
    guestAvatarUrl: String,
    guestName: String,
    guestPseudo: String,
    inviteStatus: String,
    inviteTime: String,
    onCancelInvitationClick: (invitationId: String) -> Unit = {}
) {
    val status = inviteStatus.lowercase()

    val backgroundColorStatus = when (status) {
        "pending" -> MaterialTheme.colorScheme.primary.copy(alpha = .15f)
        "declined" -> MaterialTheme.colorScheme.error.copy(alpha = .15f)
        else -> MaterialTheme.colorScheme.tertiary.copy(alpha = .15f)
    }

    val textColorStatus = when (status) {
        "pending" -> MaterialTheme.colorScheme.primary
        "declined" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.tertiary
    }

    val icon = when (status) {
        "pending" -> Clock
        "declined" -> ChromeClose
        else -> Check
    }

    GenericCard(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CustomSize.Eight)
            ) {
                CircleWithDefaultIcon(
                    backgroundColor = MaterialTheme.colorScheme.onSurface,
                    icon = Sparkle,
                    iconTint = MaterialTheme.colorScheme.primary,
                    size = CustomSize.FortyEight
                )
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = guestName.ifBlank { "@$guestPseudo" },
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Row(
                            modifier = Modifier.border(
                                1.dp,
                                backgroundColorStatus,
                                RoundedCornerShape(CustomSize.Eight)
                            )
                                .background(
                                    backgroundColorStatus,
                                    RoundedCornerShape(
                                        CustomSize.Eight
                                    )
                                )
                                .padding(CustomSize.Four),
                            horizontalArrangement = Arrangement.spacedBy(CustomSize.Four)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Time",
                                tint = textColorStatus
                            )

                            Text(
                                text = inviteStatus.asInviteStatusText(),
                                style = MaterialTheme.typography.labelMedium,
                                color = textColorStatus
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (guestName.isBlank()) "" else "@$guestPseudo",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = inviteTime,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            if (inviteStatus.lowercase() == "pending") {
                SpacerHeight4()
                Button(
                    onClick = { onCancelInvitationClick(invitationId) },
                    shape = RoundedCornerShape(CustomSize.Eight),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Trash,
                        contentDescription = ""
                    )

                    SpacerWidth4()

                    Text(text = stringResource(Res.string.social_sent_requests_cancel_invitation_text))
                }
            }
        }
    }
}

@Composable
fun ReceivedInvitationCard(
    modifier: Modifier = Modifier,
    invitationId: String,
    guestAvatarUrl: String,
    guestName: String,
    guestPseudo: String,
    inviteTime: String,
    onAcceptInvitationClick: (invitationId: String) -> Unit = {},
    onDeclineInvitationClick: (invitationId: String) -> Unit = {}
) {
    GenericCard(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CustomSize.Eight)
            ) {
                CircleWithDefaultIcon(
                    backgroundColor = MaterialTheme.colorScheme.onSurface,
                    icon = Sparkle,
                    iconTint = MaterialTheme.colorScheme.primary,
                    size = CustomSize.FortyEight
                )
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = guestName, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = stringResource(Res.string.social_received_requests_time) + " " + inviteTime
                        )
                    }

                    Text(text = "@$guestPseudo", style = MaterialTheme.typography.bodySmall)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onAcceptInvitationClick(invitationId) },
                    shape = RoundedCornerShape(CustomSize.Eight),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row {
                        Icon(
                            imageVector = Check,
                            contentDescription = "Accept",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        SpacerWidth4()
                        Text(
                            text = stringResource(Res.string.social_received_requests_accept_button_text),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Button(
                    onClick = { onDeclineInvitationClick(invitationId) },
                    border = BorderStroke(
                        1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = .8f)
                    ),
                    shape = RoundedCornerShape(CustomSize.Eight),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row {
                        Icon(
                            imageVector = ChromeClose,
                            contentDescription = "Decline",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        SpacerWidth4()
                        Text(
                            text = stringResource(Res.string.social_received_requests_decline_button_text),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ReceivedInvitationCardPreview() {
    ReceivedInvitationCard(
        invitationId = "",
        guestAvatarUrl = "",
        guestName = "Test received card",
        guestPseudo = "testCard",
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
        inviteStatus = "Pending",
        inviteTime = "1d",
        onCancelInvitationClick = {}
    )
}

