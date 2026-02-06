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
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
import com.msoula.hobbymatchmaker.features.social.domain.models.InviteStatus
import com.msoula.hobbymatchmaker.features.social.domain.models.SocialInviteDomainModel
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

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

    private var currentUserUid: String? = null
    private var currentUserPseudo: String? = null

    private val _searchResults = MutableStateFlow<ImmutableList<SocialUserSummaryUiModel>>(
        persistentListOf()
    )
    val searchResults = _searchResults.asStateFlow()

    private val _sentInvites =
        MutableStateFlow<UiState<ImmutableList<InviteUiModel>>>(UiState.Loading)
    val sentInvites = _sentInvites.asStateFlow()

    private val _incomingInvites =
        MutableStateFlow<UiState<ImmutableList<InviteUiModel>>>(UiState.Loading)
    val incomingInvites = _incomingInvites.asStateFlow()

    init {
        observeSessionAndInvites()
        refreshInvitesWhenAuthenticated()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeSessionAndInvites() {
        // Sent invitations
        scope.launch {
            observeWhenAuthenticated(
                emptyResult = ObserveSentInvitesSuccess.Empty,
                observe = { uid ->
                    currentUserUid = uid
                    socialUseCases.observeSentInvitesUseCase(uid)
                }
            ).collect { result ->
                _sentInvites.update { result.toInvitesUiState() }
            }
        }

        // Incoming invitations
        scope.launch {
            observeSessionStateUseCase()
                .flatMapLatest { state ->
                    when (state) {
                        is SessionState.Authenticated -> observeCurrentUser(state.uid)
                        is SessionState.Guest, null -> flowOf(null)
                    }
                }
                .flatMapLatest { profile ->
                    currentUserPseudo = profile?.pseudo
                    val pseudo = profile?.pseudo
                    if (pseudo.isNullOrEmpty()) {
                        flowOf(AppResult.Success(ObserveIncomingInvitesSuccess.Empty))
                    } else {
                        socialUseCases.observeIncomingInvitesUseCase(pseudo)
                    }
                }
                .collect { result ->
                    _incomingInvites.update { result.toInvitesUiState() }
                }
        }
    }

    private fun refreshInvitesWhenAuthenticated() {
        scope.launch {
            observeSessionStateUseCase().collect { state ->
                when (state) {
                    is SessionState.Authenticated -> {
                        val uid = state.uid
                        socialUseCases.refreshIncomingInvitesUseCase(uid)
                            .onFailure {
                                Logger.e("Failed to refresh incoming invites: $it")
                            }
                        socialUseCases.refreshSentInvitesUseCase(uid)
                            .onFailure {
                                Logger.e("Failed to refresh sent invites: $it")
                            }
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

                else -> UiState.Empty
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
        }
    }

    private suspend fun searchUsers(pseudo: String) {
        socialUseCases.searchUsersByPseudoUseCase(pseudo, currentUserUid)
            .onSuccess { list ->
                _searchResults.update { list.map { it.toSocialSummaryUiModel() }.toImmutableList() }
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

        socialUseCases.sendInvitesUseCase(
            SocialInviteDomainModel(
                inviteId = "",
                fromUid = uid,
                fromPseudo = currentUserPseudo ?: "",
                toPseudo = pseudo,
                name = name,
                status = InviteStatus.PENDING,
                createdAt = Clock.System.now(),
                updatedAt = null
            )
        )
            .onSuccess {
                socialUseCases.refreshSentInvitesUseCase(uid)
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
            }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private suspend fun acceptInvitation(inviteId: String, guestUid: String) {
        currentUserUid?.let { ownerUid ->
            socialUseCases.checkSocialCircleLimitUseCase(ownerUid, guestUid)
                .onSuccess { slotAvailable ->
                    if (slotAvailable) {
                        socialUseCases.acceptInviteUseCase(
                            inviteId,
                            ownerUid,
                            guestUid
                        )
                            .onSuccess {
                                Logger.d("Invitation $inviteId accept successfully")
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
        } ?: eventHandler.sendEvent(
            UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(AppError.Domain.Unauthorized))
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> observeWhenAuthenticated(
        emptyResult: T,
        observe: (uid: String) -> Flow<AppResult<T, AppError>>
    ): Flow<AppResult<T, AppError>> =
        observeSessionStateUseCase()
            .flatMapLatest { state ->
                when (state) {
                    is SessionState.Authenticated -> {
                        observe(state.uid)
                    }

                    is SessionState.Guest, null ->
                        flowOf(AppResult.Success(emptyResult))
                }
            }
}
