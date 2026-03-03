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
import com.msoula.hobbymatchmaker.core.design.util.NavigationDestination
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveSessionStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.IsPseudoAvailableUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.SyncUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.UpsertUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toUserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import com.msoula.hobbymatchmaker.features.profile.presentation.orchestrators.UserProfileOrchestrator
import com.msoula.hobbymatchmaker.features.social.domain.useCases.RefreshSocialCircleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModel(
    private val userProfileInteractor: UserProfileOrchestrator,
    private val defaultMessageMapper: ErrorMessageMapper,
    private val checkPseudoUseCase: IsPseudoAvailableUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val upsertUserUseCase: UpsertUserProfileUseCase,
    private val syncUserUseCase: SyncUserProfileUseCase,
    private val refreshSocialCircleUseCase: RefreshSocialCircleUseCase,
    private val observeSessionStateUseCase: ObserveSessionStateUseCase,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope

    private val eventHandler = EventHandler()
    val events = eventHandler.events

    val isEditMode: StateFlow<Boolean>
        field = MutableStateFlow<Boolean>(false)

    val editableProfile: StateFlow<UserProfileUiModel?>
        field = MutableStateFlow<UserProfileUiModel?>(null)

    val originalProfile: StateFlow<UserProfileUiModel?>
        field = MutableStateFlow<UserProfileUiModel?>(null)

    val isPseudoAvailable: StateFlow<Boolean?>
        field = MutableStateFlow<Boolean?>(null)

    val screenState: StateFlow<UserProfileUiStateModel>
        field = MutableStateFlow<UserProfileUiStateModel>(UserProfileUiStateModel.Loading)

    private val currentUserUid: StateFlow<String?> = observeSessionStateUseCase()
        .map { state -> (state as? SessionState.Authenticated)?.uid }
        .stateIn(scope, SharingStarted.Eagerly, null)

    init {
        observeProfile()
        refreshProfileData()
    }

    private fun observeProfile() {
        scope.launch {
            currentUserUid
                .flatMapLatest { uid ->
                    if (uid != null) userProfileInteractor.observeCurrentUser(uid)
                    else flowOf(null)
                }
                .collect { profile ->
                    if (profile == null) {
                        screenState.update { UserProfileUiStateModel.Loading }
                        return@collect
                    }

                    screenState.update { UserProfileUiStateModel.Success(profile) }
                    if (!isEditMode.value) {
                        editableProfile.update { profile }
                    }
                }
        }
    }

    private fun refreshProfileData() {
        scope.launch {
            val uid = currentUserUid.filterNotNull().first()
            refreshSocialCircleUseCase(uid)
                .onFailure { error ->
                    Logger.e("Failed to refresh profile data: $error")
                }
        }
    }

    fun onEvent(event: UserProfileUiEventModel) {
        when (event) {
            is UserProfileUiEventModel.OnBioChanged ->
                editableProfile.update { current -> current?.copy(bio = event.value) }

            is UserProfileUiEventModel.OnNameChanged ->
                editableProfile.update { current -> current?.copy(name = event.value) }

            is UserProfileUiEventModel.OnInterestsChanged -> {
                editableProfile.update { current -> current?.copy(interests = event.value) }
            }

            is UserProfileUiEventModel.OnAvatarSelected -> {
                onAvatarSelected(event.path)
            }

            is UserProfileUiEventModel.OnPseudoChanged -> {
                editableProfile.update { current -> current?.copy(pseudo = event.value) }
                isPseudoAvailable.update { null }
            }

            UserProfileUiEventModel.OnPseudoDefined -> {
                val profile = editableProfile.value ?: return
                scope.launch {
                    if (profile.pseudo != originalProfile.value?.pseudo)
                        checkPseudoUseCase(profile.pseudo)
                            .onSuccess { available ->
                                isPseudoAvailable.update { available }
                            }
                            .onFailure { error ->
                                isPseudoAvailable.update { null }
                                eventHandler.sendEvent(
                                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                                )
                            }
                }
            }

            UserProfileUiEventModel.OnSaveClicked -> saveProfile()

            UserProfileUiEventModel.OnEditModeClicked -> toggleEditMode()

            UserProfileUiEventModel.OnSignUpButtonClicked -> logOut()
        }
    }

    private fun toggleEditMode() {
        originalProfile.update { editableProfile.value }
        isEditMode.update { true }
    }

    private fun saveProfile() {
        scope.launch {
            val editable = editableProfile.value ?: return@launch
            val uid = currentUserUid.value ?: run {
                Logger.e("SaveProfile called with null uid")
                return@launch
            }
            val editableToDomain = editable.toUserProfileDomainModel(uid)

            upsertUserUseCase(editableToDomain)
                .onSuccess {
                    originalProfile.update { editable }

                    eventHandler.sendEvent(UiEvent.OnDataReady("profile_updated"))
                    launch {
                        syncUserUseCase(editableToDomain)
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
            logOutUseCase()
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
        editableProfile.update { originalProfile.value }
        isEditMode.update { false }
        isPseudoAvailable.update { null }
    }

    @OptIn(ExperimentalTime::class)
    private fun onAvatarSelected(avatarPath: String) {
        scope.launch {
            val uid = currentUserUid.value ?: run {
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(AppError.Domain.Unauthorized))
                )
                return@launch
            }
            val old = editableProfile.value?.avatarUrl

            userProfileInteractor.saveAvatar(uid, old, avatarPath)
                .onSuccess { newPath ->
                    editableProfile.update { it?.copy(avatarUrl = newPath) }
                }
                .onFailure { error ->
                    eventHandler.sendEvent(
                        UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                    )
                }
        }
    }
}
