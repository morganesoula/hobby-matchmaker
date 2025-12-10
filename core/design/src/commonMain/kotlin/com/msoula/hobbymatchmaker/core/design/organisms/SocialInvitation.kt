package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.icons.Person_add
import com.msoula.hobbymatchmaker.core.design.models.Invitation
import com.msoula.hobbymatchmaker.core.design.molecules.ReceivedInvitationCard
import com.msoula.hobbymatchmaker.core.design.molecules.SentInvitationCard
import com.msoula.hobbymatchmaker.core.design.molecules.TipTextField
import com.msoula.hobbymatchmaker.core.design.social_received_requests_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReceivedInvitationSection(
    receivedInvitation: List<Invitation>,
    onAcceptInvitationClick: (invitationId: String) -> Unit,
    onDeclineInvitationClick: (invitationId: String) -> Unit
) {
    SpacerHeight4()
    TipTextField(
        hintText = stringResource(Res.string.social_received_requests_description),
        icon = Person_add
    )

    SpacerHeight8()

    if (receivedInvitation.isNotEmpty()) {
        LazyColumn {
            items(receivedInvitation) { invitation ->
                ReceivedInvitationCard(
                    invitationId = invitation.invitationId,
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
