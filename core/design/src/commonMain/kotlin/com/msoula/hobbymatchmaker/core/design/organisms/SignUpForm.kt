package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.PasswordTextField
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryButton
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryTextField
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.component.rememberSubmitKeyBoardActions
import com.msoula.hobbymatchmaker.core.design.email
import com.msoula.hobbymatchmaker.core.design.firstname
import com.msoula.hobbymatchmaker.core.design.hide_password
import com.msoula.hobbymatchmaker.core.design.password
import com.msoula.hobbymatchmaker.core.design.show_password
import com.msoula.hobbymatchmaker.core.design.sign_up
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpForm(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    password: String,
    loading: Boolean,
    enabled: Boolean,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onFinish: () -> Unit
) {
    val emailTipVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordTipVisibility = rememberSaveable { mutableStateOf(false) }
    var hiddenPassword by remember { mutableStateOf(true) }

    // Name
    PrimaryTextField(
        text = name,
        label = stringResource(Res.string.firstname),
        contentDescription = stringResource(Res.string.firstname),
        singleLine = true,
        onEvent = onNameChanged,
        icon = Icons.Default.People,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next
        ),
        keyboardActions = null
    )

    SpacerHeight8()

    // Email
    PrimaryTextField(
        text = email,
        modifier = modifier.onFocusChanged {
            emailTipVisibility.value = it.isFocused
        },
        label = stringResource(Res.string.email),
        contentDescription = stringResource(Res.string.email),
        singleLine = true,
        onEvent = onEmailChanged,
        icon = Icons.Default.Email,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            autoCorrectEnabled = false
        ),
        keyboardActions = null
    )

    SpacerHeight8()

    // Password
    PasswordTextField(
        text = password,
        label = stringResource(Res.string.password),
        contentDescription = stringResource(Res.string.password),
        onEvent = onPasswordChanged,
        visualTransformation = if (hiddenPassword) PasswordVisualTransformation() else
            VisualTransformation.None,
        keyboardActions = rememberSubmitKeyBoardActions {
            onFinish()
        },
        trailingIcon = {
            IconButton(onClick = { hiddenPassword = !hiddenPassword }) {
                val description =
                    if (hiddenPassword) {
                        stringResource(Res.string.show_password)
                    } else {
                        stringResource(Res.string.hide_password)
                    }
                Icon(
                    imageVector = if (hiddenPassword) Icons.Default.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = description
                )
            }
        }
    )

    SpacerHeight16()

    PrimaryButton(
        text = stringResource(Res.string.sign_up),
        onClick = onFinish,
        loading = loading,
        enabled = enabled
    )
}



