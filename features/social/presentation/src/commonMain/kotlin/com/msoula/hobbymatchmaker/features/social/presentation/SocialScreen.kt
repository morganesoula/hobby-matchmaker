package com.msoula.hobbymatchmaker.features.social.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.models.TabItem
import com.msoula.hobbymatchmaker.core.design.organisms.ReceivedInvitationSection
import com.msoula.hobbymatchmaker.core.design.organisms.SentInvitationSection
import com.msoula.hobbymatchmaker.core.design.templates.SocialLayout
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toListInvitation
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUiEventModel

@Composable
fun SocialContent(
    modifier: Modifier = Modifier,
    socialViewModel: SocialViewModel,
    tabs: List<TabItem>
) {
    val sentInvites by socialViewModel.sentInvites.collectAsState()
    val incomingInvites by socialViewModel.incomingInvites.collectAsState()

    Scaffold { paddingValues ->
        SocialLayout(
            paddingValues = paddingValues,
            tabs = tabs,
            receivedContent = {
                ReceivedInvitationSection(
                    incomingInvites.toListInvitation(),
                    onAcceptInvitationClick = { id ->
                        socialViewModel.onEvent(SocialUiEventModel.OnAcceptInvitation(id))
                    },
                    onDeclineInvitationClick = { id ->
                        socialViewModel.onEvent(SocialUiEventModel.OnDeclineInvitation(id))
                    }
                )
            },
            sentContent = {
                SentInvitationSection(
                    sentInvites.toListInvitation(),
                    onCancelInvitationClick = { id ->
                        socialViewModel.onEvent(SocialUiEventModel.OnCancelInvitation(id))
                    }
                )
            }
        )
    }
}
