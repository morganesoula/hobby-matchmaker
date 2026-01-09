package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.LoadingOverlay
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryAlertDialog
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryTextField
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.cancel
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_create_redirect_button
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_dialog_text
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_dialog_title
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_dont_ask_again
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_validation_button
import com.msoula.hobbymatchmaker.core.design.continue_with_rs
import com.msoula.hobbymatchmaker.core.design.forgot_password_title
import com.msoula.hobbymatchmaker.core.design.molecules.LabeledDivider
import com.msoula.hobbymatchmaker.core.design.organisms.AuthenticationScreenBottom
import com.msoula.hobbymatchmaker.core.design.organisms.AuthenticationScreenTop
import com.msoula.hobbymatchmaker.core.design.organisms.SignInForm
import com.msoula.hobbymatchmaker.core.design.organisms.SignInSocialMedia
import com.msoula.hobbymatchmaker.core.design.reset_password
import com.msoula.hobbymatchmaker.core.design.templates.SignInLayout
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.util.NavigationDestination
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.core.design.your_email
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.models.SignInFormStateModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInScreenContent(
    signInState: UiState<Unit>,
    formState: SignInFormStateModel,
    onEvent: (AuthenticationUIEvent) -> Unit,
    dontAskCheckbox: Boolean,
    onNavigate: (NavigationDestination) -> Unit,
    snackBarHostState: SnackbarHostState,
    facebookUIClient: FacebookUIClient
) {
    var displayResetPasswordDialog by rememberSaveable { mutableStateOf(false) }
    var displayGuestDialog by rememberSaveable { mutableStateOf(false) }
    var localCheckboxValue by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(displayGuestDialog) {
        if (displayGuestDialog) {
            localCheckboxValue = dontAskCheckbox
        }
    }

    Scaffold(
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
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(
                top = CustomSize.Sixteen,
                start = CustomSize.Sixteen,
                end = CustomSize.Sixteen
            )
        ) {
            StateContainer(
                state = signInState,
                onLoading = { LoadingOverlay(signInState is UiState.Loading) },
                onEmpty = {},
                onError = { _, _ -> },
                onSuccess = {
                    SignInLayout(
                        padding = padding,
                        topBar = { AuthenticationScreenTop(isSignInScreen = true) },
                        form = {
                            SignInForm(
                                email = formState.email,
                                password = formState.password,
                                loading = signInState is UiState.Loading,
                                enabled = formState.submit,
                                onEmailChanged = {
                                    onEvent(AuthenticationUIEvent.OnEmailChanged(it))
                                },
                                onPasswordChanged = {
                                    onEvent(
                                        AuthenticationUIEvent.OnPasswordChanged(
                                            it
                                        )
                                    )
                                },
                                onForgotPasswordClicked = { displayResetPasswordDialog = true },
                                onFinish = {
                                    onEvent(AuthenticationUIEvent.OnSignIn)
                                }
                            )
                        },
                        divider = { LabeledDivider(label = stringResource(Res.string.continue_with_rs)) },
                        socialMediaSection = {
                            SignInSocialMedia(
                                onGoogleClick = {
                                    onEvent(AuthenticationUIEvent.OnGoogleButtonClicked)
                                },
                                onAppleClick = {
                                    onEvent(AuthenticationUIEvent.OnAppleButtonClicked)
                                },
                                onFacebookClick = {
                                    if (facebookUIClient.hasValidToken()) {
                                        return@SignInSocialMedia
                                    }

                                    facebookUIClient.registerCallback(
                                        onSuccess = { credential, _ ->
                                            onEvent(
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
                                onNavigateToOppositeScreen = { onNavigate(NavigationDestination.SignUp) },
                                onContinueAsGuest = {
                                    if (dontAskCheckbox) {
                                        Logger.d("Continue as guest clicked - setting account as guest")
                                        onEvent(AuthenticationUIEvent.SetAccountAsGuest)
                                    } else {
                                        displayGuestDialog = true
                                    }
                                },
                                guestButtonEnabled = true
                            )
                        }
                    )
                }
            )

            if (displayResetPasswordDialog) {
                PrimaryAlertDialog(
                    paddingValues = padding,
                    title = stringResource(Res.string.forgot_password_title),
                    confirmButtonText = stringResource(Res.string.reset_password),
                    cancelButtonText = stringResource(Res.string.cancel),
                    isEnabled = formState.submitEmailReset,
                    isLoading = false,
                    onCancel = { displayResetPasswordDialog = false },
                    onDismiss = { displayResetPasswordDialog = false },
                    onConfirm = { onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed) }
                ) {
                    PrimaryTextField(
                        text = formState.emailReset,
                        label = stringResource(Res.string.your_email),
                        singleLine = true,
                        onValueChanged = {
                            onEvent(
                                AuthenticationUIEvent.OnEmailResetChanged(
                                    it
                                )
                            )
                        }
                    )
                }
            }

            if (displayGuestDialog) {
                PrimaryAlertDialog(
                    paddingValues = padding,
                    title = stringResource(Res.string.continue_as_guest_dialog_title),
                    confirmButtonText = stringResource(Res.string.continue_as_guest_validation_button),
                    cancelButtonText = stringResource(Res.string.continue_as_guest_create_redirect_button),
                    isEnabled = true,
                    isLoading = false,
                    onCancel = {
                        displayGuestDialog = false
                        onNavigate(NavigationDestination.SignUp)
                    },
                    onDismiss = {
                        displayGuestDialog = false
                    },
                    onConfirm = {
                        displayGuestDialog = false
                        onEvent(AuthenticationUIEvent.SetAccountAsGuest)
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
                                checked = localCheckboxValue,
                                onCheckedChange = {
                                    Logger.d("Checkbox value: $it")
                                    localCheckboxValue = it
                                    onEvent(
                                        AuthenticationUIEvent.SaveDontAskGuestDialogValue(it)
                                    )
                                }
                            )
                            Text(stringResource(Res.string.continue_as_guest_dont_ask_again))
                        }
                    }
                }
            }
        }
    }
}
