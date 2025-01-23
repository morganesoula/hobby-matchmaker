package com.msoula.hobbymatchmaker.core.login.presentation.signIn

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.msoula.hobbymatchmaker.core.common.ObserveAsEvents
import com.msoula.hobbymatchmaker.core.design.component.HMMButtonAuthComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldAuthComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldPasswordComponent
import com.msoula.hobbymatchmaker.core.design.component.HeaderTextComponent
import com.msoula.hobbymatchmaker.core.login.presentation.components.SocialMediaRowCustom
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.ResetPasswordEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignInEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.msoula.hobbymatchmaker.core.login.presentation.R.string as StringRes

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    signInViewModel: SignInViewModel,
    oneTimeEventChannelFlow: Flow<AuthenticationEvent>,
    redirectToAppScreen: () -> Unit,
    redirectToSignUpScreen: () -> Unit,
    connectWithSocialMedia: (facebookAccessToken: AccessToken?, context: Context) -> Unit
) {

}

@Composable
fun SignInScreenContent(
    modifier: Modifier = Modifier,
    signInViewModel: SignInViewModel,
    oneTimeEventChannelFlow: Flow<AuthenticationEvent>,
    redirectToAppScreen: () -> Unit,
    redirectToSignUpScreen: () -> Unit,
    connectWithSocialMedia: (facebookAccessToken: AccessToken?, context: Context) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val loginFormState by signInViewModel.formDataFlow.collectAsStateWithLifecycle()
    val circularProgressLoading by
    signInViewModel.circularProgressLoading.collectAsStateWithLifecycle()
    val openResetDialog by signInViewModel.openResetDialog.collectAsStateWithLifecycle()
    val resetPasswordState by signInViewModel.resetPasswordState.collectAsStateWithLifecycle()
    val signInState by signInViewModel.signInState.collectAsStateWithLifecycle()

    val callBackManager = remember { CallbackManager.Factory.create() }
    val resourcesProvider = StringResourcesProviderImpl(context)
    val loginManager = LoginManager.getInstance()

    val snackBarHostState = remember { SnackbarHostState() }

    val facebookLauncher =
        rememberLauncherForActivityResult(
            loginManager.createLogInActivityResultContract(callBackManager, null)
        ) {}

    RegisterFacebookCallback(
        loginManager = loginManager,
        callBackManager = callBackManager,
        coroutineScope = coroutineScope,
        context = context,
        connectWithSocialMedia = connectWithSocialMedia
    )

    LaunchedEffect(resetPasswordState) {
        when (resetPasswordState) {
            is ResetPasswordEvent.Error -> coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    (resetPasswordState as ResetPasswordEvent.Error).message
                )
            }

            is ResetPasswordEvent.Success -> signInViewModel.onEvent(
                AuthenticationUIEvent.HideForgotPasswordDialog
            )

            else -> Unit
        }
    }

    LaunchedEffect(signInState) {
        when (signInState) {
            is SignInEvent.Error -> coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    (signInState as SignInEvent.Error).message
                )
            }

            is SignInEvent.Success -> redirectToAppScreen()
            else -> Unit
        }
    }

    ObserveAsEvents(oneTimeEventChannelFlow) { event ->
        coroutineScope.launch {
            when (event) {
                is AuthenticationEvent.OnFacebookFailedConnection,
                is AuthenticationEvent.OnGoogleFailedConnection -> {
                    snackBarHostState.showSnackbar(event.message)
                }

                AuthenticationEvent.OnSignInSuccess -> redirectToAppScreen()
                AuthenticationEvent.OnSignUpClicked -> redirectToSignUpScreen()
                else -> Unit
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
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
                        authUIEvent = signInViewModel::onEvent,
                        isLoading = resetPasswordState == ResetPasswordEvent.Loading
                    )
                }

                HeaderTextComponent(
                    text = stringResource(id = StringRes.welcome_back_title),
                )

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
                    connectWithSocialMedia = connectWithSocialMedia
                )
            }

            Box(
                modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            ) {
                AnnotatedStringWithLinkAnnotation(isSystemInDarkTheme()) {
                    redirectToSignUpScreen()
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
    connectWithSocialMedia: (facebookAccessToken: AccessToken?, context: Context) -> Unit,
    context: Context
) {
    val localContext = LocalContext.current

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
                    connectWithSocialMedia(accessToken, localContext)
                }
            }
        })

        onDispose {
            loginManager.unregisterCallback(callBackManager)
        }
    }
}

@Composable
fun AnnotatedStringWithLinkAnnotation(isDarkTheme: Boolean, onClick: () -> Unit) {
    val color = if (isDarkTheme) Color(0, 191, 255) else Color.Blue

    val annotatedString = buildAnnotatedString {
        append(stringResource(id = StringRes.new_member) + " ")

        pushStringAnnotation(
            tag = "clickable",
            annotation = "link"
        )
        withStyle(style = SpanStyle(color = color, textDecoration = TextDecoration.Underline)) {
            append(stringResource(id = StringRes.new_member_clickable_part))
        }
        pop()
    }

    Text(
        text = annotatedString,
        style = TextStyle(color = MaterialTheme.colorScheme.onBackground),
        modifier = Modifier
            .wrapContentSize()
            .clickable {
                annotatedString
                    .getStringAnnotations(
                        tag = "clickable",
                        start = 0,
                        end = annotatedString.length
                    )
                    .firstOrNull()
                    ?.let {
                        if (it.item == "link") {
                            onClick()
                        }
                    }
            }
    )
}

@Composable
fun ColumnScope.SignInScreenMainContent(
    modifier: Modifier = Modifier,
    email: String = "",
    onEmailChanged: (String) -> Unit,
    password: String = "",
    onPasswordChanged: (String) -> Unit,
    onForgotPasswordClicked: () -> Unit = {},
    onSignInClicked: () -> Unit,
    canSubmit: Boolean = false,
    circularProgressLoading: Boolean = false,
    dividerConnectText: String = "",
    facebookLauncher: ManagedActivityResultLauncher<Collection<String>,
        CallbackManager.ActivityResultParameters>,
    connectWithSocialMedia: (facebookAccessToken: AccessToken?, context: Context) -> Unit
) {
    val localContext = LocalContext.current

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

    DividerRowComponent(modifier, dividerConnectText)

    Spacer(modifier = Modifier.height(32.dp))

    SocialMediaRowCustom(
        onFacebookButtonClicked = {
            facebookLauncher.launch(listOf("email", "public_profile"))
        },
        onGoogleButtonClicked = {
            connectWithSocialMedia(null, localContext)
        }
    )
}

@Composable
fun DividerRowComponent(modifier: Modifier = Modifier, dividerConnectText: String) {
    Row(
        modifier =
        modifier
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
}

@Composable
fun ForgotPasswordAlertDialog(
    modifier: Modifier = Modifier,
    email: String,
    authUIEvent: (AuthenticationUIEvent) -> Unit,
    paddingValues: PaddingValues,
    enableSubmit: Boolean,
    isLoading: Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(
        modifier = modifier.padding(paddingValues),
        title = {
            Text(
                text = stringResource(
                    id =
                    StringRes.forgot_password_title
                ),
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
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        keyboardController?.hide()
                        authUIEvent(AuthenticationUIEvent.OnResetPasswordConfirmed)
                    },
                    enabled = enableSubmit
                ) {
                    Text(text = stringResource(id = StringRes.reset_password))
                }
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
