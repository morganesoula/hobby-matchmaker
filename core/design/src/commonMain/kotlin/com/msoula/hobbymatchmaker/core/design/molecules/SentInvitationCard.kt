package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth4
import com.msoula.hobbymatchmaker.core.design.icons.Check
import com.msoula.hobbymatchmaker.core.design.icons.ChromeClose
import com.msoula.hobbymatchmaker.core.design.icons.Clock
import com.msoula.hobbymatchmaker.core.design.icons.Sparkle
import com.msoula.hobbymatchmaker.core.design.icons.Trash
import com.msoula.hobbymatchmaker.core.design.social_received_requests_time
import com.msoula.hobbymatchmaker.core.design.social_sent_requests_cancel_invitation_text
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SentInvitationCard(
    modifier: Modifier = Modifier,
    guestAvatarUrl: String,
    guestName: String,
    guestPseudo: String,
    inviteStatus: String,
    inviteTime: String,
    onCancelInvitationClick: () -> Unit = {}
) {
    val backgroundColorStatus = when (inviteStatus) {
        "Pending" -> MaterialTheme.colorScheme.primary.copy(alpha = .15f)
        "Declined" -> MaterialTheme.colorScheme.error.copy(alpha = .15f)
        else -> MaterialTheme.colorScheme.error.copy(alpha = .15f)
    }

    val textColorStatus = when (inviteStatus) {
        "Pending" -> MaterialTheme.colorScheme.primary
        "Declined" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.error
    }

    val icon = when (inviteStatus) {
        "Pending" -> Clock
        "Declined" -> ChromeClose
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
                    verticalArrangement = Arrangement.spacedBy(CustomSize.Four)
                ) {
                    Text(text = guestName, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "@$guestPseudo", style = MaterialTheme.typography.bodySmall)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(CustomSize.Four),
                    horizontalAlignment = Alignment.End
                ) {
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
                            text = inviteStatus,
                            style = MaterialTheme.typography.labelSmall,
                            color = textColorStatus
                        )
                    }


                    Text(
                        text = stringResource(Res.string.social_received_requests_time) + " " + inviteTime
                    )
                }
            }

            if (inviteStatus == "Pending") {
                SpacerHeight8()
                Button(
                    onClick = { onCancelInvitationClick() },
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

@Preview
@Composable
fun SentInvitationCardPreview() {
    SentInvitationCard(
        guestAvatarUrl = "",
        guestName = "Test name",
        guestPseudo = "pseudo",
        inviteStatus = "Pending",
        inviteTime = "1d",
        onCancelInvitationClick = {}
    )
}

