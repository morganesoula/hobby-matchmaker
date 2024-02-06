package com.msoula.auth.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msoula.auth.data.AuthUIEvent
import com.msoula.auth.data.SignUpRegistrationState
import com.msoula.component.HMMButtonAuthComponent
import com.msoula.component.HMMErrorText
import com.msoula.component.HMMFormHelperText
import com.msoula.component.HMMTextFieldAuthComponent
import com.msoula.component.HeaderTextComponent
import com.msoula.auth.R.string as StringRes

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    registrationState: SignUpRegistrationState,
    signUpProgressLoading: Boolean,
    authUIEvent: (AuthUIEvent) -> Unit,
    redirectToLogInScreen: () -> Unit
) {
    val emailTipVisibility = remember { mutableStateOf(false) }
    val passwordTipVisibility = remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column {
            HeaderTextComponent(text = stringResource(id = StringRes.welcome_title))
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = modifier.verticalScroll(rememberScrollState())) {
                    if (registrationState.signUpError != null) {
                        HMMErrorText(modifier = modifier, errorText = registrationState.signUpError)
                    }

                    HMMTextFieldAuthComponent(
                        modifier = modifier,
                        placeHolderText = stringResource(id = StringRes.firstname),
                        value = registrationState.firstName.trimEnd(),
                        onValueChange = { authUIEvent(AuthUIEvent.OnFirstNameChanged(it)) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    Spacer(modifier = modifier.height(8.dp))

                    HMMTextFieldAuthComponent(
                        modifier = modifier,
                        value = registrationState.lastName.trimEnd(),
                        onValueChange = { authUIEvent(AuthUIEvent.OnLastNameChanged(it)) },
                        placeHolderText = stringResource(id = StringRes.lastname),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    Spacer(modifier = modifier.height(8.dp))

                    HMMFormHelperText(
                        modifier = modifier,
                        isVisible = emailTipVisibility.value,
                        titleHint = stringResource(id = StringRes.example),
                        hint = "john@test.com"
                    )

                    HMMTextFieldAuthComponent(
                        modifier = modifier.onFocusChanged {
                            emailTipVisibility.value = it.isFocused
                        },
                        value = registrationState.email.trimEnd(),
                        onValueChange = { authUIEvent(AuthUIEvent.OnEmailChanged(it)) },
                        placeHolderText = stringResource(id = StringRes.email)
                    )
                    Spacer(modifier = modifier.height(8.dp))

                    HMMFormHelperText(
                        modifier = modifier,
                        isVisible = passwordTipVisibility.value,
                        titleHint = stringResource(id = StringRes.at_least),
                        hint = stringResource(id = StringRes.password_hint)
                    )

                    HMMTextFieldAuthComponent(
                        modifier = modifier.onFocusChanged {
                            passwordTipVisibility.value = it.isFocused
                        },
                        value = registrationState.password.trimEnd(),
                        onValueChange = { authUIEvent(AuthUIEvent.OnPasswordChanged(it)) },
                        placeHolderText = stringResource(id = StringRes.password),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = modifier.height(32.dp))
                    HMMButtonAuthComponent(
                        onClick = { authUIEvent(AuthUIEvent.OnSignUp) },
                        enabled = registrationState.submit,
                        text = stringResource(id = StringRes.sign_up),
                        loading = signUpProgressLoading
                    )
                    Spacer(modifier = modifier.height(16.dp))
                    HMMButtonAuthComponent(
                        onClick = { redirectToLogInScreen() },
                        text = stringResource(id = StringRes.already_a_member),
                        enabled = true
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(
        registrationState = SignUpRegistrationState(),
        signUpProgressLoading = false,
        authUIEvent = {},
        redirectToLogInScreen = {}
    )
}