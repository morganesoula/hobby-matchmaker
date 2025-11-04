package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msoula.hobbymatchmaker.core.design.CallOnceEffect
import com.msoula.hobbymatchmaker.core.design.ObserveEvents
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.SnackEffect
import com.msoula.hobbymatchmaker.core.design.cancel
import com.msoula.hobbymatchmaker.core.design.component.AppSpacing
import com.msoula.hobbymatchmaker.core.design.component.HMMButtonAuthComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldAuthComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldPasswordComponent
import com.msoula.hobbymatchmaker.core.design.component.HeaderTextComponent
import com.msoula.hobbymatchmaker.core.design.component.keyboardDismissOnTap
import com.msoula.hobbymatchmaker.core.design.continue_as_guest_button_title
import com.msoula.hobbymatchmaker.core.design.continue_with_rs
import com.msoula.hobbymatchmaker.core.design.email
import com.msoula.hobbymatchmaker.core.design.forgot_password
import com.msoula.hobbymatchmaker.core.design.forgot_password_title
import com.msoula.hobbymatchmaker.core.design.hide_password
import com.msoula.hobbymatchmaker.core.design.log_in
import com.msoula.hobbymatchmaker.core.design.new_member
import com.msoula.hobbymatchmaker.core.design.new_member_clickable_part
import com.msoula.hobbymatchmaker.core.design.password
import com.msoula.hobbymatchmaker.core.design.reset_password
import com.msoula.hobbymatchmaker.core.design.show_password
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.welcome_back_subtitle
import com.msoula.hobbymatchmaker.core.design.welcome_back_title
import com.msoula.hobbymatchmaker.core.design.your_email
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.components.GuestModeDialog
import com.msoula.hobbymatchmaker.core.login.presentation.components.SocialMediaButtonListPlatformSpecificUI
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthUiEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.ResetPasswordEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.models.SignInFormStateModel
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.background)
                .consumeWindowInsets(paddingValues)
                .verticalScroll(rememberScrollState())
                .keyboardDismissOnTap()
        ) {
            Box(Modifier.fillMaxWidth().padding(top = 4.dp)) {
                HeaderTextComponent(
                    title = stringResource(Res.string.welcome_back_title),
                    subtitle = stringResource(Res.string.welcome_back_subtitle)
                )
            }

            Spacer(Modifier.height(12.dp))

            SignInScreenMainContent(
                modifier = Modifier.fillMaxWidth(),
                signInFormState = signInFormState,
                onEvent = signInViewModel::onEvent,
                signInState = signInState,
                facebookUIClient = facebookUIClient,
                shouldShowGuestWarning = shouldShowGuestWarning,
                isGuestLoading = isGuestLoading,
                displayGuestDialog = { showGuestDialog = true },
                redirectToMovieScreen = redirectToMovieScreen,
                redirectToSignUpScreen = redirectToSignUpScreen
            )
        }

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
}

@Composable
fun AnnotatedStringWithLinkAnnotation(isDarkTheme: Boolean, onClick: () -> Unit) {
    val color = if (isDarkTheme) Color(0, 191, 255) else Color.Blue

    val annotatedString = buildAnnotatedString {
        append(stringResource(Res.string.new_member) + " ")
        pushStringAnnotation(tag = "clickable", annotation = "link")
        withStyle(style = SpanStyle(color = color, textDecoration = TextDecoration.Underline)) {
            append(stringResource(Res.string.new_member_clickable_part))
        }
        pop()
    }

    Text(
        text = annotatedString,
        style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
        modifier = Modifier
            .wrapContentSize()
            .semantics { role = Role.Button }
            .clickable {
                annotatedString
                    .getStringAnnotations("clickable", 0, annotatedString.length)
                    .firstOrNull()
                    ?.let { if (it.item == "link") onClick() }
            }
    )
}

@Composable
fun ColumnScope.SignInScreenMainContent(
    modifier: Modifier = Modifier,
    signInFormState: SignInFormStateModel,
    onEvent: (AuthenticationUIEvent) -> Unit,
    signInState: SignInEvent = SignInEvent.Idle,
    facebookUIClient: FacebookUIClient,
    shouldShowGuestWarning: Boolean,
    isGuestLoading: Boolean,
    displayGuestDialog: () -> Unit,
    redirectToMovieScreen: () -> Unit,
    redirectToSignUpScreen: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    HMMTextFieldAuthComponent(
        value = signInFormState.email.trimEnd(),
        label = stringResource(Res.string.email),
        icon = Icons.Default.Email,
        contentDescription = stringResource(Res.string.email),
        onValueChange = { onEvent(AuthenticationUIEvent.OnEmailChanged(it)) },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = modifier.height(AppSpacing.Sixteen))

    HMMTextFieldPasswordComponent(
        value = signInFormState.password,
        onValueChange = { onEvent(AuthenticationUIEvent.OnPasswordChanged(it)) },
        modifier = Modifier.fillMaxWidth(),
        label = stringResource(Res.string.password),
        leadingIcon = Icons.Default.Lock,
        showPasswordContentDescription = stringResource(Res.string.show_password),
        hidePasswordContentDescription = stringResource(Res.string.hide_password),
        onFormDoneClicked = { onEvent(AuthenticationUIEvent.OnSignIn) }
    )

    Spacer(modifier = Modifier.height(AppSpacing.Sixteen))

    ClickableText(
        text = AnnotatedString(stringResource(Res.string.forgot_password)),
        onClick = { onEvent(AuthenticationUIEvent.OnForgotPasswordClicked) },
        style = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            textDecoration = TextDecoration.Underline
        ),
        modifier = Modifier
            .wrapContentSize()
            .align(Alignment.End)
            .padding(end = 16.dp)
    )

    Spacer(modifier = Modifier.height(AppSpacing.Sixteen))

    HMMButtonAuthComponent(
        onClick = {
            keyboardController?.hide()
            onEvent(AuthenticationUIEvent.OnSignIn)
        },
        text = stringResource(Res.string.log_in),
        enabled = signInFormState.submit,
        loading = signInState == SignInEvent.Loading
    )

    Spacer(modifier = Modifier.height(AppSpacing.Sixteen))

    DividerRowComponent(modifier, stringResource(Res.string.continue_with_rs))

    Spacer(modifier = Modifier.height(AppSpacing.Sixteen))

    SocialMediaButtonListPlatformSpecificUI(
        modifier = Modifier.fillMaxWidth(),
        onEvent = onEvent,
        facebookUIClient = facebookUIClient
    )

    Spacer(Modifier.height(AppSpacing.TwentyFour))

    Column(
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AnnotatedStringWithLinkAnnotation(isSystemInDarkTheme()) {
                onEvent(AuthenticationUIEvent.OnScreenChanged)
                redirectToSignUpScreen()
            }
        }

        Spacer(Modifier.height(AppSpacing.FortyEight))

        OutlinedButton(
            onClick = {
                if (shouldShowGuestWarning) {
                    displayGuestDialog()
                } else {
                    onEvent(AuthenticationUIEvent.OnContinueAsGuestConfirmed(true))
                    redirectToMovieScreen()
                }
            },
            enabled = !isGuestLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            if (isGuestLoading) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(12.dp))
            } else {
                Text(
                    text = stringResource(Res.string.continue_as_guest_button_title),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DividerRowComponent(modifier: Modifier = Modifier, dividerConnectText: String) {
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
            text = dividerConnectText,
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
