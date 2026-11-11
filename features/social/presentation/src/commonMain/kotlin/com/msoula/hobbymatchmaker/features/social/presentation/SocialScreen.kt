package com.msoula.hobbymatchmaker.features.social.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.DestructiveConfirmationDialog
import com.msoula.hobbymatchmaker.core.design.atoms.EmptyStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.InfoDialog
import com.msoula.hobbymatchmaker.core.design.atoms.ReportReasonDialog
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapSendCheck
import com.msoula.hobbymatchmaker.core.design.icons.FeatherInbox
import com.msoula.hobbymatchmaker.core.design.icons.FontAwesomeUserFriends
import com.msoula.hobbymatchmaker.core.design.models.EmptyStateConfig
import com.msoula.hobbymatchmaker.core.design.models.InviteSearchStatus
import com.msoula.hobbymatchmaker.core.design.models.SocialTab
import com.msoula.hobbymatchmaker.core.common.MAX_SOCIAL_CIRCLE_SIZE
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.organisms.ActiveCircleCard
import com.msoula.hobbymatchmaker.core.design.organisms.InviteToCircleDialog
import com.msoula.hobbymatchmaker.core.design.organisms.CircleMembersSection
import com.msoula.hobbymatchmaker.core.design.organisms.ReceivedInvitationSection
import com.msoula.hobbymatchmaker.core.design.organisms.SocialHubHeader
import com.msoula.hobbymatchmaker.core.design.organisms.SocialTabRow
import com.msoula.hobbymatchmaker.core.design.organisms.SentInvitationSection
import com.msoula.hobbymatchmaker.core.design.cancel
import com.msoula.hobbymatchmaker.core.design.social_circle_empty_description
import com.msoula.hobbymatchmaker.core.design.social_circle_empty_title
import com.msoula.hobbymatchmaker.core.design.social_block_dialog_confirm
import com.msoula.hobbymatchmaker.core.design.social_block_dialog_message
import com.msoula.hobbymatchmaker.core.design.social_block_dialog_title
import com.msoula.hobbymatchmaker.core.design.social_remove_dialog_confirm
import com.msoula.hobbymatchmaker.core.design.social_remove_dialog_message
import com.msoula.hobbymatchmaker.core.design.social_remove_dialog_title
import com.msoula.hobbymatchmaker.core.design.social_report_dialog_confirm
import com.msoula.hobbymatchmaker.core.design.social_report_dialog_message
import com.msoula.hobbymatchmaker.core.design.social_report_dialog_title
import com.msoula.hobbymatchmaker.core.design.social_active_circle_full
import com.msoula.hobbymatchmaker.core.design.social_circle_full_dialog_message
import com.msoula.hobbymatchmaker.core.design.social_dialog_ok
import com.msoula.hobbymatchmaker.core.design.social_invite_button
import com.msoula.hobbymatchmaker.core.design.social_decline_dialog_message
import com.msoula.hobbymatchmaker.core.design.social_tab_circle
import com.msoula.hobbymatchmaker.core.design.social_tab_received
import com.msoula.hobbymatchmaker.core.design.social_tab_sent
import com.msoula.hobbymatchmaker.core.design.social_decline_dialog_title
import com.msoula.hobbymatchmaker.core.design.social_received_requests_decline_button_text
import com.msoula.hobbymatchmaker.core.design.social_received_requests_no_data_description
import com.msoula.hobbymatchmaker.core.design.social_received_requests_no_data_title
import com.msoula.hobbymatchmaker.core.design.social_sent_requests_no_data_description
import com.msoula.hobbymatchmaker.core.design.social_sent_requests_no_data_title
import com.msoula.hobbymatchmaker.core.design.templates.SocialLayout
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.features.social.domain.models.ReportReason
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toLabelResource
import com.msoula.hobbymatchmaker.features.social.presentation.models.MemberActionUiModel
import kotlinx.collections.immutable.toImmutableList
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toInviteCandidates
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUserSummaryUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toCircleMembers
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toCircleMembers
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toReceivedInvitations
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toSentInvitations
import com.msoula.hobbymatchmaker.features.social.presentation.models.CircleMemberUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.InviteUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUiEventModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun SocialContent(
    sentInvites: UiState<ImmutableList<InviteUiModel>>,
    incomingInvites: UiState<ImmutableList<InviteUiModel>>,
    circleMembers: ImmutableList<CircleMemberUiModel>,
    busyInviteIds: ImmutableSet<String>,
    searchResults: ImmutableList<SocialUserSummaryUiModel>,
    searchStatus: InviteSearchStatus,
    snackBarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onEvent: (SocialUiEventModel) -> Unit
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(SocialTabIndex.RECEIVED) }
    var inviteIdToDecline by remember { mutableStateOf<String?>(null) }
    var pendingMemberAction by remember { mutableStateOf<MemberActionUiModel?>(null) }
    var isInviteDialogVisible by remember { mutableStateOf(false) }
    var isCircleFullDialogVisible by remember { mutableStateOf(false) }

    // A full circle explains itself instead of hiding the button
    val onInviteClick = {
        if (circleMembers.size >= MAX_SOCIAL_CIRCLE_SIZE) isCircleFullDialogVisible = true else isInviteDialogVisible = true
    }

    if (isInviteDialogVisible) {
        val closeInviteDialog = {
            isInviteDialogVisible = false
            onEvent(SocialUiEventModel.OnSearchPeople(""))
        }
        InviteToCircleDialog(
            candidates = searchResults.toInviteCandidates(),
            searchStatus = searchStatus,
            onSearch = { onEvent(SocialUiEventModel.OnSearchPeople(it)) },
            onDismiss = closeInviteDialog,
            onSendInvite = { pseudo, name ->
                closeInviteDialog()
                onEvent(SocialUiEventModel.OnInviteToSocialCircle(pseudo, name))
            }
        )
    }

    if (isCircleFullDialogVisible) {
        InfoDialog(
            title = stringResource(Res.string.social_active_circle_full),
            message = stringResource(Res.string.social_circle_full_dialog_message, MAX_SOCIAL_CIRCLE_SIZE),
            buttonText = stringResource(Res.string.social_dialog_ok),
            onDismiss = { isCircleFullDialogVisible = false }
        )
    }

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

    pendingMemberAction?.let { action ->
        MemberActionDialog(
            action = action,
            onDismiss = { pendingMemberAction = null },
            onEvent = {
                pendingMemberAction = null
                onEvent(it)
            }
        )
    }

    val receivedCount = incomingInvites.countOrZero()
    val sentCount = sentInvites.countOrZero()
    val tabs = persistentListOf(
        SocialTab(stringResource(Res.string.social_tab_circle), "${circleMembers.size}/$MAX_SOCIAL_CIRCLE_SIZE"),
        SocialTab(stringResource(Res.string.social_tab_received), receivedCount.toString()),
        SocialTab(stringResource(Res.string.social_tab_sent), sentCount.toString())
    )

    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) { paddingValues ->
        SocialLayout(
            paddingValues = paddingValues,
            header = {
                SocialHubHeader(
                    showBackArrow = isIosPlatform(),
                    onBack = onBack,
                    onInviteClick = onInviteClick
                )
            },
            circleCard = if (selectedTabIndex == SocialTabIndex.CIRCLE) null else ({
                ActiveCircleCard(
                    filledSlots = circleMembers.size,
                    maxMembers = MAX_SOCIAL_CIRCLE_SIZE,
                    onManageClick = { selectedTabIndex = SocialTabIndex.CIRCLE }
                )
            }),
            tabRow = {
                SocialTabRow(
                    tabs = tabs,
                    selectedIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
            },
            content = {
                when (selectedTabIndex) {
                    SocialTabIndex.RECEIVED -> ReceivedTabContent(
                        incomingInvites = incomingInvites,
                        busyInviteIds = busyInviteIds,
                        onDeclineClick = { inviteIdToDecline = it },
                        onReportClick = { uid, name -> pendingMemberAction = MemberActionUiModel.Report(uid, name) },
                        onBlockClick = { uid, name -> pendingMemberAction = MemberActionUiModel.Block(uid, name) },
                        onEvent = onEvent
                    )

                    SocialTabIndex.SENT -> SentTabContent(
                        sentInvites = sentInvites,
                        busyInviteIds = busyInviteIds,
                        onEvent = onEvent
                    )

                    else -> CircleTabContent(
                        circleMembers = circleMembers,
                        onRemoveClick = { uid, name -> pendingMemberAction = MemberActionUiModel.Remove(uid, name) },
                        onReportClick = { uid, name -> pendingMemberAction = MemberActionUiModel.Report(uid, name) },
                        onBlockClick = { uid, name -> pendingMemberAction = MemberActionUiModel.Block(uid, name) },
                        onInviteClick = onInviteClick
                    )
                }
            }
        )
    }
}

