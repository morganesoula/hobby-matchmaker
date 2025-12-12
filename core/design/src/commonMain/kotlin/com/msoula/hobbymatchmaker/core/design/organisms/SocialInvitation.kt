package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.icons.Person_add
import com.msoula.hobbymatchmaker.core.design.models.Invitation
import com.msoula.hobbymatchmaker.core.design.molecules.ReceivedInvitationCard
import com.msoula.hobbymatchmaker.core.design.molecules.SentInvitationCard
import com.msoula.hobbymatchmaker.core.design.molecules.TipTextField
import com.msoula.hobbymatchmaker.core.design.social_received_requests_description
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ReceivedInvitationSection(
    receivedInvitation: List<Invitation>,
    onAcceptInvitationClick: (invitationId: String, guestUid: String) -> Unit,
    onDeclineInvitationClick: (invitationId: String) -> Unit
) {
    SpacerHeight16()
    TipTextField(
        modifier = Modifier.padding(start = CustomSize.Sixteen, end = CustomSize.Sixteen),
        hintText = stringResource(Res.string.social_received_requests_description),
        icon = Person_add,
        iconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = .8f),
        textColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = .8f)
    )
    SpacerHeight16()

    if (receivedInvitation.isNotEmpty()) {
        LazyColumn {
            items(receivedInvitation) { invitation ->
                ReceivedInvitationCard(
                    invitationId = invitation.invitationId,
                    guestUid = invitation.invitationGuestUid,
                    guestAvatarUrl = invitation.invitationGuestAvatarUrl,
                    guestName = invitation.invitationGuestName,
                    guestPseudo = invitation.invitationGuestPseudo,
                    inviteTime = invitation.invitationTime,
                    onAcceptInvitationClick = onAcceptInvitationClick,
                    onDeclineInvitationClick = onDeclineInvitationClick
                )
            }
        }
    }
}

@Composable
fun SentInvitationSection(
    sentInvitation: List<Invitation>,
    onCancelInvitationClick: (invitationId: String) -> Unit
) {
    SpacerHeight16()

    if (sentInvitation.isNotEmpty()) {
        LazyColumn {
            items(sentInvitation) { invitation ->
                SentInvitationCard(
                    invitationId = invitation.invitationId,
                    guestAvatarUrl = invitation.invitationGuestAvatarUrl,
                    guestName = invitation.invitationGuestName,
                    guestPseudo = invitation.invitationGuestPseudo,
                    inviteStatus = invitation.invitationStatus,
                    inviteTime = invitation.invitationTime,
                    onCancelInvitationClick = onCancelInvitationClick
                )
            }
        }
    }
}

@Preview
@Composable
fun ReceivedInvitationSectionPreview() {
    Column {
        ReceivedInvitationSection(
            listOf(
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
            {}
        )
    }
}
