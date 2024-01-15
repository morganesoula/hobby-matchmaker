package com.msoula.hobbymatchmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.msoula.auth.presentation.AuthViewModel
import com.msoula.auth.presentation.SignInScreen
import com.msoula.hobbymatchmaker.ui.theme.HobbyMatchmakerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HobbyMatchmakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val authViewModel: AuthViewModel by viewModels()
                    val user = authViewModel.userState.collectAsState().value
                    val formState = authViewModel.authFormState.collectAsState().value

                    HobbyMatchmakerTheme {
                        SignInScreen(authViewModel = authViewModel, user = user, formState = formState)
                    }
                }
            }
        }
    }
}