package com.msoula.hobbymatchmaker.features.social.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.social_invite_accepted_success
import com.msoula.hobbymatchmaker.core.design.social_invite_canceled_success
import com.msoula.hobbymatchmaker.core.design.social_invite_declined_success
import com.msoula.hobbymatchmaker.core.design.social_invite_sent_success
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.core.design.util.toUIState
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveSessionStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.models.UserProfileNoCircleDomainModel
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveIncomingInvitesSuccess
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveSentInvitesSuccess
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SocialUseCases
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toIncomingInviteUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toSentInviteUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toSocialSummaryUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.InviteStatusUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.InviteUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUiEventModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUserSummaryUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

private const val SEARCH_DEBOUNCE_MILLIS = 300L

@OptIn(ExperimentalCoroutinesApi::class)
class SocialViewModel(
    private val observeSessionStateUseCase: ObserveSessionStateUseCase,
    private val observeCurrentUser: ObserveCurrentUserProfileStateUseCase,
    private val socialUseCases: SocialUseCases,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    private val scope = externalScope ?: viewModelScope
    private val eventHandler = EventHandler()
    val events = eventHandler.events

    private val sessionState: StateFlow<SessionState?> = observeSessionStateUseCase()
        .stateIn(scope, SharingStarted.Eagerly, null)

    private val currentUserProfile: StateFlow<UserProfileNoCircleDomainModel?> = sessionState
        .flatMapLatest { state ->
            when (state) {
                is SessionState.Authenticated -> observeCurrentUser(state.uid)
                else -> flowOf(null)
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, null)

    private val currentUserUid: String?
        get() = (sessionState.value as? SessionState.Authenticated)?.uid

    private val currentUserPseudo: String?
        get() = currentUserProfile.value?.pseudo

    private val foundUsers = MutableStateFlow<ImmutableList<SocialUserSummaryUiModel>>(persistentListOf())
    private var searchJob: Job? = null

    val sentInvites: StateFlow<UiState<ImmutableList<InviteUiModel>>>
        field = MutableStateFlow<UiState<ImmutableList<InviteUiModel>>>(UiState.Loading)

    // Users that already have a pending invite from the current user are flagged, not hidden
    val searchResults: StateFlow<ImmutableList<SocialUserSummaryUiModel>> =
        combine(foundUsers, sentInvites) { users, sent ->
            val pendingPseudos = (sent as? UiState.Success)?.data.orEmpty()
                .filter { it.inviteStatus == InviteStatusUiModel.PENDING }
                .map { it.guestPseudo.lowercase() }
                .toSet()

            users.map { it.copy(isInvited = it.pseudo.lowercase() in pendingPseudos) }.toImmutableList()
        }.stateIn(scope, SharingStarted.Eagerly, persistentListOf())

    val incomingInvites: StateFlow<UiState<ImmutableList<InviteUiModel>>>
        field = MutableStateFlow<UiState<ImmutableList<InviteUiModel>>>(UiState.Loading)

    val busyInviteIds: StateFlow<ImmutableSet<String>>
        field = MutableStateFlow<ImmutableSet<String>>(persistentSetOf())

    private val retryTrigger = MutableStateFlow(0)

    init {
        observeSessionAndInvites()
        refreshInvitesWhenAuthenticated()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSessionAndInvites() {
        // Sent invitations
        scope.launch {
            combine(sessionState, retryTrigger) { state, _ -> state }
                .flatMapLatest { state ->
                    when (state) {
                        is SessionState.Authenticated ->
                            socialUseCases.observeSentInvitesUseCase(state.uid)

                        else -> flowOf(AppResult.Success(ObserveSentInvitesSuccess.Empty))
                    }
                }
                .collect { result ->
                    sentInvites.update { result.toInvitesUiState() }
                }
        }

        // Incoming invitations
        scope.launch {
            combine(currentUserProfile, retryTrigger) { profile, _ -> profile }
                .flatMapLatest { profile ->
                    val pseudo = profile?.pseudo
                    val uid = currentUserUid
                    if (pseudo.isNullOrEmpty() || uid == null) {
                        flowOf(AppResult.Success(ObserveIncomingInvitesSuccess.Empty))
                    } else {
                        socialUseCases.observeIncomingInvitesUseCase(uid, pseudo)
                    }
                }
                .collect { result ->
                    incomingInvites.update { result.toInvitesUiState() }
                }
        }
    }

    private fun refreshInvitesWhenAuthenticated() {
        scope.launch {
            sessionState
                .collect { state ->
                    when (state) {
                        is SessionState.Authenticated -> {
                            val uid = state.uid
                            socialUseCases.refreshIncomingInvitesUseCase(uid)
                                .onFailure { Logger.e("Failed to refresh incoming invites: $it") }
                            socialUseCases.refreshSentInvitesUseCase(uid)
                                .onFailure { Logger.e("Failed to refresh sent invites: $it") }
                        }

                        else -> {}
                    }
                }
        }
    }

    private inline fun <reified T> AppResult<T, AppError>.toInvitesUiState(): UiState<ImmutableList<InviteUiModel>> =
        toUIState(defaultMessageMapper) { data ->
            when (data) {
                is ObserveSentInvitesSuccess.Empty,
                is ObserveIncomingInvitesSuccess.Empty -> UiState.Empty

                is ObserveSentInvitesSuccess.Success -> UiState.Success(
                    data.invites.map { it.toSentInviteUiModel() }.toImmutableList()
                )

                is ObserveIncomingInvitesSuccess.Success -> UiState.Success(
                    data.invites.map { it.toIncomingInviteUiModel() }.toImmutableList()
                )

                else -> {
                    val message = data?.let { it::class.simpleName } ?: "Uncoded message"
                    Logger.e("Unhandled invite result type: $message")
                    UiState.Empty
                }
            }
        }

    fun onEvent(event: SocialUiEventModel) {
        when (event) {
            is SocialUiEventModel.OnSearchPeople -> searchPeople(event.value)

            is SocialUiEventModel.OnInviteToSocialCircle -> {
                scope.launch {
                    inviteToSocialCircle(event.pseudo, event.name)
                }
            }

            is SocialUiEventModel.OnAcceptInvitation -> {
                scope.launch {
                    acceptInvitation(event.inviteId, event.guestUid)
                }
            }

            is SocialUiEventModel.OnDeclineInvitation -> {
                scope.launch {
                    declineInvitation(event.inviteId)
                }
            }

            is SocialUiEventModel.OnCancelInvitation -> {
                scope.launch {
                    cancelInvitation(event.inviteId)
                }
            }

            is SocialUiEventModel.OnRetrySocialCircle -> {
                sentInvites.update { UiState.Loading }
                incomingInvites.update { UiState.Loading }
                retryTrigger.update { it + 1 }
                scope.launch {
                    currentUserUid?.let { uid ->
                        socialUseCases.refreshIncomingInvitesUseCase(uid)
                            .onFailure { Logger.e("Retry: failed to refresh incoming invites: $it") }
                        socialUseCases.refreshSentInvitesUseCase(uid)
                            .onFailure { Logger.e("Retry: failed to refresh sent invites: $it") }
                    }
                }
            }
        }
    }

    // Typing fires one event per character: only the last query is sent, older ones are dropped
    private fun searchPeople(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            foundUsers.update { persistentListOf() }
            return
        }

        searchJob = scope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            searchUsers(query)
        }
    }

    private suspend fun searchUsers(pseudo: String) {
        socialUseCases.searchUsersByPseudoUseCase(pseudo, currentUserUid)
            .onSuccess { list ->
                foundUsers.update { list.map { it.toSocialSummaryUiModel() }.toImmutableList() }
            }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private suspend fun inviteToSocialCircle(pseudo: String, name: String?) {
        val uid = currentUserUid
        if (uid == null) {
            Logger.e("inviteToSocialCircle: currentUserUid is null, aborting")
            eventHandler.sendEvent(
                UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(AppError.Domain.Unauthorized))
            )
            return
        }

        Logger.d("inviteToSocialCircle: sending invite to pseudo=$pseudo, uid=$uid, ownerPseudo=$currentUserPseudo")

        socialUseCases.sendInvitesUseCase(uid, currentUserPseudo, pseudo, name)
            .onSuccess {
                Logger.d("inviteToSocialCircle: invite sent successfully to $pseudo")
                socialUseCases.refreshSentInvitesUseCase(uid)
                    .onFailure {
                        Logger.e("Failed to refresh sent invites after send: $it")
                    }
                eventHandler.sendEvent(successSnackBar(Res.string.social_invite_sent_success))
            }
            .onFailure { error ->
                Logger.e("inviteToSocialCircle: failed to send invite to $pseudo — $error")
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private suspend fun cancelInvitation(inviteId: String) = runExclusive(inviteId) {
        socialUseCases.cancelInvitationUseCase(inviteId)
            .onSuccess {
                Logger.d("Invitation $inviteId canceled successfully")
                eventHandler.sendEvent(successSnackBar(Res.string.social_invite_canceled_success))
                currentUserUid?.let { uid ->
                    socialUseCases.refreshSentInvitesUseCase(uid)
                        .onFailure {
                            Logger.e("Failed to refresh sent invites after cancel: $it")
                        }
                }
            }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private suspend fun declineInvitation(inviteId: String) = runExclusive(inviteId) {
        socialUseCases.declineInviteUseCase(inviteId)
            .onSuccess {
                Logger.d("Invitation $inviteId declined successfully")
                eventHandler.sendEvent(successSnackBar(Res.string.social_invite_declined_success))
                currentUserUid?.let { uid ->
                    socialUseCases.refreshIncomingInvitesUseCase(uid)
                        .onFailure {
                            Logger.e("Failed to refresh incoming invites after decline: $it")
                        }
                }
            }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private suspend fun acceptInvitation(inviteId: String, guestUid: String) = runExclusive(inviteId) {
        val ownerUid = currentUserUid ?: run {
            eventHandler.sendEvent(
                UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(AppError.Domain.Unauthorized))
            )
            return@runExclusive
        }

        socialUseCases.checkSocialCircleLimitUseCase(ownerUid, guestUid)
            .onSuccess { slotAvailable ->
                if (slotAvailable) {
                    socialUseCases.acceptInviteUseCase(inviteId, ownerUid, guestUid)
                        .onSuccess {
                            Logger.d("Invitation $inviteId accept successfully")
                            eventHandler.sendEvent(successSnackBar(Res.string.social_invite_accepted_success))
                            socialUseCases.refreshIncomingInvitesUseCase(ownerUid)
                                .onFailure {
                                    Logger.e("Failed to refresh incoming invites after accept: $it")
                                }
                        }
                        .onFailure { error ->
                            eventHandler.sendEvent(
                                UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                            )
                        }
                } else {
                    eventHandler.sendEvent(UiEvent.CapacityReached)
                }
            }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private fun successSnackBar(message: StringResource) = UiEvent.ShowSnackBar(UIText.Resource(message))

    // One action at a time per invite: a second tap while the first is running is ignored
    private suspend fun runExclusive(inviteId: String, block: suspend () -> Unit) {
        if (inviteId in busyInviteIds.value) return

        busyInviteIds.update { (it + inviteId).toImmutableSet() }
        try {
            block()
        } finally {
            busyInviteIds.update { (it - inviteId).toImmutableSet() }
        }
    }
}