private object SocialTabIndex {
    const val CIRCLE = 0
    const val RECEIVED = 1
    const val SENT = 2
}

private fun UiState<ImmutableList<InviteUiModel>>.countOrZero(): Int =
    (this as? UiState.Success)?.data?.size ?: 0

@Composable
internal fun CircleTabContent(
    circleMembers: ImmutableList<CircleMemberUiModel>,
    onRemoveClick: (memberUid: String, displayName: String) -> Unit,
    onReportClick: (memberUid: String, displayName: String) -> Unit,
    onBlockClick: (memberUid: String, displayName: String) -> Unit,
    onInviteClick: () -> Unit
) {
    if (circleMembers.isEmpty()) {
        EmptyStateScreen(
            config = EmptyStateConfig(
                icon = FontAwesomeUserFriends,
                title = UIText.Resource(Res.string.social_circle_empty_title),
                description = UIText.Resource(Res.string.social_circle_empty_description),
                ctaText = UIText.Resource(Res.string.social_invite_button),
                ctaAction = onInviteClick
            )
        )
    } else {
        CircleMembersSection(
            members = circleMembers.toCircleMembers(),
            maxMembers = MAX_SOCIAL_CIRCLE_SIZE,
            onRemoveClick = onRemoveClick,
            onReportClick = onReportClick,
            onBlockClick = onBlockClick,
            onInviteClick = onInviteClick
        )
    }
}

