package com.msoula.auth.presentation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.msoula.auth.data.AuthFormState
import com.msoula.auth.data.UserState

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    user: UserState?,
    formState: AuthFormState
) {
    user?.let {
        if (it.connected) {
            Snackbar {
                Text(text = "Hey there, you are connected")
            }
            Log.i("HMM", "User is connected")
        } else {
            SignInForm(
                modifier = modifier,
                user = it,
                formState = formState,
                onEmailChanged = { email ->
                    authViewModel.onFormEvent(
                        AuthFormEvent.OnEmailChanged(
                            email
                        )
                    )
                },
                onPasswordChanged = { password ->
                    authViewModel.onFormEvent(
                        AuthFormEvent.OnPasswordChanged(
                            password
                        )
                    )
                },
                submit = {
                    authViewModel.onFormEvent(AuthFormEvent.OnSubmitAuthForm)
                }
            )
        }
    } ?: run {
        SignInForm(
            modifier = modifier,
            user = user,
            formState = formState,
            onEmailChanged = { email -> authViewModel.onFormEvent(AuthFormEvent.OnEmailChanged(email)) },
            onPasswordChanged = { password ->
                authViewModel.onFormEvent(
                    AuthFormEvent.OnPasswordChanged(
                        password
                    )
                )
            },
            submit = {
                authViewModel.onFormEvent(AuthFormEvent.OnSubmitAuthForm)
            }
        )
    }
}

@Composable
fun SignInForm(
    modifier: Modifier,
    user: UserState?,
    formState: AuthFormState,
    onEmailChanged: (email: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,
    submit: () -> Unit
) {
    Text(modifier = modifier.fillMaxWidth(), text = "Bienvenue dans HobbyMatchMaker")

    Column {
        TextField(
            value = formState.email,
            onValueChange = {
                onEmailChanged(it)
                Log.d(
                    "HMM",
                    "User email is: ${user?.email}"
                )
            },
            label = { Text(text = "Email") },
            modifier = modifier.fillMaxWidth(),
        )

        TextField(
            value = formState.password,
            onValueChange = { onPasswordChanged(it) },
            modifier = modifier.fillMaxWidth(),
            label = { Text(text = "Mot de passe") },
            visualTransformation = if (user?.password?.isNotEmpty() == true) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
        )

        Button(onClick = {
            submit()
        }) {
            Text(text = "Connexion")
        }
    }
}