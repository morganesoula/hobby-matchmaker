package com.msoula.hobbymatchmaker.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseAuth
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ConnexionMode
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.ObserveAuthenticationStateUseCase
import com.msoula.hobbymatchmaker.core.common.AuthUiStateModel
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.FetchConnexionModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AppViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val ioDispatcher: CoroutineDispatcher,
    private val logOutUseCase: LogOutUseCase,
    private val observeAuthenticationStateUseCase: ObserveAuthenticationStateUseCase,
    private val fetchConnexionModeUseCase: FetchConnexionModeUseCase
) : ViewModel() {

    val authenticationState: StateFlow<AuthUiStateModel> by lazy {
        observeAuthenticationStateUseCase()
            .mapLatest { isConnected ->
                if (isConnected) AuthUiStateModel.IsConnected else AuthUiStateModel.NotConnected
            }
            .flowOn(Dispatchers.Main)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                AuthUiStateModel.CheckingState
            )
    }

    fun logOut() {
        viewModelScope.launch(ioDispatcher) {
            val connexionMode: ConnexionMode = fetchConnexionModeUseCase().first().let { mode ->
                mode?.let { ConnexionMode.valueOf(mode) } ?: ConnexionMode.EMAIL
            }

            Log.d("HMM", "Into logOut method with connexion mode $connexionMode")
            logOutUseCase(connexionMode)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("HMM", "AppViewModel is killed, sorry buddy")
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
}
