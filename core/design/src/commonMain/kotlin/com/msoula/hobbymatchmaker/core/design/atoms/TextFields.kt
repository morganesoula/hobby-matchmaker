package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.msoula.hobbymatchmaker.core.design.component.rememberSubmitKeyBoardActions
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.theme.HMMTextFieldColors
import com.msoula.hobbymatchmaker.core.design.theme.IconSize

@Composable
fun PrimaryTextField(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    contentDescription: String,
    singleLine: Boolean,
    onEvent: (String) -> Unit,
    icon: ImageVector? = null,
    keyboardOptions: KeyboardOptions? = null,
    keyboardActions: KeyboardActions? = null
) {
    OutlinedTextField(
        value = text,
        onValueChange = { onEvent(it) },
        label = { Text(text = label) },
        leadingIcon = {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(IconSize.Sixteen)
                )
            }
        },
        singleLine = singleLine,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = CustomSize.Sixteen, vertical = CustomSize.Eight),
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
                Icons.Default.Lock,
                contentDescription = contentDescription,
                modifier = modifier.size(IconSize.Sixteen)
            )
        },
        trailingIcon = trailingIcon,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = keyboardActions ?: KeyboardActions.Default
    )
}
