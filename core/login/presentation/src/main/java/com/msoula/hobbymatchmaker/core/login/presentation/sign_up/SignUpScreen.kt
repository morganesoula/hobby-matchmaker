package com.msoula.hobbymatchmaker.core.login.presentation.sign_up

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msoula.hobbymatchmaker.core.design.component.HMMButtonAuthComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMErrorText
import com.msoula.hobbymatchmaker.core.design.component.HMMFormHelperText
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldAuthComponent
import com.msoula.hobbymatchmaker.core.design.component.HMMTextFieldPasswordComponent
import com.msoula.hobbymatchmaker.core.design.component.HeaderTextComponent
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEventModel

import com.msoula.hobbymatchmaker.core.login.presentation.R.string as StringRes

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    redirectToLogInScreen: () -> Unit,
    signUpViewModel: SignUpViewModel = hiltViewModel<SignUpViewModel>()
) {
    val registrationState by signUpViewModel.formDataFlow.collectAsStateWithLifecycle()
    val signUpProgressLoading by signUpViewModel.signUpCircularProgress.collectAsStateWithLifecycle()

    val emailTipVisibility = remember { mutableStateOf(false) }
    val passwordTipVisibility = remember { mutableStateOf(false) }

    Scaffold(modifier = modifier) { paddingValues ->
        Column {
            HeaderTextComponent(text = stringResource(id = StringRes.welcome_title))
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (registrationState.signUpError != null) {
                        HMMErrorText(
                            modifier = Modifier,
                            errorText = registrationState.signUpError!!,
                        )
                    }

                    HMMTextFieldAuthComponent(
                        placeHolderText = stringResource(id = StringRes.firstname),
                        value = registrationState.firstName.trimEnd(),
                        onValueChange = {
                            signUpViewModel.onEvent(AuthenticationUIEventModel.OnFirstNameChanged(it))
                        },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    HMMTextFieldAuthComponent(
                        value = registrationState.lastName.trimEnd(),
                        onValueChange = {
                            signUpViewModel.onEvent(AuthenticationUIEventModel.OnLastNameChanged(it))
                        },
                        placeHolderText = stringResource(id = StringRes.lastname),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    HMMFormHelperText(
                        isVisible = emailTipVisibility.value,
                        titleHint = stringResource(id = StringRes.example),
                        hint = "john@test.com",
                    )

                    HMMTextFieldAuthComponent(
                        modifier =
                        modifier.onFocusChanged {
                            emailTipVisibility.value = it.isFocused
                        },
                        value = registrationState.email.trimEnd(),
                        onValueChange = {
                            signUpViewModel.onEvent(AuthenticationUIEventModel.OnEmailChanged(it))
                        },
                        placeHolderText = stringResource(id = StringRes.email),
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    HMMFormHelperText(
                        isVisible = passwordTipVisibility.value,
                        titleHint = stringResource(id = StringRes.at_least),
                        hint = stringResource(id = StringRes.password_hint),
                    )

                    HMMTextFieldPasswordComponent(
                        modifier =
                        Modifier.onFocusChanged {
                            passwordTipVisibility.value = it.isFocused
                        },
                        value = registrationState.password,
                        onValueChange = {
                            signUpViewModel.onEvent(AuthenticationUIEventModel.OnPasswordChanged(it))
                        },
                        placeholder = stringResource(id = StringRes.password),
                        showPasswordContentDescription = stringResource(id = StringRes.show_password),
                        hidePasswordContentDescription = stringResource(id = StringRes.hide_password),
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    HMMButtonAuthComponent(
                        onClick = {
                            signUpViewModel.onEvent(AuthenticationUIEventModel.OnSignUp)
                        },
                        enabled = registrationState.submit,
                        text = stringResource(id = StringRes.sign_up),
                        loading = signUpProgressLoading,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HMMButtonAuthComponent(
                        onClick = { redirectToLogInScreen() },
                        text = stringResource(id = StringRes.already_a_member),
                        enabled = true,
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
        redirectToLogInScreen = {},
    )
}
