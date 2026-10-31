package com.msoula.hobbymatchmaker.features.social.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.DestructiveConfirmationDialog
import com.msoula.hobbymatchmaker.core.design.atoms.EmptyStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapSendCheck
import com.msoula.hobbymatchmaker.core.design.icons.FeatherInbox
import com.msoula.hobbymatchmaker.core.design.models.EmptyStateConfig
import com.msoula.hobbymatchmaker.core.design.models.TabItem
import com.msoula.hobbymatchmaker.core.design.organisms.ReceivedInvitationSection
import com.msoula.hobbymatchmaker.core.design.organisms.SentInvitationSection
import com.msoula.hobbymatchmaker.core.design.cancel
import com.msoula.hobbymatchmaker.core.design.social_decline_dialog_message
import com.msoula.hobbymatchmaker.core.design.social_decline_dialog_title
import com.msoula.hobbymatchmaker.core.design.social_received_requests_decline_button_text
import com.msoula.hobbymatchmaker.core.design.social_received_requests_no_data_description
import com.msoula.hobbymatchmaker.core.design.social_received_requests_no_data_title
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
import kotlinx.collections.immutable.ImmutableSet
import org.jetbrains.compose.resources.stringResource

@Composable
fun SocialContent(
    sentInvites: UiState<ImmutableList<InviteUiModel>>,
    incomingInvites: UiState<ImmutableList<InviteUiModel>>,
    tabs: ImmutableList<TabItem>,
    busyInviteIds: ImmutableSet<String>,
    snackBarHostState: SnackbarHostState,
    onEvent: (SocialUiEventModel) -> Unit
) {
    var inviteIdToDecline by remember { mutableStateOf<String?>(null) }

    inviteIdToDecline?.let { inviteId ->
        DestructiveConfirmationDialog(
            title = stringResource(Res.string.social_decline_dialog_title),
            message = stringResource(Res.string.social_decline_dialog_message),
            confirmButtonText = stringResource(Res.string.social_received_requests_decline_button_text),
            cancelButtonText = stringResource(Res.string.cancel),
            isLoading = false,
            onDismiss = { inviteIdToDecline = null },
            onConfirm = {
                inviteIdToDecline = null
                onEvent(SocialUiEventModel.OnDeclineInvitation(inviteId))
            }
        )
    }

    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) { paddingValues ->
        SocialLayout(
            paddingValues = paddingValues,
            tabs = tabs,
            receivedContent = {
                StateContainer(
                    state = incomingInvites,
                    onLoading = {},
                    onEmpty = {
                        EmptyStateScreen(
                            config = EmptyStateConfig(
                                icon = FeatherInbox,
                                title = UIText.Resource(Res.string.social_received_requests_no_data_title),
                                description = UIText.Resource(Res.string.social_received_requests_no_data_description)
                            )
                        )
                    },
                    onError = { error, hint ->
                        ErrorStateScreen(
                            error = error,
                            hint = hint,
                            onRetry = { onEvent(SocialUiEventModel.OnRetrySocialCircle) }
                        )
                    },
                    onSuccess = { invites ->
                        ReceivedInvitationSection(
                            invites.toReceivedInvitations(busyInviteIds),
                            onAcceptInvitationClick = { id, guestUid ->
                                onEvent(
                                    SocialUiEventModel.OnAcceptInvitation(
                                        id, guestUid
                                    )
                                )
                            },
                            onDeclineInvitationClick = { id -> inviteIdToDecline = id }
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
                            config = EmptyStateConfig(
                                icon = BootstrapSendCheck,
                                title = UIText.Resource(Res.string.social_sent_requests_no_data_title),
                                description = UIText.Resource(Res.string.social_sent_requests_no_data_description)
                            )
                        )
                    },
                    onError = { error, hint ->
                        ErrorStateScreen(
                            error = error,
                            hint = hint,
                            onRetry = { onEvent(SocialUiEventModel.OnRetrySocialCircle) }
                        )
                    },
                    onSuccess = { invites ->
                        SentInvitationSection(
                            invites.toSentInvitations(busyInviteIds),
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
