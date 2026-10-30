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
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ReservePseudoUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.validators.PseudoValidator
import com.msoula.hobbymatchmaker.features.profile.presentation.models.PickPseudoEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.PickPseudoStatusModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.PickPseudoUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class PickPseudoViewModel(
    private val observeSessionStateUseCase: ObserveSessionStateUseCase,
    private val observeCurrentUserProfileStateUseCase: ObserveCurrentUserProfileStateUseCase,
    private val isPseudoAvailableUseCase: IsPseudoAvailableUseCase,
    private val reservePseudoUseCase: ReservePseudoUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope

    private val eventHandler = EventHandler()
    val events = eventHandler.events

    val formState: StateFlow<PickPseudoUiModel>
        field = MutableStateFlow(PickPseudoUiModel.Initial)

    private val currentUserUid: StateFlow<String?> = observeSessionStateUseCase()
        .map { state -> (state as? SessionState.Authenticated)?.uid }
        .stateIn(scope, SharingStarted.Eagerly, null)

    init {
        prefillFirstName()
        observePseudoAvailability()
    }

    fun onEvent(event: PickPseudoEventModel) {
        when (event) {
            is PickPseudoEventModel.OnFirstNameChanged ->
                formState.update { it.copy(firstName = event.value) }

            is PickPseudoEventModel.OnPseudoChanged -> onPseudoChanged(event.value)
            PickPseudoEventModel.OnContinueClicked -> reservePseudo()
            PickPseudoEventModel.OnLogOutClicked -> logOut()
        }
    }

    private fun prefillFirstName() {
        scope.launch {
            val uid = currentUserUid.filterNotNull().first()
            val name = observeCurrentUserProfileStateUseCase(uid).first().name
            formState.update { it.copy(firstName = name, isFirstNameEditable = name.isBlank()) }
        }
    }

    private fun onPseudoChanged(value: String) {
        val trimmed = value.trim()
        val errors = PseudoValidator.validate(trimmed)
        val status = when {
            trimmed.isEmpty() -> PickPseudoStatusModel.Idle
            errors.isNotEmpty() -> PickPseudoStatusModel.Invalid(errors)
            else -> PickPseudoStatusModel.Checking
        }
        formState.update { it.copy(pseudo = value, status = status) }
    }

    // The availability check is only a hint: the reservation transaction enforces uniqueness
    private fun observePseudoAvailability() {
        scope.launch {
            formState
                .map { it.pseudo.trim() }
                .distinctUntilChanged()
                .debounce(AVAILABILITY_DEBOUNCE_MS)
                .collectLatest { pseudo ->
                    if (pseudo.isEmpty() || PseudoValidator.validate(pseudo).isNotEmpty()) return@collectLatest

                    isPseudoAvailableUseCase(pseudo)
                        .onSuccess { isAvailable ->
                            val status =
                                if (isAvailable) PickPseudoStatusModel.Available else PickPseudoStatusModel.Taken
                            formState.update { if (it.pseudo.trim() == pseudo) it.copy(status = status) else it }
                        }
                        .onFailure { error ->
                            formState.update { it.copy(status = PickPseudoStatusModel.Idle) }
                            eventHandler.sendEvent(UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error)))
                        }
                }
        }
    }

    private fun reservePseudo() {
        val form = formState.value
        if (!form.canSubmit) return

        scope.launch {
            val uid = currentUserUid.value ?: run {
                Logger.e("ReservePseudo called with null uid")
                return@launch
            }
            formState.update { it.copy(isSubmitting = true) }

            reservePseudoUseCase(uid, form.pseudo, form.firstName)
                .onSuccess {
                    eventHandler.sendEvent(UiEvent.Navigate(NavigationDestination.Movies))
                }
                .onFailure { error ->
                    if (error == AppError.Profile.PseudoTaken) {
                        formState.update { it.copy(status = PickPseudoStatusModel.Taken) }
                    } else {
                        eventHandler.sendEvent(UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error)))
                    }
                }

            formState.update { it.copy(isSubmitting = false) }
        }
    }

    private fun logOut() {
        scope.launch {
            logOutUseCase()
                .onSuccess {
                    eventHandler.sendEvent(UiEvent.Navigate(NavigationDestination.SignIn))
                }
                .onFailure { error ->
                    eventHandler.sendEvent(UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error)))
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }

    private companion object {
        const val AVAILABILITY_DEBOUNCE_MS = 400L
    }
}
