package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsPerson_add
import com.msoula.hobbymatchmaker.core.design.models.Invitation
import com.msoula.hobbymatchmaker.core.design.molecules.ReceivedInvitationCard
import com.msoula.hobbymatchmaker.core.design.molecules.SentInvitationCard
import com.msoula.hobbymatchmaker.core.design.molecules.TipTextField
import com.msoula.hobbymatchmaker.core.design.social_received_requests_description
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReceivedInvitationSection(
    receivedInvitation: ImmutableList<Invitation>,
    onAcceptInvitationClick: (invitationId: String, guestUid: String) -> Unit,
    onDeclineInvitationClick: (invitationId: String) -> Unit
) {
    SpacerHeight16()
    TipTextField(
        modifier = Modifier.padding(start = CustomSize.Sixteen, end = CustomSize.Sixteen),
        hintText = stringResource(Res.string.social_received_requests_description),
        icon = MaterialSymbolsPerson_add,
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

                SpacerHeight8()
            }
        }
    }
}

@Composable
fun SentInvitationSection(
    sentInvitation: ImmutableList<Invitation>,
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

        SpacerHeight8()
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
            {}
        )
    }
}
