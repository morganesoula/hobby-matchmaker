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
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.LoadingOverlay
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryAlertDialog
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryTextField
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.atoms.asStringSuspend
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
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.core.design.your_email
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInScreenContent(
    signInViewModel: SignInViewModel,
    onNavigate: (String) -> Unit,
    facebookUIClient: FacebookUIClient
) {
    val signInState by signInViewModel.signInState.collectAsState()
    val formState by signInViewModel.formDataFlow.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }

    var displayResetPasswordDialog by rememberSaveable { mutableStateOf(false) }
    var displayGuestDialog by rememberSaveable { mutableStateOf(false) }
    var localCheckboxValue by rememberSaveable { mutableStateOf(false) }

    val dontAskCheckboxValue by signInViewModel.dontAskCheckboxValue.collectAsState()

    LaunchedEffect(displayGuestDialog) {
        if (displayGuestDialog) {
            localCheckboxValue = dontAskCheckboxValue
        }
    }

    LaunchedEffect(signInViewModel.events) {
        signInViewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar ->
                    snackBarHostState.showSnackbar(event.message.asStringSuspend())

                is UiEvent.NavigateToRoute -> onNavigate(event.route)

                is UiEvent.OpenDialog ->
                    when (event.dialogPurpose) {
                        else -> {}
                    }

                is UiEvent.CloseDialog -> {
                    when (event.dialogName) {
                        "reset_password" -> {
                            displayResetPasswordDialog = false
                            snackBarHostState.showSnackbar(getString(Res.string.reset_password))
                        }
                    }
                }

                else -> Unit
            }
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
                                    signInViewModel.onEvent(AuthenticationUIEvent.OnEmailChanged(it))
                                },
                                onPasswordChanged = {
                                    signInViewModel.onEvent(
                                        AuthenticationUIEvent.OnPasswordChanged(
                                            it
                                        )
                                    )
                                },
                                onForgotPasswordClicked = { displayResetPasswordDialog = true },
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
                                onNavigateToOppositeScreen = { onNavigate("sign_up") },
                                onContinueAsGuest = {
                                    if (dontAskCheckboxValue) {
                                        onNavigate("movies")
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
                    onConfirm = { signInViewModel.onEvent(AuthenticationUIEvent.OnResetPasswordConfirmed) }
                ) {
                    PrimaryTextField(
                        text = formState.emailReset,
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
                        onNavigate("sign_up")
                    },
                    onDismiss = {
                        displayGuestDialog = false
                    },
                    onConfirm = {
                        displayGuestDialog = false
                        onNavigate("movies")
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
                                    signInViewModel.onEvent(
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
