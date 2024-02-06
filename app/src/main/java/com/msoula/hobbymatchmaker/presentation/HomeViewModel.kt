package com.msoula.hobbymatchmaker.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.msoula.auth.domain.repository.AuthRepository
import com.msoula.auth.domain.repository.Response
import com.msoula.di.navigation.LoginScreenRoute
import com.msoula.di.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val navigator: Navigator
) : ViewModel() {

    fun checkForActiveSession(): Boolean {
        return auth.currentUser?.let {
            Log.d("HMM", "User is logged in")
            true
        } ?: run {
            Log.d("HMM", "User is not logged in")
            false
        }
    }

    fun logOut() {
        viewModelScope.launch(ioDispatcher) {
            try {
                when (authRepository.logOut()) {
                    is Response.Success -> {
                        authRepository.getAuthState()

                        withContext(Dispatchers.Main) {
                            navigator.navigate(LoginScreenRoute)
                        }
                    }

                    else -> Log.e("HMM", "Error while logging out")
                }
            } catch (exception: Exception) {
                Log.e("HMM", "Exception while logging out: $exception")
            }
        }
    }
}