@Composable
private fun ReceivedTabContent(
    incomingInvites: UiState<ImmutableList<InviteUiModel>>,
    busyInviteIds: ImmutableSet<String>,
    onDeclineClick: (String) -> Unit,
    onReportClick: (guestUid: String, displayName: String) -> Unit,
    onBlockClick: (guestUid: String, displayName: String) -> Unit,
    onEvent: (SocialUiEventModel) -> Unit
) {
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
                    onEvent(SocialUiEventModel.OnAcceptInvitation(id, guestUid))
                },
                onDeclineInvitationClick = onDeclineClick,
                onReportClick = onReportClick,
                onBlockClick = onBlockClick
            )
        }
    )
}

@Composable
private fun SentTabContent(
    sentInvites: UiState<ImmutableList<InviteUiModel>>,
    busyInviteIds: ImmutableSet<String>,
    onEvent: (SocialUiEventModel) -> Unit
) {
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
                onCancelInvitationClick = { id -> onEvent(SocialUiEventModel.OnCancelInvitation(id)) },
                onDismissInvitationClick = { id -> onEvent(SocialUiEventModel.OnDismissDeclinedInvitation(id)) }
            )
        }
    )
}

@Composable
internal fun MemberActionDialog(
    action: MemberActionUiModel,
    onDismiss: () -> Unit,
    onEvent: (SocialUiEventModel) -> Unit
) {
    when (action) {
        is MemberActionUiModel.Remove -> DestructiveConfirmationDialog(
            title = stringResource(Res.string.social_remove_dialog_title, action.displayName),
            message = stringResource(Res.string.social_remove_dialog_message),
            confirmButtonText = stringResource(Res.string.social_remove_dialog_confirm),
            cancelButtonText = stringResource(Res.string.cancel),
            isLoading = false,
            onDismiss = onDismiss,
            onConfirm = { onEvent(SocialUiEventModel.OnRemoveMember(action.uid)) }
        )

        is MemberActionUiModel.Block -> DestructiveConfirmationDialog(
            title = stringResource(Res.string.social_block_dialog_title, action.displayName),
            message = stringResource(Res.string.social_block_dialog_message),
            confirmButtonText = stringResource(Res.string.social_block_dialog_confirm),
            cancelButtonText = stringResource(Res.string.cancel),
            isLoading = false,
            onDismiss = onDismiss,
            onConfirm = { onEvent(SocialUiEventModel.OnBlockUser(action.uid)) }
        )

        is MemberActionUiModel.Report -> {
            val reasons = ReportReason.entries
            ReportReasonDialog(
                title = stringResource(Res.string.social_report_dialog_title, action.displayName),
                message = stringResource(Res.string.social_report_dialog_message),
                reasons = reasons.map { stringResource(it.toLabelResource()) }.toImmutableList(),
                confirmButtonText = stringResource(Res.string.social_report_dialog_confirm),
                cancelButtonText = stringResource(Res.string.cancel),
                onDismiss = onDismiss,
                onConfirm = { index -> onEvent(SocialUiEventModel.OnReportUser(action.uid, reasons[index])) }
            )
        }
    }
}
