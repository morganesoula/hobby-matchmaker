package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.toGenericAppError
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.profile_sync_failed
import com.msoula.hobbymatchmaker.core.design.profile_updated_message
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveSessionStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.SyncUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.UpsertUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toUserProfileDomainModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import com.msoula.hobbymatchmaker.features.profile.presentation.orchestrators.UserProfileOrchestrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModel(
    private val userProfileInteractor: UserProfileOrchestrator,
    private val defaultMessageMapper: ErrorMessageMapper,
    private val upsertUserUseCase: UpsertUserProfileUseCase,
    private val syncUserUseCase: SyncUserProfileUseCase,
    private val observeSessionStateUseCase: ObserveSessionStateUseCase,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope

    private var observeJob: Job? = null

    private val eventHandler = EventHandler()
    val events = eventHandler.events

    val isEditMode: StateFlow<Boolean>
        field = MutableStateFlow<Boolean>(false)

    val editableProfile: StateFlow<UserProfileUiModel?>
        field = MutableStateFlow<UserProfileUiModel?>(null)

    val originalProfile: StateFlow<UserProfileUiModel?>
        field = MutableStateFlow<UserProfileUiModel?>(null)

    val screenState: StateFlow<UserProfileUiStateModel>
        field = MutableStateFlow<UserProfileUiStateModel>(UserProfileUiStateModel.Loading)

    val isSaving: StateFlow<Boolean>
        field = MutableStateFlow(false)

    // True only while editing and something differs from what was on screen when editing started
    val isDirty: StateFlow<Boolean> = combine(isEditMode, editableProfile, originalProfile) { editing, editable, original ->
        editing && editable != original
    }.stateIn(scope, SharingStarted.Eagerly, false)

    // Picked photo (content URI or file path): shown as a preview, only uploaded when saving
    private var pendingAvatarSource: String? = null

    private val currentUserUid: StateFlow<String?> = observeSessionStateUseCase()
        .map { state -> (state as? SessionState.Authenticated)?.uid }
        .stateIn(scope, SharingStarted.Eagerly, null)

    init {
        Logger.d("UserProfileViewModel created")
        observeProfile()
    }

    private fun observeProfile() {
        Logger.d("Observing profile")
        observeJob?.cancel()
        observeJob = scope.launch {
            observeSessionStateUseCase()
                .flatMapLatest { session ->
                    when (session) {
                        is SessionState.Guest -> flowOf(UserProfileUiStateModel.Guest)
                        is SessionState.Authenticated -> userProfileInteractor.observeCurrentUser(session.uid)
                            .map { profile ->
                                profile?.let { UserProfileUiStateModel.Success(it) }
                                    ?: UserProfileUiStateModel.Loading
                            }

                        null -> flowOf(UserProfileUiStateModel.Loading)
                    }
                }
                .catch { error ->
                    Logger.e("Failed to observe profile: ${error.message}")
                    screenState.update {
                        UserProfileUiStateModel.Error(defaultMessageMapper.toUIText(error.toGenericAppError()))
                    }
                }
                .collect { state ->
                    screenState.update { state }
                    if (state is UserProfileUiStateModel.Success && !isEditMode.value) {
                        editableProfile.update { state.userProfile }
                    }
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

            UserProfileUiEventModel.OnSaveClicked -> saveProfile()

            UserProfileUiEventModel.OnEditModeClicked -> toggleEditMode()

            UserProfileUiEventModel.OnRetryClicked -> retryObservation()
        }
    }

    private fun retryObservation() {
        screenState.update { UserProfileUiStateModel.Loading }
        observeProfile()
    }

    private fun toggleEditMode() {
        originalProfile.update { editableProfile.value }
        isEditMode.update { true }
    }

    private fun saveProfile() {
        if (isSaving.value) return
        val editable = editableProfile.value ?: return
        if (!editable.isNameValid) return
        val uid = currentUserUid.value ?: run {
            Logger.e("SaveProfile called with null uid")
            return
        }

        isSaving.update { true }
        scope.launch {
            try {
                var toSave = editable.copy(
                    name = editable.name.trim(),
                    bio = editable.bio?.trim()
                )

                pendingAvatarSource?.let { source ->
                    when (val upload = userProfileInteractor.saveAvatar(uid, originalProfile.value?.avatarUrl, source)) {
                        is AppResult.Success -> toSave = toSave.copy(avatarUrl = upload.data)
                        is AppResult.Failure -> {
                            // Stay in edit mode: nothing was saved, the user can retry or cancel
                            eventHandler.sendEvent(UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(upload.error)))
                            return@launch
                        }
                    }
                }

                val domainModel = toSave.toUserProfileDomainModel(uid)
                upsertUserUseCase(domainModel)
                    .onSuccess {
                        originalProfile.update { toSave }
                        editableProfile.update { toSave }
                        pendingAvatarSource = null

                        // Close edit mode first: the screen shows snackbars one after the other
                        eventHandler.sendEvent(UiEvent.OnDataReady("profile_updated"))
                        eventHandler.sendEvent(UiEvent.ShowSnackBar(UIText.Resource(Res.string.profile_updated_message)))

                        launch {
                            syncUserUseCase(domainModel)
                                .onFailure {
                                    Logger.e("Failed to sync profile remotely - $uid")
                                    eventHandler.sendEvent(
                                        UiEvent.ShowSnackBar(UIText.Resource(Res.string.profile_sync_failed))
                                    )
                                }
                        }
                    }
                    .onFailure { error ->
                        eventHandler.sendEvent(UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error)))
                    }
            } finally {
                isSaving.update { false }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }

    fun closeEdition() {
        pendingAvatarSource = null
        editableProfile.update { originalProfile.value }
        isEditMode.update { false }
    }

    private fun onAvatarSelected(avatarPath: String) {
        pendingAvatarSource = avatarPath
        editableProfile.update { it?.copy(avatarUrl = avatarPath) }
    }
}
