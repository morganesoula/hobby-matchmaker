package com.msoula.hobbymatchmaker.features.social.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.EmptyStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsRoundedSad_tab
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
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toReceivedInvitations
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toSentInvitations
import com.msoula.hobbymatchmaker.features.social.presentation.models.InviteUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUiEventModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun SocialContent(
    sentInvites: UiState<ImmutableList<InviteUiModel>>,
    incomingInvites: UiState<ImmutableList<InviteUiModel>>,
    tabs: ImmutableList<TabItem>,
    observeSessionAndInvites: () -> Unit,
    onEvent: (SocialUiEventModel) -> Unit
) {
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
                                onEvent(
                                    SocialUiEventModel.OnAcceptInvitation(
                                        id, guestUid
                                    )
                                )
                            },
                            onDeclineInvitationClick = { id ->
                                onEvent(
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
                                icon = MaterialSymbolsRoundedSad_tab,
                                title = UIText.Resource(Res.string.social_sent_requests_no_data_title),
                                description = UIText.Resource(Res.string.social_sent_requests_no_data_description)
                            )
                        )
                    },
                    onError = { error, hint ->
                        ErrorStateScreen(
                            error = error,
                            hint = hint,
                            onRetry = { observeSessionAndInvites() }
                        )
                    },
                    onSuccess = { invites ->
                        SentInvitationSection(
                            invites.toSentInvitations(),
                            onCancelInvitationClick = { id ->
                                onEvent(
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
