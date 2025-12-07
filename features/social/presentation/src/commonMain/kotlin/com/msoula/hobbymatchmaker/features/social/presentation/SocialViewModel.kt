package com.msoula.hobbymatchmaker.features.social.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveSessionStateUseCase
import com.msoula.hobbymatchmaker.features.social.presentation.interactors.SocialInteractor
import com.msoula.hobbymatchmaker.features.social.presentation.mappers.toSocialSummaryUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.InviteUiModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUiEventModel
import com.msoula.hobbymatchmaker.features.social.presentation.models.SocialUserSummaryUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private val _searchResults = MutableStateFlow<List<SocialUserSummaryUiModel>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _sentInvites: MutableStateFlow<List<InviteUiModel>> = MutableStateFlow(emptyList())
    val sentInvites = _sentInvites.asStateFlow()

    private val _incomingInvites: MutableStateFlow<List<InviteUiModel>> =
        MutableStateFlow(emptyList())
    val incomingInvites = _incomingInvites.asStateFlow()

    init {
        observeSession()
    }

    private fun observeSession() {
        scope.launch {
            observeSessionStateUseCase().collect { state ->
                when (state) {
                    is SessionState.Authenticated -> currentUserUid = state.uid
                    is SessionState.Guest -> currentUserUid = null
                    null -> {
                        Logger.d("SocialViewModel", "No session found")
                        currentUserUid = null
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
                    inviteToSocialCircle(event.value)
                }
            }

            is SocialUiEventModel.OnAcceptInvitation -> {}

            is SocialUiEventModel.OnDeclineInvitation -> {}

            is SocialUiEventModel.OnCancelInvitation -> {}
        }
    }

    private suspend fun searchUsers(pseudo: String) {
        interactor.searchUsers(pseudo, currentUserUid)
            .onSuccess { list ->
                _searchResults.update { list.map { it.toSocialSummaryUiModel() } }
            }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private suspend fun inviteToSocialCircle(pseudo: String) {
        val uid = currentUserUid
        if (uid == null) {
            eventHandler.sendEvent(
                UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(AppError.Domain.Unauthorized))
            )
            return
        }

        interactor.sendInvite(uid, pseudo)
            .onSuccess {
                eventHandler.sendEvent(UiEvent.OnDataReady("invitation_sent"))
            }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }
}
