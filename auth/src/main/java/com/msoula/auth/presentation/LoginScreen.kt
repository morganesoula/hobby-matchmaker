package com.msoula.auth.presentation

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.msoula.auth.data.AuthUIEvent
import com.msoula.component.HMMButtonAuthComponent
import com.msoula.component.HMMErrorText
import com.msoula.component.HMMTextFieldAuthComponent
import com.msoula.component.HMMTextFieldPasswordComponent
import com.msoula.component.HeaderTextComponent
import com.msoula.di.data.StringResourcesProviderImpl
import kotlinx.coroutines.launch
import com.msoula.auth.R.string as StringRes

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel<LoginViewModel>(),
    redirectToSignUpScreen: () -> Unit,
    redirectToHomeScreen: () -> Unit,
    googleAuthUIClient: GoogleAuthUIClient,
) {
    val context = LocalContext.current

    val loginFormState by loginViewModel.formDataFlow.collectAsStateWithLifecycle()
    val circularProgressLoading by loginViewModel.circularProgressLoading.collectAsStateWithLifecycle()
    val openResetDialog by loginViewModel.openResetDialog.collectAsStateWithLifecycle()
    val emailResetSent by loginViewModel.resettingEmailSent.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val resourcesProvider = StringResourcesProviderImpl(context)
    val callBackManager = remember { CallbackManager.Factory.create() }
    val loginManager = LoginManager.getInstance()

    val facebookLauncher =
        rememberLauncherForActivityResult(
            contract = loginManager.createLogInActivityResultContract(callBackManager),
        ) { result ->
            loginManager.onActivityResult(
                result.resultCode,
                result.data,
                object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        Log.d("HMM", "Cancelled facebook login on user part")
                    }

                    override fun onError(error: FacebookException) {
                        Toast.makeText(context, "Facebook error login", Toast.LENGTH_LONG).show()
                    }

                    override fun onSuccess(result: LoginResult) {
                        redirectToHomeScreen()
                    }
                },
            )
        }

    val annotatedString =
        buildAnnotatedString {
            append(stringResource(id = StringRes.new_member) + "  ")
            withStyle(
                style =
                    SpanStyle(
                        color = if (isSystemInDarkTheme()) Color(0, 191, 255) else Color.Blue,
                        textDecoration = TextDecoration.Underline,
                    ),
            ) {
                append(stringResource(id = StringRes.new_member_clickable_part))
            }
        }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { paddingValues ->

        LaunchedEffect(emailResetSent) {
            if (emailResetSent) {
                loginViewModel.onEvent(AuthUIEvent.HideForgotPasswordDialog)
                scope.launch {
                    snackBarHostState.showSnackbar(message = resourcesProvider.getString(StringRes.email_sent))
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
            ) {
                if (openResetDialog) {
                    ForgotPasswordAlertDialog(
                        email = loginFormState.emailReset,
                        paddingValues = paddingValues,
                        enableSubmit = loginFormState.submitEmailReset,
                        authUIEvent = loginViewModel::onEvent,
                    )
                }

                HeaderTextComponent(
                    text = stringResource(id = StringRes.welcome_back_title),
                )

                if (loginFormState.logInError != null) {
                    HMMErrorText(errorText = loginFormState.logInError!!)
                }

                HMMTextFieldAuthComponent(
                    value = loginFormState.email.trimEnd(),
                    placeHolderText = stringResource(StringRes.email),
                    onValueChange = {
                        loginViewModel.onEvent(AuthUIEvent.OnEmailChanged(it))
                    },
                )

                Spacer(modifier = modifier.height(8.dp))

                HMMTextFieldPasswordComponent(
                    value = loginFormState.password,
                    onValueChange = {
                        loginViewModel.onEvent(AuthUIEvent.OnPasswordChanged(it))
                    },
                    placeholder = stringResource(id = StringRes.password),
                    showPasswordContentDescription = stringResource(id = StringRes.show_password),
                    hidePasswordContentDescription = stringResource(id = StringRes.hide_password),
                )

                Spacer(modifier = Modifier.height(16.dp))

                ClickableText(
                    text = AnnotatedString(stringResource(id = StringRes.forgot_password)),
                    onClick = {
                        loginViewModel.onEvent(AuthUIEvent.OnForgotPasswordClicked)
                    },
                    style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                    modifier =
                        Modifier
                            .wrapContentSize()
                            .align(Alignment.End)
                            .padding(end = 16.dp),
                )

                Spacer(modifier = Modifier.height(40.dp))

                HMMButtonAuthComponent(
                    onClick = {
                        loginViewModel.onEvent(AuthUIEvent.OnLogIn)
                    },
                    text = stringResource(id = StringRes.log_in),
                    enabled = loginFormState.submit,
                    loading = circularProgressLoading,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    HorizontalDivider(
                        Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        thickness = 2.dp,
                    )
                    Text(
                        text = resourcesProvider.getString(StringRes.continue_with_rs),
                        modifier = Modifier.weight(3f),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    )

                    HorizontalDivider(
                        Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        thickness = 2.dp,
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                HMMSocialMediaRow(
                    onFacebookButtonClicked = {
                        facebookLauncher.launch(
                            listOf(
                                "email",
                                "public_profile",
                            ),
                        )
                    },
                    googleAuthUIClient = googleAuthUIClient,
                    onGoogleSignInEvent = { signInResult ->
                        loginViewModel.onGoogleSignInEvent(
                            signInResult,
                        )
                    },
                )
            }

            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
            ) {
                ClickableText(
                    modifier =
                        Modifier
                            .wrapContentSize(),
                    text = annotatedString,
                    onClick = { redirectToSignUpScreen() },
                    style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                )
            }
        }
    }
}

@Composable
fun ForgotPasswordAlertDialog(
    modifier: Modifier = Modifier,
    email: String,
    authUIEvent: (AuthUIEvent) -> Unit,
    paddingValues: PaddingValues,
    enableSubmit: Boolean,
) {
    AlertDialog(
        modifier = modifier.padding(paddingValues),
        title = {
            Text(
                text = stringResource(id = StringRes.forgot_password_title),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            HMMTextFieldAuthComponent(
                value = email,
                onValueChange = { authUIEvent(AuthUIEvent.OnEmailResetChanged(it)) },
                placeHolderText = stringResource(StringRes.your_email),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        onDismissRequest = { authUIEvent(AuthUIEvent.HideForgotPasswordDialog) },
        confirmButton = {
            Button(
                onClick = { authUIEvent(AuthUIEvent.OnResetPasswordConfirmed) },
                enabled = enableSubmit,
            ) {
                Text(text = stringResource(id = StringRes.reset_password))
            }
        },
        dismissButton = {
            Button(
                onClick = { authUIEvent(AuthUIEvent.HideForgotPasswordDialog) },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ),
            ) {
                Text(text = stringResource(id = StringRes.cancel))
            }
        },
    )
}
