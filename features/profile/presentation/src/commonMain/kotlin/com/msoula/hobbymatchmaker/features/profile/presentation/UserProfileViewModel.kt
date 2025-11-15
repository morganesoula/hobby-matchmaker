package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveSessionStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.models.ProfileState
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModel(
    observeCurrentUserProfileStateUseCase: ObserveCurrentUserProfileStateUseCase,
    observeSessionStateUseCase: ObserveSessionStateUseCase,
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

    val screenState: StateFlow<UserProfileUiStateModel> =
        combine(
            observeSessionStateUseCase(),
            observeCurrentUserProfileStateUseCase()
        ) { session, profile ->
            Logger.d("Session: $session, Profile: $profile")
            when (session) {
                is SessionState.Guest -> {
                    when (profile) {
                        is ProfileState.Present -> {
                            Logger.d("Guest profile present")
                            UserProfileUiStateModel.Success(
                                profile.profile.toUserProfileUiModel()
                            )
                        }

                        is ProfileState.Incomplete -> {
                            Logger.d("Guest profile incomplete")
                            UserProfileUiStateModel.Guest(session.uid)
                        }
                    }
                }

                is SessionState.Authenticated -> {
                    when (profile) {
                        is ProfileState.Present -> {
                            Logger.d("User profile present")
                            val uiModel = profile.profile.toUserProfileUiModel()

                            if (_editableProfile.value == null) {
                                _editableProfile.value = uiModel
                            }

                            UserProfileUiStateModel.Success(uiModel)
                        }


                        is ProfileState.Incomplete -> {
                            Logger.d("User profile incomplete")
                            UserProfileUiStateModel.Incomplete(session.uid)
                        }
                    }
                }

                else -> UserProfileUiStateModel.Guest("")
            }
        }
            .catch { error ->
                emit(
                    UserProfileUiStateModel.Error(
                        defaultMessageMapper.toUIText(AppError.Network.Unknown(error))
                    )
                )
            }
            .stateIn(
                scope,
                SharingStarted.WhileSubscribed(5000),
                UserProfileUiStateModel.Loading
            )

    fun onEvent(event: UserProfileUiEventModel) {
        when (event) {
            UserProfileUiEventModel.OnPickAvatarClicked -> {
                scope.launch {
                    eventHandler.sendEvent(
                        UiEvent.ShowSnackBar(
                            UIText.Plain(
                                "Avatar selection not implemented yet"
                            )
                        )
                    )
                }
            }

            is UserProfileUiEventModel.OnBioChanged -> {
                _editableProfile.update { current -> current?.copy(bio = event.value) }
            }

            is UserProfileUiEventModel.OnNameChanged -> {
                _editableProfile.update { current -> current?.copy(name = event.value) }
            }

            UserProfileUiEventModel.OnSaveClicked -> saveProfile()

            UserProfileUiEventModel.OnSkipClicked -> exitEditMode()

            UserProfileUiEventModel.OnEditModeToggled -> toggleEditMode()
        }
    }

    private fun toggleEditMode() {
        _isEditMode.update { !it }

        if (_isEditMode.value) {
            val currentState = screenState.value

            if (currentState is UserProfileUiStateModel.Success) {
                _editableProfile.value = currentState.userProfile
            }
        } else {
            val currentState = screenState.value

            if (currentState is UserProfileUiStateModel.Success) {
                _editableProfile.value = currentState.userProfile
            }
        }
    }

    private fun exitEditMode() {
        _isEditMode.value = false

        val currentState = screenState.value
        if (currentState is UserProfileUiStateModel.Success) {
            _editableProfile.value = currentState.userProfile
        }
    }

    private fun saveProfile() {
        // TODO
    }

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }
}
