package com.msoula.hobbymatchmaker.core.login.presentation.sign_in

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.msoula.hobbymatchmaker.core.common.ObserveAsEvents
import com.msoula.hobbymatchmaker.core.design.component.HMMButtonAuthComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMErrorText
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldAuthComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldPasswordComponent
import com.msoula.hobbymatchmaker.core.design.component.HeaderTextComponent
import com.msoula.hobbymatchmaker.core.design.component.LocalSnackBar
import com.msoula.hobbymatchmaker.core.di.data.StringResourcesProviderImpl
import com.msoula.hobbymatchmaker.core.login.presentation.components.SocialMediaRowCustom
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.msoula.hobbymatchmaker.core.login.presentation.R.string as StringRes

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    signInViewModel: SignInViewModel,
    oneTimeEventChannelFlow: Flow<AuthenticationEvent>,
    redirectToAppScreen: () -> Unit,
    redirectToSignUpScreen: () -> Unit,
    handleFacebookAccessToken: (credential: AuthCredential, email: String) -> Unit,
    handleGoogleSignIn: (result: GetCredentialResponse?, googleAuthClient: GoogleAuthClient) -> Unit,
    googleAuthClient: GoogleAuthClient
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val loginFormState by signInViewModel.formDataFlow.collectAsStateWithLifecycle()
    val circularProgressLoading by signInViewModel.circularProgressLoading.collectAsStateWithLifecycle()
    val openResetDialog by signInViewModel.openResetDialog.collectAsStateWithLifecycle()
    val emailResetSent by signInViewModel.resettingEmailSent.collectAsStateWithLifecycle()

    val callBackManager = remember { CallbackManager.Factory.create() }
    val resourcesProvider = StringResourcesProviderImpl(context)
    val loginManager = LoginManager.getInstance()

    val snackBarHostState = remember { SnackbarHostState() }

    val facebookLauncher =
        rememberLauncherForActivityResult(
            loginManager.createLogInActivityResultContract(callBackManager, null)
        ) {}

    val annotatedString = createAnnotatedString(isDarkTheme = isSystemInDarkTheme())

    RegisterFacebookCallback(
        loginManager = loginManager,
        callBackManager = callBackManager,
        coroutineScope = coroutineScope,
        handleFacebookAccessToken = handleFacebookAccessToken,
        context = context
    )

    ObserveAsEvents(oneTimeEventChannelFlow) { event ->
        coroutineScope.launch {
            when (event) {
                is AuthenticationEvent.OnFacebookFailedConnection, is AuthenticationEvent.OnGoogleFailedConnection, is AuthenticationEvent.OnResetPasswordFailed -> {
                    snackBarHostState.showSnackbar(event.message)
                }

                AuthenticationEvent.OnSignInSuccess -> redirectToAppScreen()
                AuthenticationEvent.OnSignUpClicked -> redirectToSignUpScreen()
                else -> Unit
            }
        }
    }

    CompositionLocalProvider(LocalSnackBar provides snackBarHostState) {
        Scaffold(
            modifier = modifier
        ) { paddingValues ->
            LaunchedEffect(emailResetSent) {
                if (emailResetSent) {
                    signInViewModel.onEvent(AuthenticationUIEvent.HideForgotPasswordDialog)
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            resourcesProvider.getString(
                                StringRes.email_sent
                            )
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier =
                    Modifier
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (openResetDialog) {
                        ForgotPasswordAlertDialog(
                            email = loginFormState.emailReset,
                            paddingValues = paddingValues,
                            enableSubmit = loginFormState.submitEmailReset,
                            authUIEvent = signInViewModel::onEvent
                        )
                    }

                    HeaderTextComponent(
                        text = stringResource(id = StringRes.welcome_back_title),
                    )

                    if (loginFormState.logInError != null) {
                        HMMErrorText(errorText = loginFormState.logInError!!)
                    }

                    SignInScreenMainContent(
                        email = loginFormState.email.trimEnd(),
                        onEmailChanged = {
                            signInViewModel.onEvent(
                                AuthenticationUIEvent.OnEmailChanged(
                                    it
                                )
                            )
                        },
                        password = loginFormState.password,
                        onPasswordChanged = {
                            signInViewModel.onEvent(AuthenticationUIEvent.OnPasswordChanged(it))
                        },
                        onForgotPasswordClicked = {
                            signInViewModel.onEvent(AuthenticationUIEvent.OnForgotPasswordClicked)
                        },
                        onSignInClicked = {
                            signInViewModel.onEvent(AuthenticationUIEvent.OnSignIn)
                        },
                        canSubmit = loginFormState.submit,
                        circularProgressLoading = circularProgressLoading,
                        dividerConnectText = resourcesProvider.getString(StringRes.continue_with_rs),
                        facebookLauncher = facebookLauncher,
                        scope = coroutineScope,
                        googleAuthClient = googleAuthClient,
                        handleGoogleSignIn = handleGoogleSignIn
                    )
                }

                Box(
                    modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    ClickableText(
                        modifier =
                        Modifier.wrapContentSize(),
                        text = annotatedString,
                        onClick = { redirectToSignUpScreen() },
                        style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
                    )
                }
            }
        }
    }
}

