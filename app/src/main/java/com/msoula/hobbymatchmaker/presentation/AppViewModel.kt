package com.msoula.hobbymatchmaker.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseAuth
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.ObserveAuthState
import com.msoula.hobbymatchmaker.core.common.AuthUiStateModel
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val ioDispatcher: CoroutineDispatcher,
    private val logOutUseCase: LogOutUseCase,
    private val observeAuthState: ObserveAuthState
) : ViewModel() {

    val authState: StateFlow<AuthUiStateModel> by lazy {
        observeAuthState()
            .map { user ->
                user?.let {
                    if (it.isConnected) {
                        AuthUiStateModel.IsConnected
                    } else {
                        AuthUiStateModel.NotConnected
                    }
                } ?: AuthUiStateModel.NotConnected
            }
            .flowOn(Dispatchers.Main)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AuthUiStateModel.CheckingState
            )
    }

    init {
        Log.d("HMM", "Init AppViewModel into ViewModel")
    }

    fun checkForActiveSession(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()

        return if (auth.currentUser != null) {
            Log.d("HMM", "User is logged in")
            true
        } else if (accessToken != null && !accessToken.isExpired) {
            Log.d("HMM", "Logged in through Facebook cache")
            true
        } else {
            Log.d("HMM", "User is not logged in")
            false
        }
    }

    fun logOut() {
        viewModelScope.launch(ioDispatcher) {
            logOutUseCase()
                .mapSuccess {
                    Log.d("HMM", "Successfully logged out")
                }
                .mapError {
                    Log.e("HMM", "Error while logging out")
                    it
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("HMM", "AppViewModel is killed, sorry buddy")
    }
}
