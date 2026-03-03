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
import com.msoula.hobbymatchmaker.features.social.presentation.models.InviteUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUiEventModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUserSummaryUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    val searchResults: StateFlow<ImmutableList<SocialUserSummaryUiModel>>
        field = MutableStateFlow<ImmutableList<SocialUserSummaryUiModel>>(persistentListOf())

    val sentInvites: StateFlow<UiState<ImmutableList<InviteUiModel>>>
        field = MutableStateFlow<UiState<ImmutableList<InviteUiModel>>>(UiState.Loading)

    val incomingInvites: StateFlow<UiState<ImmutableList<InviteUiModel>>>
        field = MutableStateFlow<UiState<ImmutableList<InviteUiModel>>>(UiState.Loading)

    init {
        observeSessionAndInvites()
        refreshInvitesWhenAuthenticated()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSessionAndInvites() {
        // Sent invitations
        scope.launch {
            sessionState
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
            currentUserProfile
                .flatMapLatest { profile ->
                    val pseudo = profile?.pseudo
                    if (pseudo.isNullOrEmpty()) {
                        flowOf(AppResult.Success(ObserveIncomingInvitesSuccess.Empty))
                    } else {
                        socialUseCases.observeIncomingInvitesUseCase(pseudo)
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
                    Logger.e("Unhandled invite result type: ${data::class.simpleName}")
                    UiState.Empty
                }
            }
        }

    fun onEvent(event: SocialUiEventModel) {
        when (event) {
            is SocialUiEventModel.OnSearchPeople -> {
                scope.launch {
                    searchUsers(event.value)
                }
            }

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

            is SocialUiEventModel.OnRetrySocialCircle ->
                scope.launch {
                    currentUserUid?.let { uid ->
                        socialUseCases.refreshIncomingInvitesUseCase(uid)
                        socialUseCases.refreshSentInvitesUseCase(uid)
                    }
                }
        }
    }

    private suspend fun searchUsers(pseudo: String) {
        socialUseCases.searchUsersByPseudoUseCase(pseudo, currentUserUid)
            .onSuccess { list ->
                searchResults.update { list.map { it.toSocialSummaryUiModel() }.toImmutableList() }
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
            eventHandler.sendEvent(
                UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(AppError.Domain.Unauthorized))
            )
            return
        }

        socialUseCases.sendInvitesUseCase(uid, currentUserPseudo, pseudo, name)
            .onSuccess {
                socialUseCases.refreshSentInvitesUseCase(uid)
                    .onFailure {
                        Logger.e("Failed to refresh sent invites after send: $it")
                    }
                eventHandler.sendEvent(UiEvent.OnDataReady("invitation_sent"))
            }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private suspend fun cancelInvitation(inviteId: String) {
        socialUseCases.cancelInvitationUseCase(inviteId)
            .onSuccess {
                Logger.d("Invitation $inviteId canceled successfully")
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

    private suspend fun declineInvitation(inviteId: String) {
        socialUseCases.declineInviteUseCase(inviteId)
            .onSuccess {
                Logger.d("Invitation $inviteId declined successfully")
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

    private suspend fun acceptInvitation(inviteId: String, guestUid: String) {
        val ownerUid = currentUserUid ?: run {
            eventHandler.sendEvent(
                UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(AppError.Domain.Unauthorized))
            )
            return
        }

        socialUseCases.checkSocialCircleLimitUseCase(ownerUid, guestUid)
            .onSuccess { slotAvailable ->
                if (slotAvailable) {
                    socialUseCases.acceptInviteUseCase(inviteId, ownerUid, guestUid)
                        .onSuccess {
                            Logger.d("Invitation $inviteId accept successfully")
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
}
