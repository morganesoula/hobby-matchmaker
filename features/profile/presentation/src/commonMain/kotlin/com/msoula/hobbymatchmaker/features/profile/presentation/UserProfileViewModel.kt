package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileSuccess
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.presentation.mappers.toUserProfileUiModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiStateModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModel(
    observeCurrentUserProfileUseCase: ObserveCurrentUserProfileUseCase,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope
    private val _oneTimeEventChannel = Channel<UserProfileUiEventModel>()
    val oneTimeEventChannel = _oneTimeEventChannel.receiveAsFlow()

    val currentUserProfileState: StateFlow<UserProfileUiStateModel> =
        observeCurrentUserProfileUseCase().mapLatest { result ->
            when (result) {
                is AppResult.Success -> {
                    when (val data = result.data) {
                        is ObserveCurrentUserProfileSuccess.Success -> {
                            UserProfileUiStateModel.Success(data.userProfile.toUserProfileUiModel())
                        }
                    }
                }

                is AppResult.Failure -> {
                    UserProfileUiStateModel.Error(
                        defaultMessageMapper.toUIText(result.error)
                    )
                }
            }
        }.stateIn(
            scope,
            SharingStarted.WhileSubscribed(5000),
            UserProfileUiStateModel.Loading
        )
}
