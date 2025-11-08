package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.PasswordTextField
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryButton
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryTextField
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.atoms.rememberSubmitKeyBoardActions
import com.msoula.hobbymatchmaker.core.design.email
import com.msoula.hobbymatchmaker.core.design.forgot_password
import com.msoula.hobbymatchmaker.core.design.hide_password
import com.msoula.hobbymatchmaker.core.design.log_in
import com.msoula.hobbymatchmaker.core.design.password
import com.msoula.hobbymatchmaker.core.design.show_password
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInForm(
    email: String,
    password: String,
    loading: Boolean,
    enabled: Boolean,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onFinish: () -> Unit
) {
    var hiddenPassword by remember { mutableStateOf(true) }

    // Email
    PrimaryTextField(
        text = email.trimEnd(),
        label = stringResource(Res.string.email),
        contentDescription = stringResource(Res.string.email),
        singleLine = true,
        onValueChanged = onEmailChanged,
        icon = Icons.Default.Email
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

    SpacerHeight8()

    ClickableText(
        text = AnnotatedString(stringResource(Res.string.forgot_password)),
        onClick = { onForgotPasswordClicked() },
        style = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            textDecoration = TextDecoration.Underline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = CustomSize.Sixteen)
            .wrapContentWidth(Alignment.End)
    )

    SpacerHeight16()

    PrimaryButton(
        text = stringResource(Res.string.log_in),
        onClick = onFinish,
        loading = loading,
        enabled = enabled
    )
}
