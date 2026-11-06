package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.msoula.hobbymatchmaker.core.design.social_received_section_title
import com.msoula.hobbymatchmaker.core.design.social_sent_section_hint
import com.msoula.hobbymatchmaker.core.design.social_sent_section_title
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.models.Invitation
import com.msoula.hobbymatchmaker.core.design.molecules.ReceivedInvitationCard
import com.msoula.hobbymatchmaker.core.design.molecules.SentInvitationCard
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReceivedInvitationSection(
    receivedInvitation: ImmutableList<Invitation>,
    onAcceptInvitationClick: (invitationId: String, guestUid: String) -> Unit,
    onDeclineInvitationClick: (invitationId: String) -> Unit,
    onReportClick: (guestUid: String, displayName: String) -> Unit,
    onBlockClick: (guestUid: String, displayName: String) -> Unit
) {
    if (receivedInvitation.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = CustomSize.Sixteen),
            verticalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
        ) {
            item(key = "header") {
                SectionHeader(title = stringResource(Res.string.social_received_section_title))
            }

            items(receivedInvitation, key = { it.invitationId }) { invitation ->
                ReceivedInvitationCard(
                    invitationId = invitation.invitationId,
                    guestUid = invitation.invitationGuestUid,
                    guestAvatarUrl = invitation.invitationGuestAvatarUrl,
                    guestName = invitation.invitationGuestName,
                    guestPseudo = invitation.invitationGuestPseudo,
                    commonMoviesCount = invitation.commonMoviesCount,
                    inviteTime = invitation.invitationTime,
                    isBusy = invitation.isBusy,
                    onAcceptInvitationClick = onAcceptInvitationClick,
                    onDeclineInvitationClick = onDeclineInvitationClick,
                    onReportClick = {
                        onReportClick(invitation.invitationGuestUid, invitation.displayName())
                    },
                    onBlockClick = {
                        onBlockClick(invitation.invitationGuestUid, invitation.displayName())
                    }
                )
            }
        }
    }
}

@Composable
fun SentInvitationSection(
    sentInvitation: ImmutableList<Invitation>,
    onCancelInvitationClick: (invitationId: String) -> Unit
) {
    if (sentInvitation.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = CustomSize.Sixteen),
            verticalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
        ) {
            item(key = "header") {
                SectionHeader(
                    title = stringResource(Res.string.social_sent_section_title, sentInvitation.size),
                    hint = stringResource(Res.string.social_sent_section_hint)
                )
            }

            items(sentInvitation, key = { it.invitationId }) { invitation ->
                SentInvitationCard(
                    invitationId = invitation.invitationId,
                    guestAvatarUrl = invitation.invitationGuestAvatarUrl,
                    guestName = invitation.invitationGuestName,
                    guestPseudo = invitation.invitationGuestPseudo,
                    commonMoviesCount = invitation.commonMoviesCount,
                    inviteStatus = invitation.invitationStatus,
                    inviteTime = invitation.invitationTime,
                    isBusy = invitation.isBusy,
                    onCancelInvitationClick = onCancelInvitationClick
                )
            }
        }
    }
}

private fun Invitation.displayName(): String =
    invitationGuestName.ifBlank { "@$invitationGuestPseudo" }

@Composable
internal fun SectionHeader(title: String, hint: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.Sixteen),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        hint?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
fun ReceivedInvitationSectionPreview() {
    Column {
        ReceivedInvitationSection(
            persistentListOf(
                Invitation(
                    ownerId = "AZERTY",
                    invitationId = "123AZE",
                    invitationGuestUid = "123123123",
                    invitationGuestName = "Lena Aluos",
                    invitationGuestPseudo = "pépé",
                    invitationGuestAvatarUrl = "",
                    invitationTime = "Lundi",
                    invitationStatus = "PENDING"
                )
            ),
            { _, _ -> },
            {},
            { _, _ -> },
            { _, _ -> }
        )
    }
}
