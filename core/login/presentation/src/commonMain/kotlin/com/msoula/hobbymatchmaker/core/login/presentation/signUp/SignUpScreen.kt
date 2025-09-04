package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.CallOnceEffect
import com.msoula.hobbymatchmaker.core.common.ObserveEvents
import com.msoula.hobbymatchmaker.core.common.SnackEffect
import com.msoula.hobbymatchmaker.core.design.component.HMMButtonAuthComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMFormHelperText
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldAuthComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldPasswordComponent
import com.msoula.hobbymatchmaker.core.design.component.HeaderTextComponent
import com.msoula.hobbymatchmaker.core.design.component.LoadingOverlay
import com.msoula.hobbymatchmaker.core.design.component.keyboardDismissOnTap
import com.msoula.hobbymatchmaker.core.login.presentation.Res
import com.msoula.hobbymatchmaker.core.login.presentation.already_a_member
import com.msoula.hobbymatchmaker.core.login.presentation.already_a_member_connect
import com.msoula.hobbymatchmaker.core.login.presentation.at_least
import com.msoula.hobbymatchmaker.core.login.presentation.email
import com.msoula.hobbymatchmaker.core.login.presentation.example
import com.msoula.hobbymatchmaker.core.login.presentation.firstname
import com.msoula.hobbymatchmaker.core.login.presentation.hide_password
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthUiEventModel
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import com.msoula.hobbymatchmaker.core.login.presentation.password
import com.msoula.hobbymatchmaker.core.login.presentation.password_hint
import com.msoula.hobbymatchmaker.core.login.presentation.show_password
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.models.SignUpStateModel
import com.msoula.hobbymatchmaker.core.login.presentation.sign_up
import com.msoula.hobbymatchmaker.core.login.presentation.welcome_subtitle
import com.msoula.hobbymatchmaker.core.login.presentation.welcome_title
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

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

    val annotatedString =
        buildAnnotatedString {
            append(stringResource(Res.string.already_a_member) + " ")
            withStyle(
                style =
                    SpanStyle(
                        color = if (isSystemInDarkTheme()) Color(0, 191, 255)
                        else Color.Blue,
                        textDecoration = TextDecoration.Underline
                    )
            ) {
                append(stringResource(Res.string.already_a_member_connect))
            }
        }

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

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = data.visuals.message)
                    }
                }
            )
        }
    )
    { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.keyboardDismissOnTap()
            ) {
                HeaderTextComponent(
                    title = stringResource(Res.string.welcome_title),
                    subtitle = stringResource(Res.string.welcome_subtitle)
                )

                Spacer(modifier = Modifier.height(16.dp))

                SignUpScreenMainContent(
                    paddingValues = paddingValues,
                    registrationState = registrationState,
                    onNameChanged = {
                        signUpViewModel.onEvent(
                            AuthenticationUIEvent.OnFirstNameChanged(
                                it
                            )
                        )
                    },
                    onEmailChanged = {
                        signUpViewModel.onEvent(
                            AuthenticationUIEvent.OnEmailChanged(
                                it
                            )
                        )
                    },
                    onPasswordChanged = {
                        signUpViewModel.onEvent(
                            AuthenticationUIEvent.OnPasswordChanged(
                                it
                            )
                        )
                    },
                    onSignUpClicked = { signUpViewModel.onEvent(AuthenticationUIEvent.OnSignUp) },
                    signUpState = signUpState
                )
            }

            SignUpScreenBottomContent(
                redirectText = annotatedString,
                redirectToLogInScreen = {
                    signUpViewModel.onEvent(AuthenticationUIEvent.OnScreenChanged)
                    redirectToSignInScreen()
                })
        }

        LoadingOverlay(visible = signUpState == SignUpEvent.Loading)
    }
}

@Composable
fun SignUpScreenMainContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    registrationState: SignUpStateModel,
    onNameChanged: (name: String) -> Unit,
    onEmailChanged: (email: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,
    onSignUpClicked: () -> Unit,
    signUpState: SignUpEvent
) {
    val scrollState = rememberScrollState()
    val emailTipVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordTipVisibility = rememberSaveable { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .wrapContentSize()
                .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            HMMTextFieldAuthComponent(
                label = stringResource(Res.string.firstname),
                value = registrationState.firstName.trimEnd(),
                onValueChange = {
                    onNameChanged(it)
                },
                icon = Icons.Default.People,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            HMMFormHelperText(
                isVisible = emailTipVisibility,
                titleHint = stringResource(Res.string.example),
                hint = "john@test.com"
            )

            HMMTextFieldAuthComponent(
                modifier =
                    modifier.onFocusChanged {
                        emailTipVisibility.value = it.isFocused
                    },
                value = registrationState.email.trimEnd(),
                onValueChange = {
                    onEmailChanged(it)
                },
                icon = Icons.Default.Email,
                label = stringResource(Res.string.email),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    autoCorrect = false
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            HMMFormHelperText(
                isVisible = passwordTipVisibility,
                titleHint = stringResource(Res.string.at_least),
                hint = stringResource(Res.string.password_hint)
            )

            HMMTextFieldPasswordComponent(
                modifier =
                    Modifier.onFocusChanged {
                        passwordTipVisibility.value = it.isFocused
                    },
                value = registrationState.password,
                onValueChange = {
                    onPasswordChanged(it)
                },
                leadingIcon = Icons.Default.Lock,
                label = stringResource(Res.string.password),
                showPasswordContentDescription = stringResource(Res.string.show_password),
                hidePasswordContentDescription = stringResource(Res.string.hide_password),
                onFormDoneClicked = onSignUpClicked
            )

            Spacer(modifier = Modifier.height(32.dp))
            HMMButtonAuthComponent(
                onClick = { onSignUpClicked() },
                enabled = registrationState.submit,
                text = stringResource(Res.string.sign_up),
                loading = signUpState == SignUpEvent.Loading
            )
        }
    }
}

@Composable
fun BoxScope.SignUpScreenBottomContent(
    modifier: Modifier = Modifier,
    redirectText: AnnotatedString,
    redirectToLogInScreen: () -> Unit
) {
    Box(
        modifier = modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = redirectText,
            modifier = Modifier
                .wrapContentSize()
                .semantics {
                    role = Role.Button
                    contentDescription = redirectText.text
                }
                .clickable { redirectToLogInScreen() },
            style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}
