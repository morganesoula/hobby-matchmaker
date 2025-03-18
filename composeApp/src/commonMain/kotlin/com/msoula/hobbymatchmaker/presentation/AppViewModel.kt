package com.msoula.hobbymatchmaker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AuthUiStateModel
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.presentation.models.LogOutState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val logOutUseCase: LogOutUseCase,
    private val observeIsConnectedUseCase: ObserveIsConnectedUseCase
) : ViewModel() {

    private val _logOutState: MutableStateFlow<LogOutState> = MutableStateFlow(LogOutState.Idle)
    val logOutState = _logOutState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val authenticationState: StateFlow<AuthUiStateModel> by lazy {
        observeIsConnectedUseCase()
            .mapLatest { isConnected ->
                if (isConnected) AuthUiStateModel.IsConnected else AuthUiStateModel.NotConnected
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AuthUiStateModel.CheckingState
            )
    }

    fun logOut() {
        viewModelScope.launch {
            logOutUseCase(Parameters.StringParam("")).collectLatest { result ->
                _logOutState.update {
                    when (result) {
                        is Result.Success -> LogOutState.Success
                        is Result.Failure -> {
                            val errorMessage = handleError(result.error)
                            LogOutState.Error(errorMessage)
                        }

                        else -> LogOutState.Idle
                    }
                }
            }
        }
    }

    private fun handleError(error: AppError): String {
        return when (error) {
            is LogOutError.FirebaseException -> ""//getString(Res.string.firebase_log_out_error)
            is LogOutError.FacebookLogOutException -> ""//getString(Res.string.facebook_log_out_error)
            is LogOutError.CredentialException -> ""//getString(Res.string.google_log_out_error)
            is LogOutError.UnknownError -> ""//getString(Res.string.unknown_log_out_error)
            else -> error.message
        }
    }
}
