package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveSessionStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
import com.msoula.hobbymatchmaker.features.profile.presentation.interactors.UserProfileInteractor
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toUserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toUserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModel(
    private val interactor: UserProfileInteractor,
    private val observeCurrentUserProfileStateUseCase: ObserveCurrentUserProfileStateUseCase,
    private val observeSessionStateUseCase: ObserveSessionStateUseCase,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope

    private val eventHandler = EventHandler()
    val events = eventHandler.events

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    private var currentUserUid: String? = null

    private val _editableProfile = MutableStateFlow<UserProfileUiModel?>(null)
    val editableProfile = _editableProfile.asStateFlow()

    private val _originalProfile = MutableStateFlow<UserProfileUiModel?>(null)

    private val _isPseudoAvailable = MutableStateFlow<Boolean?>(null)
    val isPseudoAvailable = _isPseudoAvailable.asStateFlow()

    private val _enableSave = MutableStateFlow<Boolean?>(true)
    val enableSave = _enableSave.asStateFlow()

    private val _screenState =
        MutableStateFlow<UserProfileUiStateModel>(UserProfileUiStateModel.Loading)
    val screenState = _screenState.asStateFlow()

    init {
        observeProfile()
    }

    fun observeProfile() {
        scope.launch {
            observeSessionStateUseCase().collect { state ->
                when (state) {
                    is SessionState.Authenticated -> {
                        Logger.d("UserProfileViewModel", "Authenticated session")
                        currentUserUid = state.uid

                        observeCurrentUserProfileStateUseCase(state.uid).collect { profile ->
                            val uiModel = profile.toUserProfileUiModel()

                            if (!_isEditMode.value) {
                                _originalProfile.update { uiModel }
                                _editableProfile.update { uiModel }
                            }

                            Logger.d("UserProfileViewModel", "Success with uid: $currentUserUid")
                            _screenState.update { UserProfileUiStateModel.Success(uiModel) }
                        }
                    }

                    is SessionState.Guest -> {
                        Logger.d("UserProfileViewModel", "Guest session")
                        _screenState.update { UserProfileUiStateModel.Guest }
                    }

                    null -> {
                        Logger.d("UserProfileViewModel", "No session found (null state)")
                        _screenState.update { UserProfileUiStateModel.Guest }
                    }
                }
            }
        }
    }

    fun onEvent(event: UserProfileUiEventModel) {
        when (event) {
            is UserProfileUiEventModel.OnBioChanged ->
                _editableProfile.update { current -> current?.copy(bio = event.value) }

            is UserProfileUiEventModel.OnNameChanged ->
                _editableProfile.update { current -> current?.copy(name = event.value) }

            is UserProfileUiEventModel.OnInterestsChanged -> {
                _editableProfile.update { current -> current?.copy(interests = event.value) }
            }

            is UserProfileUiEventModel.OnAvatarSelected -> {
                onAvatarSelected(event.path)
            }

            is UserProfileUiEventModel.OnPseudoChanged -> {
                _editableProfile.update { current -> current?.copy(pseudo = event.value) }
                _isPseudoAvailable.update { null }
            }

            UserProfileUiEventModel.OnPseudoDefined -> {
                _editableProfile.value?.let {
                    scope.launch {
                        if (it.pseudo != _originalProfile.value?.pseudo)
                            interactor.checkPseudoAvailable(it.pseudo)
                                .onSuccess { available ->
                                    if (!available) {
                                        _enableSave.update { false }
                                    }

                                    _isPseudoAvailable.update { available }
                                }
                                .onFailure { error ->
                                    _isPseudoAvailable.update { null }
                                    eventHandler.sendEvent(
                                        UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                                    )
                                }
                    }
                }
            }

            UserProfileUiEventModel.OnSaveClicked -> saveProfile()

            UserProfileUiEventModel.OnEditModeClicked -> toggleEditMode()

            UserProfileUiEventModel.OnSignUpButtonClicked -> logOut()
        }
    }

    private fun toggleEditMode() {
        _editableProfile.update { current -> current }
        _isEditMode.update { true }
    }

    private fun saveProfile() {
        scope.launch {
            val uid = currentUserUid ?: return@launch
            val editable = _editableProfile.value ?: return@launch

            val profile = editable.toUserProfileDomainModel(uid)

            interactor.saveProfile(uid, profile)
                .onSuccess {
                    eventHandler.sendEvent(
                        UiEvent.OnDataReady("profile_updated")
                    )

                    launch {
                        interactor.syncProfile(profile)
                            .onFailure {
                                Logger.e("Failed to sync profile remotely - ${profile.uid}")
                            }
                    }
                }
                .onFailure { error ->
                    eventHandler.sendEvent(
                        UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                    )
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }

    private fun logOut() {
        scope.launch {
            interactor.logOut()
                .onSuccess {
                    eventHandler.sendEvent(
                        UiEvent.NavigateToRoute("sign_up")
                    )
                }
                .onFailure { error ->
                    eventHandler.sendEvent(
                        UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                    )
                }
        }
    }

    fun closeEdition() {
        _editableProfile.update { _originalProfile.value }
        _isEditMode.update { false }
        _isPseudoAvailable.update { null }
    }

    @OptIn(ExperimentalTime::class)
    private fun onAvatarSelected(avatarPath: String) {
        scope.launch {
            val uid = currentUserUid ?: return@launch
            val old = _editableProfile.value?.avatarUrl

            interactor.saveAvatar(uid, old, avatarPath)
                .onSuccess { newPath ->
                    _editableProfile.update { it?.copy(avatarUrl = newPath) }
                }
                .onFailure { error ->
                    eventHandler.sendEvent(
                        UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                    )
                }
        }
    }
}
