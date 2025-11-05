package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.design.CallOnceEffect
import com.msoula.hobbymatchmaker.core.design.ObserveEvents
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.SnackEffect
import com.msoula.hobbymatchmaker.core.design.cancel
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldAuthComponent
import com.msoula.hobbymatchmaker.core.design.continue_with_rs
import com.msoula.hobbymatchmaker.core.design.forgot_password_title
import com.msoula.hobbymatchmaker.core.design.organisms.AuthenticationScreenBottom
import com.msoula.hobbymatchmaker.core.design.organisms.AuthenticationScreenTop
import com.msoula.hobbymatchmaker.core.design.organisms.SignInForm
import com.msoula.hobbymatchmaker.core.design.organisms.SignInSocialMedia
import com.msoula.hobbymatchmaker.core.design.reset_password
import com.msoula.hobbymatchmaker.core.design.templates.SignInLayout
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.your_email
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.components.GuestModeDialog
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthUiEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.ResetPasswordEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInScreenContent(
    signInViewModel: SignInViewModel,
    redirectToMovieScreen: () -> Unit,
    redirectToSignUpScreen: () -> Unit,
    oneTimeEventChannelFlow: Flow<AuthUiEventModel>,
    shouldShowGuestWarning: Boolean,
    facebookUIClient: FacebookUIClient
) {
    val resetPasswordState by signInViewModel.resetPasswordState.collectAsState()
    val signInState by signInViewModel.signInState.collectAsState()
    val openResetDialog by signInViewModel.openResetDialog.collectAsState()
    val signInFormState by signInViewModel.formDataFlow.collectAsState()
    val isGuestLoading by signInViewModel.isGuestLoading.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    var showGuestDialog by rememberSaveable { mutableStateOf(false) }

    ObserveEvents(oneTimeEventChannelFlow) { event ->
        when (event) {
            is AuthUiEventModel.ShowError ->
                SnackEffect(snackBarHostState, event.error, event)

            is AuthUiEventModel.OnSignInSuccess ->
                CallOnceEffect(event) {
                    redirectToMovieScreen()
                    signInViewModel.onEvent(AuthenticationUIEvent.OnResetSignInState)
                }

            is AuthUiEventModel.OnResetPasswordSuccess -> {
                signInViewModel.onEvent(AuthenticationUIEvent.HideForgotPasswordDialog)
                SnackEffect(
                    snackBarHostState,
                    UIText.Resource(Res.string.reset_password),
                    event
                )
            }

            else -> Unit
        }
    }

    SignInLayout(
        topBar = { AuthenticationScreenTop(isSignInScreen = true) },
        form = {
            SignInForm(
                email = signInFormState.email,
                password = signInFormState.password,
                loading = signInState == SignInEvent.Loading,
                enabled = signInFormState.submit,
                onEmailChanged = {
                    signInViewModel.onEvent(AuthenticationUIEvent.OnEmailChanged(it))
                },
                onPasswordChanged = {
                    signInViewModel.onEvent(AuthenticationUIEvent.OnPasswordChanged(it))
                },
                onForgotPasswordClicked = {
                    signInViewModel.onEvent(AuthenticationUIEvent.OnForgotPasswordClicked)
                },
                onFinish = {
                    signInViewModel.onEvent(AuthenticationUIEvent.OnSignIn)
                }
            )
        },
        divider = { DividerRowComponent() },
        socialMediaSection = {
            SignInSocialMedia(
                onGoogleClick = {
                    signInViewModel.onEvent(AuthenticationUIEvent.OnGoogleButtonClicked)
                },
                onAppleClick = {
                    signInViewModel.onEvent(AuthenticationUIEvent.OnAppleButtonClicked)
                },
                onFacebookClick = {
                    if (facebookUIClient.hasValidToken()) {
                        return@SignInSocialMedia
                    }

                    facebookUIClient.registerCallback(
                        onSuccess = { credential, _ ->
                            signInViewModel.onEvent(
                                AuthenticationUIEvent.OnFacebookButtonClicked(
                                    credential
                                )
                            )
                        },
                        onError = { Logger.d("Error fetching Facebook credentials") }
                    )
                    facebookUIClient.logIn()
                }
            )
        },
        bottomSection = {
            AuthenticationScreenBottom(
                isSignInScreen = true,
                onNavigateToOppositeScreen = redirectToSignUpScreen,
                onContinueAsGuest = {
                    if (shouldShowGuestWarning) {
                        showGuestDialog = true
                    } else {
                        signInViewModel.onEvent(
                            AuthenticationUIEvent.OnContinueAsGuestConfirmed(true)
                        )
                        redirectToMovieScreen()
                    }
                },
                isGuestLoading = isGuestLoading,
                guestButtonEnabled = true
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) { Text(text = data.visuals.message) }
                }
            )
        },
        overlayContent = { paddingValues ->
            if (openResetDialog) {
                ForgotPasswordAlertDialog(
                    email = signInFormState.emailReset,
                    paddingValues = paddingValues,
                    enableSubmit = signInFormState.submitEmailReset,
                    onEvent = signInViewModel::onEvent,
                    isLoading = resetPasswordState == ResetPasswordEvent.Loading
                )
            }

            GuestModeDialog(
                show = showGuestDialog,
                onDismiss = { showGuestDialog = false },
                onContinue = { dontAskAgain ->
                    signInViewModel.onEvent(
                        AuthenticationUIEvent.OnContinueAsGuestConfirmed(dontAskAgain)
                    )
                    redirectToMovieScreen()
                },
                onCreateAccount = redirectToSignUpScreen
            )
        }
    )
}


@Composable
fun DividerRowComponent(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        HorizontalDivider(
            Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            thickness = 2.dp
        )
        Text(
            text = stringResource(Res.string.continue_with_rs),
            modifier = Modifier.weight(3f),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        HorizontalDivider(
            Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            thickness = 2.dp
        )
    }
}

@Composable
fun ForgotPasswordAlertDialog(
    modifier: Modifier = Modifier,
    email: String,
    onEvent: (AuthenticationUIEvent) -> Unit,
    paddingValues: PaddingValues,
    enableSubmit: Boolean,
    isLoading: Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(
        modifier = modifier.padding(paddingValues),
        title = {
            Text(
                text = stringResource(Res.string.forgot_password_title),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        },
        text = {
            HMMTextFieldAuthComponent(
                value = email,
                onValueChange = {
                    onEvent(AuthenticationUIEvent.OnEmailResetChanged(it))
                },
                label = stringResource(Res.string.your_email),
                modifier = Modifier.fillMaxWidth()
            )
        },
        onDismissRequest = { onEvent(AuthenticationUIEvent.HideForgotPasswordDialog) },
        confirmButton = {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        keyboardController?.hide()
                        onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
                    },
                    enabled = enableSubmit
                ) {
                    Text(text = stringResource(Res.string.reset_password))
                }
            }

        },
        dismissButton = {
            Button(
                onClick = { onEvent(AuthenticationUIEvent.HideForgotPasswordDialog) },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
            ) {
                Text(text = stringResource(Res.string.cancel))
            }
        }
    )
}
