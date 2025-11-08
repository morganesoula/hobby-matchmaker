package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.CallOnceEffect
import com.msoula.hobbymatchmaker.core.design.ObserveEvents
import com.msoula.hobbymatchmaker.core.design.SnackEffect
import com.msoula.hobbymatchmaker.core.design.atoms.LoadingOverlay
import com.msoula.hobbymatchmaker.core.design.organisms.AuthenticationScreenBottom
import com.msoula.hobbymatchmaker.core.design.organisms.AuthenticationScreenTop
import com.msoula.hobbymatchmaker.core.design.organisms.SignUpForm
import com.msoula.hobbymatchmaker.core.design.templates.SignUpLayout
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthUiEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    oneTimeEventChannelFlow: Flow<AuthUiEventModel>,
    redirectToSignInScreen: () -> Unit,
    redirectToMovieScreen: () -> Unit,
    signUpViewModel: SignUpViewModel
) {
    val snackBarHostState = remember { SnackbarHostState() }

    val registrationState by signUpViewModel.formDataFlow.collectAsState()
    val signUpState by signUpViewModel.signUpState.collectAsState()

    ObserveEvents(oneTimeEventChannelFlow) { event ->
        when (event) {
            is AuthUiEventModel.ShowError ->
                SnackEffect(snackBarHostState, event.error, event)

            is AuthUiEventModel.OnSignUpSuccess ->
                CallOnceEffect(event) {
                    redirectToMovieScreen()
                }

            else -> Unit
        }
    }

    SignUpLayout(
        topBar = { AuthenticationScreenTop(isSignInScreen = false) },
        form = {
            SignUpForm(
                name = registrationState.firstName,
                email = registrationState.email,
                password = registrationState.password,
                loading = signUpState == SignUpEvent.Loading,
                enabled = registrationState.submit,
                onNameChanged = {
                    signUpViewModel.onEvent(
                        AuthenticationUIEvent.OnFirstNameChanged(
                            it
                        )
                    )
                },
                onEmailChanged = { signUpViewModel.onEvent(AuthenticationUIEvent.OnEmailChanged(it)) },
                onPasswordChanged = {
                    signUpViewModel.onEvent(
                        AuthenticationUIEvent.OnPasswordChanged(
                            it
                        )
                    )
                },
                onFinish = { signUpViewModel.onEvent(AuthenticationUIEvent.OnSignUp) }
            )
        },
        bottomSection = {
            AuthenticationScreenBottom(
                isSignInScreen = false,
                onNavigateToOppositeScreen = {
                    signUpViewModel.onEvent(AuthenticationUIEvent.OnScreenChanged)
                    redirectToSignInScreen()
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(CustomSize.Eight)
                    ) {
                        Text(text = data.visuals.message)
                    }
                }
            )
        },
        loadingOverlay = {
            LoadingOverlay(visible = signUpState == SignUpEvent.Loading)
        }
    )
}
