package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.NavigationDestination
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.features.profile.presentation.interactors.SessionInteractor
import com.msoula.hobbymatchmaker.features.profile.presentation.interactors.UserProfileInteractor
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toUserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModel(
    private val userProfileInteractor: UserProfileInteractor,
    private val sessionInteractor: SessionInteractor,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope

    private val eventHandler = EventHandler()
    val events = eventHandler.events

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    private val _editableProfile = MutableStateFlow<UserProfileUiModel?>(null)

    val editableProfile = _editableProfile.asStateFlow()
    private val _originalProfile = MutableStateFlow<UserProfileUiModel?>(null)

    private val _isPseudoAvailable = MutableStateFlow<Boolean?>(null)

    val isPseudoAvailable = _isPseudoAvailable.asStateFlow()

    private val _screenState =
        MutableStateFlow<UserProfileUiStateModel>(UserProfileUiStateModel.Loading)

    val screenState = _screenState.asStateFlow()

    private val currentUserUid: StateFlow<String?> = sessionInteractor.observeSessionState()
        .map { state ->
            when (state) {
                is SessionState.Authenticated -> {
                    state.uid
                }

                else -> null
            }
        }
        .stateIn(scope, SharingStarted.Eagerly, null)

    init {
        observeProfile()
        refreshProfileData()
    }

    private fun observeProfile() {
        scope.launch {
            sessionInteractor.observeSessionState()
                .flatMapLatest { state ->
                    when (state) {
                        is SessionState.Authenticated -> userProfileInteractor.observeCurrentUser(
                            state.uid
                        )

                        else -> flowOf(null)
                    }
                }
                .collect { profile ->
                    profile?.let {
                        val uiModel = it.toUserProfileUiModel()
                        _screenState.update { UserProfileUiStateModel.Success(uiModel) }

                        if (!_isEditMode.value) {
                            _editableProfile.update { uiModel }
                        }
                    }
                }
        }
    }

    private fun refreshProfileData() {
        scope.launch {
            currentUserUid.value?.let { uid ->
                userProfileInteractor.refreshProfileData(uid)
                    .onFailure { error ->
                        Logger.e("Failed to refresh profile data: $error")
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
                            userProfileInteractor.checkPseudoAvailable(it.pseudo)
                                .onSuccess { available ->
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
        _originalProfile.update { _editableProfile.value }
        _isEditMode.update { true }
    }

    private fun saveProfile() {
        scope.launch {
            val editable = _editableProfile.value ?: return@launch
            val uid = currentUserUid.value ?: return@launch

            userProfileInteractor.saveProfile(uid, editable)
                .onSuccess {
                    _originalProfile.update { editable }

                    eventHandler.sendEvent(
                        UiEvent.OnDataReady("profile_updated")
                    )

                    scope.launch {
                        userProfileInteractor.syncProfile(uid, editable)
                            .onFailure {
                                Logger.e("Failed to sync profile remotely - $uid")
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
            userProfileInteractor.logOut()
                .onSuccess {
                    eventHandler.sendEvent(
                        UiEvent.Navigate(NavigationDestination.SignIn)
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
            val uid = currentUserUid.value
            val old = _editableProfile.value?.avatarUrl

            uid?.let { safeUid ->
                userProfileInteractor.saveAvatar(safeUid, old, avatarPath)
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
}
