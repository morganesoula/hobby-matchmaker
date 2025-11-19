package com.msoula.hobbymatchmaker.core.login.presentation.signUp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.LoadingOverlay
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.atoms.asStringSuspend
import com.msoula.hobbymatchmaker.core.design.edit_profile_basic_information_requirements_no_number
import com.msoula.hobbymatchmaker.core.design.email_validation_correct_format
import com.msoula.hobbymatchmaker.core.design.email_validation_no_spaces
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirement
import com.msoula.hobbymatchmaker.core.design.organisms.AuthenticationScreenBottom
import com.msoula.hobbymatchmaker.core.design.organisms.AuthenticationScreenTop
import com.msoula.hobbymatchmaker.core.design.organisms.SignUpForm
import com.msoula.hobbymatchmaker.core.design.password_validation_eight_characters
import com.msoula.hobbymatchmaker.core.design.password_validation_one_number
import com.msoula.hobbymatchmaker.core.design.password_validation_one_special_character
import com.msoula.hobbymatchmaker.core.design.password_validation_one_upper_case
import com.msoula.hobbymatchmaker.core.design.templates.SignUpLayout
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.core.login.presentation.models.AuthenticationUIEvent
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    signUpViewModel: SignUpViewModel,
    onNavigate: (String) -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }

    val registrationState by signUpViewModel.formDataFlow.collectAsState()
    val signUpState by signUpViewModel.signUpState.collectAsState()

    val eightCharactersRequirement = stringResource(Res.string.password_validation_eight_characters)
    val oneNumberRequirement = stringResource(Res.string.password_validation_one_number)
    val oneUppercaseRequirement = stringResource(Res.string.password_validation_one_upper_case)
    val oneSpecialRequirement = stringResource(Res.string.password_validation_one_special_character)
    val noSpacesRequirement = stringResource(Res.string.email_validation_no_spaces)
    val emailFormatRequirement = stringResource(Res.string.email_validation_correct_format)
    val noNumberRequirement =
        stringResource(Res.string.edit_profile_basic_information_requirements_no_number)

    val nameRequirements = remember(registrationState) {
        derivedStateOf {
            listOf(
                ValidationRequirement(
                    text = noNumberRequirement,
                    isValid = registrationState.firstName.all { !it.isDigit() }
                ),
                ValidationRequirement(
                    text = noSpacesRequirement,
                    isValid = !registrationState.firstName.contains(Regex("\\s"))
                )
            )
        }
    }

    val emailRequirements = remember(registrationState) {
        derivedStateOf {
            listOf(
                ValidationRequirement(
                    text = noSpacesRequirement,
                    isValid = !registrationState.email.contains(Regex("\\s"))
                ),
                ValidationRequirement(
                    text = emailFormatRequirement,
                    isValid = registrationState.email
                        .trim()
                        .let {
                            it.isNotBlank() && Regex(
                                """^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$"""
                            ).matches(it)
                        }
                )
            )
        }
    }

    val passwordRequirements = remember(registrationState) {
        derivedStateOf {
            listOf(
                ValidationRequirement(
                    text = eightCharactersRequirement,
                    isValid = registrationState.password.length >= 8
                ),
                ValidationRequirement(
                    text = oneNumberRequirement,
                    isValid = registrationState.password.contains(Regex("[0-9]"))
                ),
                ValidationRequirement(
                    text = oneUppercaseRequirement,
                    isValid = registrationState.password.contains(Regex("[A-Z]"))
                ),
                ValidationRequirement(
                    text = oneSpecialRequirement,
                    isValid = registrationState.password.contains(Regex("[!-/:-@\\[-`{-~]"))
                ),
                ValidationRequirement(
                    text = noSpacesRequirement,
                    isValid = !registrationState.password.contains(Regex("\\s"))
                )
            )
        }
    }

    LaunchedEffect(signUpViewModel.events) {
        signUpViewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar ->
                    snackBarHostState.showSnackbar(event.message.asStringSuspend())

                is UiEvent.NavigateToRoute -> onNavigate(event.route)
                else -> {}
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
                        shape = RoundedCornerShape(CustomSize.Eight)
                    ) {
                        Text(text = data.visuals.message)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = modifier.padding(
                top = CustomSize.Sixteen,
                start = CustomSize.Sixteen,
                end = CustomSize.Sixteen
            )
        ) {
            StateContainer(
                state = signUpState,
                onLoading = { LoadingOverlay(signUpState is UiState.Loading) },
                onError = { _, _ -> },
                onEmpty = {},
                onSuccess = {
                    SignUpLayout(
                        paddingValues = padding,
                        topBar = { AuthenticationScreenTop(isSignInScreen = false) },
                        form = {
                            SignUpForm(
                                name = registrationState.firstName,
                                email = registrationState.email,
                                password = registrationState.password,
                                loading = signUpState is UiState.Loading,
                                enabled = registrationState.submit,
                                nameRequirement = nameRequirements.value,
                                emailRequirement = emailRequirements.value,
                                passwordRequirement = passwordRequirements.value,
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
                                onFinish = { signUpViewModel.onEvent(AuthenticationUIEvent.OnSignUp) }
                            )
                        },
                        bottomSection = {
                            AuthenticationScreenBottom(
                                isSignInScreen = false,
                                onNavigateToOppositeScreen = {
                                    signUpViewModel.onEvent(AuthenticationUIEvent.OnScreenChanged)
                                    onNavigate("sign_in")
                                }
                            )
                        }
                    )
                }
            )
        }
    }
}
