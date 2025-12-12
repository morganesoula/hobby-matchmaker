package com.msoula.hobbymatchmaker.features.social.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.EmptyStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.icons.Sad_tab
import com.msoula.hobbymatchmaker.core.design.models.EmptyStateConfig
import com.msoula.hobbymatchmaker.core.design.models.TabItem
import com.msoula.hobbymatchmaker.core.design.no_data
import com.msoula.hobbymatchmaker.core.design.not_found
import com.msoula.hobbymatchmaker.core.design.organisms.ReceivedInvitationSection
import com.msoula.hobbymatchmaker.core.design.organisms.SentInvitationSection
import com.msoula.hobbymatchmaker.core.design.social_sent_requests_no_data_description
import com.msoula.hobbymatchmaker.core.design.social_sent_requests_no_data_title
import com.msoula.hobbymatchmaker.core.design.templates.SocialLayout
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toReceivedInvitations
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toSentInvitations
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
                StateContainer(
                    state = incomingInvites,
                    onLoading = {},
                    onEmpty = {
                        EmptyStateScreen(
                            EmptyStateConfig(
                                icon = Icons.Outlined.MovieFilter,
                                title = UIText.Resource(Res.string.no_data),
                                description = UIText.Resource(Res.string.not_found)
                            )
                        )
                    },
                    onError = { error, hint ->
                        ErrorStateScreen(
                            error = error,
                            hint = hint,
                            onRetry = {}
                        )
                    },
                    onSuccess = { invites ->
                        ReceivedInvitationSection(
                            invites.toReceivedInvitations(),
                            onAcceptInvitationClick = { id, guestUid ->
                                socialViewModel.onEvent(
                                    SocialUiEventModel.OnAcceptInvitation(
                                        id, guestUid
                                    )
                                )
                            },
                            onDeclineInvitationClick = { id ->
                                socialViewModel.onEvent(
                                    SocialUiEventModel.OnDeclineInvitation(
                                        id
                                    )
                                )
                            }
                        )
                    }
                )
            },
            sentContent = {
                StateContainer(
                    state = sentInvites,
                    onLoading = {},
                    onEmpty = {
                        EmptyStateScreen(
                            EmptyStateConfig(
                                icon = Sad_tab,
                                title = UIText.Resource(Res.string.social_sent_requests_no_data_title),
                                description = UIText.Resource(Res.string.social_sent_requests_no_data_description)
                            )
                        )
                    },
                    onError = { error, hint ->
                        ErrorStateScreen(
                            error = error,
                            hint = hint,
                            onRetry = { socialViewModel.observeSessionAndInvites() }
                        )
                    },
                    onSuccess = { invites ->
                        SentInvitationSection(
                            invites.toSentInvitations(),
                            onCancelInvitationClick = { id ->
                                socialViewModel.onEvent(
                                    SocialUiEventModel.OnCancelInvitation(
                                        id
                                    )
                                )
                            }
                        )
                    }
                )
            }
        )
    }
}
