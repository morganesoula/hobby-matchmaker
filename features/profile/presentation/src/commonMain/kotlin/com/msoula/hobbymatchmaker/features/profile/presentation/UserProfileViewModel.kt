package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionState
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveSessionStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.models.ProfileState
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toUserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModel(
    observeCurrentUserProfileStateUseCase: ObserveCurrentUserProfileStateUseCase,
    private val observeSessionStateUseCase: ObserveSessionStateUseCase,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope
    private val _oneTimeEventChannel = Channel<UserProfileUiEventModel>()
    val oneTimeEventChannel = _oneTimeEventChannel.receiveAsFlow()

    val currentUserProfileState: StateFlow<UserProfileUiStateModel> =
        combine(
            observeSessionStateUseCase(),
            observeCurrentUserProfileStateUseCase()
        ) { session, profile ->
            if (session == null) return@combine UserProfileUiStateModel.Loading

            when (session) {
                is SessionState.Guest -> {
                    when (profile) {
                        is ProfileState.Present ->
                            UserProfileUiStateModel.Success(profile.profile.toUserProfileUiModel())

                        is ProfileState.Incomplete ->
                            UserProfileUiStateModel.Guest(session.uid)
                    }
                }

                is SessionState.Authenticated -> {
                    when (profile) {
                        is ProfileState.Present ->
                            UserProfileUiStateModel.Success(profile.profile.toUserProfileUiModel())

                        is ProfileState.Incomplete ->
                            UserProfileUiStateModel.Incomplete(session.uid)
                    }
                }
            }
        }
            .catch { e ->
                emit(
                    UserProfileUiStateModel.Error(
                        defaultMessageMapper.toUIText(
                            AppError.Network.Unknown(
                                e
                            )
                        )
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
            else -> {}
        }
    }
}
