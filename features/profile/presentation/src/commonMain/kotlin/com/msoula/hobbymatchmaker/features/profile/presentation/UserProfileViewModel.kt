package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveSessionStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.UpdateUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toUserProfileDomainModel
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
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val logOutUseCase: LogOutUseCase,
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

    val screenState: StateFlow<UserProfileUiStateModel> =
        combine(
            observeSessionStateUseCase(),
            observeCurrentUserProfileStateUseCase()
        ) { session, profile ->
            when (session) {
                is SessionState.Authenticated -> {
                    currentUserUid = profile.uid
                    UserProfileUiStateModel.Success(profile.toUserProfileUiModel())
                }

                is SessionState.Guest -> {
                    currentUserUid = session.uid
                    UserProfileUiStateModel.Guest
                }

                else -> UserProfileUiStateModel.Loading
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

            is UserProfileUiEventModel.OnInterestsChanged -> {
                _editableProfile.update { current -> current?.copy(interests = event.value) }
            }

            UserProfileUiEventModel.OnSaveClicked -> saveProfile()

            UserProfileUiEventModel.OnEditModeClicked -> toggleEditMode()

            UserProfileUiEventModel.OnSignUpButtonClicked -> logOut()
        }
    }

    private fun toggleEditMode() = _isEditMode.update { true }

    private fun saveProfile() {
        scope.launch {
            _editableProfile.value?.let {
                updateUserProfileUseCase(
                    it.toUserProfileDomainModel(currentUserUid ?: "")
                )
                    .onSuccess {
                        eventHandler.sendEvent(UiEvent.OnDataReady("profile_updated"))
                    }
                    .onFailure { error ->
                        eventHandler.sendEvent(
                            UiEvent.ShowSnackBar(
                                defaultMessageMapper.toUIText(
                                    error
                                )
                            )
                        )
                    }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }

    private fun logOut() {
        scope.launch {
            logOutUseCase()
                .onFailure { error ->
                    eventHandler.sendEvent(
                        UiEvent.ShowSnackBar(
                            defaultMessageMapper.toUIText(error)
                        )
                    )
                }
                .onSuccess {
                    Logger.d("Successfully logged out")
                    eventHandler.sendEvent(UiEvent.NavigateToRoute("sign_up"))
                }
        }
    }

    fun closeEdition() {
        _isEditMode.update { false }
    }
}
