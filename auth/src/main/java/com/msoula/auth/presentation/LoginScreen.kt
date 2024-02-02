package com.msoula.auth.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msoula.auth.data.LoginFormState
import com.msoula.component.HMMButtonAuthComponent
import com.msoula.component.HMMErrorText
import com.msoula.component.HMMTextFieldAuthComponent
import com.msoula.component.HeaderTextComponent
import com.msoula.di.data.StringResourcesProviderImpl
import com.msoula.theme.HobbyMatchmakerTheme
import kotlinx.coroutines.launch

import com.msoula.auth.R.string as StringRes

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    circularProgressLoading: Boolean,
    loginFormState: LoginFormState,
    onEmailChanged: (email: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onHideForgotPasswordDialog: () -> Unit,
    onLogIn: () -> Unit,
    redirectToSignUpScreen: () -> Unit,
    onResetPasswordConfirmed: () -> Unit,
    openResetDialog: Boolean,
    onEmailResetChanged: (String) -> Unit,
    emailResetSent: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val resourcesProvider = StringResourcesProviderImpl(context)

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->

        LaunchedEffect(emailResetSent) {
            if (emailResetSent) {
                onHideForgotPasswordDialog()
                scope.launch {
                    snackBarHostState.showSnackbar(message = resourcesProvider.getString(StringRes.email_sent))
                }
            }
        }

        Column {
            HeaderTextComponent(
                text = stringResource(id = StringRes.welcome_back_title)
            )

            if (openResetDialog) {
                ForgotPasswordAlertDialog(
                    email = loginFormState.emailReset,
                    onDismissRequest = onHideForgotPasswordDialog,
                    onResetPasswordConfirmed = onResetPasswordConfirmed,
                    paddingValues = paddingValues,
                    enableSubmit = loginFormState.submitEmailReset,
                    onEmailChanged = onEmailResetChanged
                )
            }

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    loginFormState.logInError?.let {
                        HMMErrorText(modifier = modifier, errorText = it)
                    }

                    HMMTextFieldAuthComponent(
                        modifier = modifier,
                        value = loginFormState.email,
                        placeHolderText = stringResource(StringRes.email),
                        onValueChange = { onEmailChanged(it) }
                    )

                    Spacer(modifier = modifier.height(8.dp))

                    HMMTextFieldAuthComponent(
                        modifier = modifier,
                        value = loginFormState.password,
                        placeHolderText = stringResource(id = StringRes.password),
                        onValueChange = { onPasswordChanged(it) },
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = modifier.height(16.dp))

                    ClickableText(
                        text = AnnotatedString(stringResource(id = StringRes.forgot_password)),
                        onClick = { onForgotPasswordClicked() },
                        style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                        modifier = modifier
                            .wrapContentSize()
                            .align(Alignment.End)
                            .padding(end = 16.dp)
                    )

                    Spacer(modifier = modifier.height(40.dp))
                    HMMButtonAuthComponent(
                        modifier = modifier,
                        onClick = { onLogIn() },
                        text = stringResource(id = StringRes.log_in),
                        enabled = loginFormState.submit
                    )
                    Spacer(modifier = modifier.height(16.dp))
                    HMMButtonAuthComponent(
                        onClick = { redirectToSignUpScreen() },
                        text = stringResource(id = StringRes.new_member),
                        enabled = true
                    )
                }

                if (circularProgressLoading) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview(modifier: Modifier = Modifier) {
    HobbyMatchmakerTheme {
        LoginScreen(
            circularProgressLoading = false,
            loginFormState = LoginFormState(email = "test@test.fr"),
            onEmailChanged = {},
            onPasswordChanged = {},
            onLogIn = { },
            redirectToSignUpScreen = {},
            onForgotPasswordClicked = {},
            onResetPasswordConfirmed = {},
            onHideForgotPasswordDialog = {},
            openResetDialog = false,
            onEmailResetChanged = {},
            emailResetSent = false
        )
    }
}

@Composable
fun ForgotPasswordAlertDialog(
    modifier: Modifier = Modifier,
    email: String,
    onEmailChanged: (email: String) -> Unit,
    onDismissRequest: () -> Unit,
    onResetPasswordConfirmed: () -> Unit,
    paddingValues: PaddingValues,
    enableSubmit: Boolean
) {
    AlertDialog(
        modifier = modifier.padding(paddingValues),
        title = {
            Text(
                text = stringResource(id = StringRes.forgot_password_title),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        },
        text = {
            HMMTextFieldAuthComponent(
                value = email,
                onValueChange = { onEmailChanged(it) },
                placeHolderText = stringResource(StringRes.your_email),
                modifier = modifier.fillMaxWidth()
            )
        },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            Button(onClick = { onResetPasswordConfirmed() }, enabled = enableSubmit) {
                Text(text = stringResource(id = StringRes.reset_password))
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismissRequest() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text(text = stringResource(id = StringRes.cancel))
            }
        }
    )
}

