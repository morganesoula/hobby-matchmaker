package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.msoula.hobbymatchmaker.core.design.icons.LucideLock
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.HMMTextFieldColors
import com.msoula.hobbymatchmaker.core.design.theme.IconSize

@Composable
fun PrimaryTextField(
    modifier: Modifier = Modifier,
    text: String,
    singleLine: Boolean,
    onValueChanged: (String) -> Unit,
    readOnly: Boolean = false,
    label: String? = null,
    keyboardOptions: KeyboardOptions? = null,
    keyboardActions: KeyboardActions? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    showSupportingText: Boolean = false
) {
    val maxChar = 120

    OutlinedTextField(
        modifier = modifier,
        value = text,
        onValueChange = {
            if (it.length <= maxChar) {
                onValueChanged(it)
            }
        },
        readOnly = readOnly,
        label = { label?.let { Text(text = it) } },
        singleLine = singleLine,
        colors = HMMTextFieldColors(),
        keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
        keyboardActions = keyboardActions ?: rememberSubmitKeyBoardActions {},
        supportingText = {
            if (showSupportingText) {
                Text(
                    text = "${text.length}/$maxChar",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        trailingIcon = trailingIcon,
    )
}

@Composable
fun PrimaryTextFieldWithIcon(
    modifier: Modifier = Modifier,
    text: String,
    contentDescription: String,
    singleLine: Boolean,
    icon: ImageVector,
    onValueChanged: (String) -> Unit,
    label: String? = null,
    keyboardOptions: KeyboardOptions? = null,
    keyboardActions: KeyboardActions? = null
) {
    OutlinedTextField(
        modifier = modifier,
        value = text,
        onValueChange = { onValueChanged(it) },
        label = { label?.let { Text(text = it) } },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(IconSize.Sixteen)
            )
        },
        singleLine = singleLine,
        colors = HMMTextFieldColors(),
        keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
        keyboardActions = keyboardActions ?: rememberSubmitKeyBoardActions {}
    )
}

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    contentDescription: String,
    onEvent: (String) -> Unit,
    visualTransformation: VisualTransformation,
    keyboardActions: KeyboardActions?,
    trailingIcon: @Composable () -> Unit
) {
    OutlinedTextField(
        value = text,
        onValueChange = onEvent,
        label = { Text(text = label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.Sixteen, vertical = CustomSize.Eight),
        visualTransformation = visualTransformation,
        leadingIcon = {
            Icon(
                LucideLock,
                contentDescription = contentDescription,
                modifier = modifier.size(IconSize.Sixteen)
            )
        },
        trailingIcon = trailingIcon,
        singleLine = true,
        colors = HMMTextFieldColors(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = keyboardActions ?: KeyboardActions.Default
    )
}

@Composable
fun rememberSubmitKeyBoardActions(onSubmit: (() -> Unit)?): KeyboardActions {
    val keyboard = LocalSoftwareKeyboardController.current
    val focus = LocalFocusManager.current

    return KeyboardActions(
        onNext = { focus.moveFocus(FocusDirection.Down) },
        onDone = {
            onSubmit?.let { it() }
            keyboard?.hide()
            focus.clearFocus(force = true)
        },
        onSend = {
            onSubmit?.let { it() }
            keyboard?.hide()
            focus.clearFocus(force = true)
        },
        onGo = {
            onSubmit?.let { it() }
            keyboard?.hide()
            focus.clearFocus(force = true)
        }
    )
}
