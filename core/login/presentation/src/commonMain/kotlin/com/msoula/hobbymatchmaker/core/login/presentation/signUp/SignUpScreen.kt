package com.msoula.hobbymatchmaker.core.login.presentation.signUp

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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.component.HMMButtonAuthComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMFormHelperText
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldAuthComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldPasswordComponent
import com.msoula.hobbymatchmaker.core.design.component.HeaderTextComponent
import com.msoula.hobbymatchmaker.core.login.presentation.Res
import com.msoula.hobbymatchmaker.core.login.presentation.already_a_member
import com.msoula.hobbymatchmaker.core.login.presentation.already_a_member_connect
import com.msoula.hobbymatchmaker.core.login.presentation.at_least
import com.msoula.hobbymatchmaker.core.login.presentation.email
import com.msoula.hobbymatchmaker.core.login.presentation.example
import com.msoula.hobbymatchmaker.core.login.presentation.firstname
import com.msoula.hobbymatchmaker.core.login.presentation.hide_password
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import com.msoula.hobbymatchmaker.core.login.presentation.models.SignUpEvent
import com.msoula.hobbymatchmaker.core.login.presentation.password
import com.msoula.hobbymatchmaker.core.login.presentation.password_hint
import com.msoula.hobbymatchmaker.core.login.presentation.show_password
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.models.SignUpStateModel
import com.msoula.hobbymatchmaker.core.login.presentation.sign_up
import com.msoula.hobbymatchmaker.core.login.presentation.welcome_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    redirectToLogInScreen: () -> Unit,
    redirectToAppScreen: () -> Unit,
    signUpViewModel: SignUpViewModel
) {

    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val registrationState by signUpViewModel.formDataFlow.collectAsState()
    val isLoading by signUpViewModel.isLoading.collectAsState()
    val signUpState by signUpViewModel.signUpState.collectAsState()

    val emailTipVisibility = remember { mutableStateOf(false) }
    val passwordTipVisibility = remember { mutableStateOf(false) }

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

    LaunchedEffect(signUpState) {
        when (signUpState) {
            is SignUpEvent.Success -> redirectToAppScreen()
            is SignUpEvent.Error -> {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar((signUpState as SignUpEvent.Error).message)
                }
            }

            else -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackBarHostState) }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                HeaderTextComponent(text = stringResource(Res.string.welcome_title))
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
                    isLoading = isLoading,
                    emailTipVisibility = emailTipVisibility,
                    passwordTipVisibility = passwordTipVisibility
                )
            }

            SignUpScreenBottomContent(
                redirectText = annotatedString,
                redirectToLogInScreen = { redirectToLogInScreen() })
        }
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
    isLoading: Boolean = false,
    emailTipVisibility: MutableState<Boolean> = mutableStateOf(false),
    passwordTipVisibility: MutableState<Boolean> = mutableStateOf(false)
) {
    Box(
        modifier =
        Modifier
            .wrapContentSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            HMMTextFieldAuthComponent(
                placeHolderText = stringResource(Res.string.firstname),
                value = registrationState.firstName.trimEnd(),
                onValueChange = {
                    onNameChanged(it)
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
            Spacer(modifier = Modifier.height(8.dp))

            HMMFormHelperText(
                isVisible = emailTipVisibility.value,
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
                placeHolderText = stringResource(Res.string.email)
            )
            Spacer(modifier = Modifier.height(8.dp))

            HMMFormHelperText(
                isVisible = passwordTipVisibility.value,
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
                placeholder = stringResource(Res.string.password),
                showPasswordContentDescription = stringResource(Res.string.show_password),
                hidePasswordContentDescription = stringResource(Res.string.hide_password)
            )

            Spacer(modifier = Modifier.height(32.dp))
            HMMButtonAuthComponent(
                onClick = { onSignUpClicked() },
                enabled = registrationState.submit,
                text = stringResource(Res.string.sign_up),
                loading = isLoading
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
        ClickableText(
            modifier = Modifier.wrapContentSize(),
            text = redirectText,
            onClick = { redirectToLogInScreen() },
            style = TextStyle(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}