@Composable
fun RegisterFacebookCallback(
    loginManager: LoginManager,
    callBackManager: CallbackManager,
    coroutineScope: CoroutineScope,
    handleFacebookAccessToken: (credential: AuthCredential, email: String) -> Unit,
    context: Context
) {
    DisposableEffect(Unit) {
        loginManager.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                Log.d("HMM", "Cancelled facebook login on user part")
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(context, "Facebook error login", Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(result: LoginResult) {
                coroutineScope.launch {
                    val accessToken = result.accessToken

                    val email = fetchFacebookUserProfile(accessToken)

                    if (email != null) {
                        val credential = FacebookAuthProvider.getCredential(accessToken.token)
                        handleFacebookAccessToken(credential, email)
                    } else {
                        Log.e("HMM", "Failed to fetch Facebook user email")
                    }
                }
            }
        })

        onDispose {
            loginManager.unregisterCallback(callBackManager)
        }
    }
}

@Composable
fun createAnnotatedString(isDarkTheme: Boolean): AnnotatedString {
    val color = if (isDarkTheme) Color(0, 191, 255) else Color.Blue

    return buildAnnotatedString {
        append(stringResource(id = StringRes.new_member) + "  ")
        pushStyle(
            SpanStyle(
                color = color,
                textDecoration = TextDecoration.Underline,
            )
        )
        append(stringResource(id = StringRes.new_member_clickable_part))
        pop()
    }
}

@Composable
fun ColumnScope.SignInScreenMainContent(
    modifier: Modifier = Modifier,
    email: String,
    onEmailChanged: (String) -> Unit,
    password: String,
    onPasswordChanged: (String) -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onSignInClicked: () -> Unit,
    canSubmit: Boolean,
    circularProgressLoading: Boolean,
    dividerConnectText: String,
    facebookLauncher: ManagedActivityResultLauncher<Collection<String>, CallbackManager.ActivityResultParameters>,
    scope: CoroutineScope,
    googleAuthClient: GoogleAuthClient,
    handleGoogleSignIn: (result: GetCredentialResponse?, googleAuthClient: GoogleAuthClient) -> Unit
) {
    HMMTextFieldAuthComponent(
        value = email,
        placeHolderText = stringResource(StringRes.email),
        onValueChange = {
            onEmailChanged(it)
        },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = modifier.height(16.dp))

    HMMTextFieldPasswordComponent(
        value = password,
        onValueChange = {
            onPasswordChanged(it)
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = stringResource(id = StringRes.password),
        showPasswordContentDescription = stringResource(id = StringRes.show_password),
        hidePasswordContentDescription = stringResource(id = StringRes.hide_password)
    )

    Spacer(modifier = Modifier.height(16.dp))

    ClickableText(
        text = AnnotatedString(stringResource(id = StringRes.forgot_password)),
        onClick = {
            onForgotPasswordClicked()
        },
        style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
        modifier =
        Modifier
            .wrapContentSize()
            .align(Alignment.End)
            .padding(end = 16.dp)
    )

    Spacer(modifier = Modifier.height(40.dp))

    HMMButtonAuthComponent(
        onClick = {
            onSignInClicked()
        },
        text = stringResource(id = StringRes.log_in),
        enabled = canSubmit,
        loading = circularProgressLoading
    )

    Spacer(modifier = Modifier.height(40.dp))

    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
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

    Spacer(modifier = Modifier.height(32.dp))

    SocialMediaRowCustom(
        onFacebookButtonClicked = {
            facebookLauncher.launch(listOf("email", "public_profile"))
        },
        onGoogleButtonClicked = {
            scope.launch {
                val credential = googleAuthClient.launchGetCredential()
                handleGoogleSignIn(credential, googleAuthClient)
            }
        }
    )
}

@Composable
fun ForgotPasswordAlertDialog(
    modifier: Modifier = Modifier,
    email: String,
    authUIEvent: (AuthenticationUIEvent) -> Unit,
    paddingValues: PaddingValues,
    enableSubmit: Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current

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
                onValueChange = {
                    Log.d("HMM", "Changing reset email with: $it")
                    authUIEvent(AuthenticationUIEvent.OnEmailResetChanged(it))
                },
                placeHolderText = stringResource(StringRes.your_email),
                modifier = Modifier.fillMaxWidth()
            )
        },
        onDismissRequest = { authUIEvent(AuthenticationUIEvent.HideForgotPasswordDialog) },
        confirmButton = {
            Button(
                onClick = {
                    keyboardController?.hide()
                    authUIEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
                },
                enabled = enableSubmit
            ) {
                Text(text = stringResource(id = StringRes.reset_password))
            }
        },
        dismissButton = {
            Button(
                onClick = { authUIEvent(AuthenticationUIEvent.HideForgotPasswordDialog) },
                colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text(text = stringResource(id = StringRes.cancel))
            }
        }
    )
}

private suspend fun fetchFacebookUserProfile(
    accessToken: AccessToken
): String? {
    return suspendCancellableCoroutine { continuation ->
        val request = GraphRequest.newMeRequest(accessToken) { jsonObject, _ ->
            try {
                val email = jsonObject?.getString("email")
                continuation.resume(email)
            } catch (e: Exception) {
                Log.e("HMM", "Error fetching Facebook profile: ${e.message}")
                continuation.resume(null)
            }
        }

        val parameters = Bundle().apply { putString("fields", "email, name") }
        request.parameters = parameters
        request.executeAsync()

        continuation.invokeOnCancellation {
            request.callback = null
        }
    }
}
