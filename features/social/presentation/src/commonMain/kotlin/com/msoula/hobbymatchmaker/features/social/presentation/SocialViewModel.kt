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
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveSessionStateUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveIncomingInvitesSuccess
import com.msoula.hobbymatchmaker.features.social.domain.useCases.ObserveSentInvitesSuccess
import com.msoula.hobbymatchmaker.features.social.presentation.interactors.SocialInteractor
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toInviteUiModel
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class SocialViewModel(
    private val interactor: SocialInteractor,
    private val observeSessionStateUseCase: ObserveSessionStateUseCase,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    private val scope = externalScope ?: viewModelScope
    private val eventHandler = EventHandler()
    val events = eventHandler.events

    private var currentUserUid: String? = null

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
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
    fun observeSessionAndInvites() {
        // Sent invitations
        scope.launch {
            observeSessionStateUseCase()
                .flatMapLatest { state ->
                    when (state) {
                        is SessionState.Authenticated -> {
                            currentUserUid = state.uid
                            interactor.observeSentInvites(state.uid)
                        }

                        is SessionState.Guest -> {
                            currentUserUid = null
                            flowOf(AppResult.Success(ObserveSentInvitesSuccess.Empty))
                        }

                        null -> {
                            currentUserUid = null
                            flowOf(AppResult.Success(ObserveSentInvitesSuccess.Empty))
                        }
                    }
                }
                .collect { result ->
                    _sentInvites.update {
                        when (result) {
                            is AppResult.Success -> {
                                Logger.d("Updating sentInvites with: ${result.data}")
                                when (val data = result.data) {
                                    is ObserveSentInvitesSuccess.Empty -> UiState.Empty
                                    is ObserveSentInvitesSuccess.Success -> UiState.Success(
                                        data.invites.map { invite -> invite.toInviteUiModel() }
                                            .toImmutableList()
                                    )
                                }
                            }

                            is AppResult.Failure -> UiState.Error(
                                defaultMessageMapper.toUIText(result.error)
                            )
                        }
                    }
                }
        }

        // Received invitations
        scope.launch {
            observeSessionStateUseCase()
                .flatMapLatest { state ->
                    when (state) {
                        is SessionState.Authenticated -> {
                            interactor.observeIncomingInvites(state.uid)
                        }

                        is SessionState.Guest -> {
                            flowOf(AppResult.Success(ObserveIncomingInvitesSuccess.Empty))
                        }

                        null -> {
                            flowOf(AppResult.Success(ObserveIncomingInvitesSuccess.Empty))
                        }
                    }
                }
                .collect { result ->
                    _incomingInvites.update {
                        when (result) {
                            is AppResult.Success -> {
                                Logger.d("Updating incomingInvites with: ${result.data}")
                                when (val data = result.data) {
                                    is ObserveIncomingInvitesSuccess.Empty -> UiState.Empty
                                    is ObserveIncomingInvitesSuccess.Success -> UiState.Success(
                                        data.invites.map { invite -> invite.toInviteUiModel() }
                                            .toImmutableList()
                                    )
                                }
                            }

                            is AppResult.Failure -> UiState.Error(
                                defaultMessageMapper.toUIText(result.error)
                            )
                        }
                    }
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
        }
    }

    private suspend fun searchUsers(pseudo: String) {
        interactor.searchUsers(pseudo, currentUserUid)
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

        interactor.sendInvite(uid, pseudo, name)
            .onSuccess {
                eventHandler.sendEvent(UiEvent.OnDataReady("invitation_sent"))
            }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private suspend fun cancelInvitation(inviteId: String) {
        interactor.cancelInvite(inviteId)
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
        interactor.declineInvite(inviteId)
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
            interactor.acceptInvite(
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
        } ?: eventHandler.sendEvent(
            UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(AppError.Domain.Unauthorized))
        )
    }
}
