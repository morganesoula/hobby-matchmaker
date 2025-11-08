package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.design.CallOnceEffect
import com.msoula.hobbymatchmaker.core.design.ObserveEvents
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.SnackEffect
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryAlertDialog
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryTextField
import com.msoula.hobbymatchmaker.core.design.cancel
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_create_redirect_button
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_dialog_text
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_dialog_title
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_dont_ask_again
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_validation_button
import com.msoula.hobbymatchmaker.core.design.continue_with_rs
import com.msoula.hobbymatchmaker.core.design.forgot_password
import com.msoula.hobbymatchmaker.core.design.forgot_password_title
import com.msoula.hobbymatchmaker.core.design.molecules.LabeledDivider
import com.msoula.hobbymatchmaker.core.design.organisms.AuthenticationScreenBottom
import com.msoula.hobbymatchmaker.core.design.organisms.AuthenticationScreenTop
import com.msoula.hobbymatchmaker.core.design.organisms.SignInForm
import com.msoula.hobbymatchmaker.core.design.organisms.SignInSocialMedia
import com.msoula.hobbymatchmaker.core.design.reset_password
import com.msoula.hobbymatchmaker.core.design.templates.SignInLayout
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.your_email
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
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
    var dontAskGuestDialog by rememberSaveable { mutableStateOf(false) }

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
        divider = { LabeledDivider(label = stringResource(Res.string.continue_with_rs)) },
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
                PrimaryAlertDialog(
                    paddingValues = paddingValues,
                    title = stringResource(Res.string.forgot_password),
                    confirmButtonText = stringResource(Res.string.reset_password),
                    cancelButtonText = stringResource(Res.string.cancel),
                    isEnabled = signInFormState.submitEmailReset,
                    isLoading = resetPasswordState == ResetPasswordEvent.Loading,
                    onDismiss = { signInViewModel.onEvent(AuthenticationUIEvent.HideForgotPasswordDialog) },
                    onConfirm = { signInViewModel.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed) }
                ) {
                    PrimaryTextField(
                        text = stringResource(Res.string.forgot_password_title),
                        label = stringResource(Res.string.your_email),
                        contentDescription = stringResource(Res.string.your_email),
                        singleLine = true,
                        onValueChanged = {
                            signInViewModel.onEvent(
                                AuthenticationUIEvent.OnEmailResetChanged(
                                    it
                                )
                            )
                        }
                    )
                }
            }

            if (showGuestDialog) {
                PrimaryAlertDialog(
                    paddingValues = paddingValues,
                    title = stringResource(Res.string.continue_as_guest_dialog_title),
                    confirmButtonText = stringResource(Res.string.continue_as_guest_validation_button),
                    cancelButtonText = stringResource(
                        Res.string.continue_as_guest_create_redirect_button
                    ),
                    isEnabled = true,
                    isLoading = false,
                    onDismiss = {
                        showGuestDialog = false
                        redirectToSignUpScreen()
                    },
                    onConfirm = {
                        showGuestDialog = false
                        signInViewModel.onEvent(
                            AuthenticationUIEvent.OnContinueAsGuestConfirmed(dontAskGuestDialog)
                        )
                        redirectToMovieScreen()
                    }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(Res.string.continue_as_guest_dialog_text))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = dontAskGuestDialog,
                                onCheckedChange = { dontAskGuestDialog = it })
                            Text(stringResource(Res.string.continue_as_guest_dont_ask_again))
                        }
                    }
                }
            }
        }
    )
}
