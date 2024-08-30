package com.msoula.hobbymatchmaker.core.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HMMTextFieldAuthComponent(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeHolderText: String,
    visualTransformation: VisualTransformation? = null,
    keyboardOptions: KeyboardOptions? = null,
) {
    OutlinedTextField(
        modifier =
        modifier
            .padding(start = 24.dp, end = 24.dp)
            .fillMaxWidth(),
        value = value,
        onValueChange = { onValueChange(it) },
        placeholder = { Text(text = placeHolderText) },
        shape = RoundedCornerShape(16.dp),
        colors =
        TextFieldDefaults.colors(
            cursorColor = MaterialTheme.colorScheme.secondary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
    )
}

@Composable
fun HMMTextFieldPasswordComponent(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    showPasswordContentDescription: String,
    hidePasswordContentDescription: String,
) {
    var hiddenPassword by remember { mutableStateOf(true) }

    TextField(
        modifier =
        modifier
            .padding(start = 24.dp, end = 24.dp)
            .fillMaxWidth(),
        value = value.trimEnd(),
        onValueChange = { onValueChange(it) },
        placeholder = { Text(text = placeholder) },
        shape = RoundedCornerShape(16.dp),
        colors =
        TextFieldDefaults.colors(
            cursorColor = MaterialTheme.colorScheme.secondary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        visualTransformation = if (hiddenPassword) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            IconButton(onClick = { hiddenPassword = !hiddenPassword }) {
                val description =
                    if (hiddenPassword) {
                        showPasswordContentDescription
                    } else {
                        hidePasswordContentDescription
                    }
                Icon(
                    imageVector = if (hiddenPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = description
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@Composable
fun HMMButtonAuthComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = false,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier =
        modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = modifier.wrapContentSize(),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            Text(
                text = text,
                modifier = modifier.padding(8.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun HMMFormHelperText(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    titleHint: String,
    hint: String,
) {
    if (isVisible) {
        Row(
            modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(text = titleHint, fontSize = 10.sp)
            Text(
                text = hint,
                fontSize = 10.sp,
            )
        }
        Spacer(modifier = modifier.height(4.dp))
    }
}

@Composable
fun HMMErrorText(
    modifier: Modifier = Modifier,
    errorText: String,
) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = errorText,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = modifier.height(8.dp))
}
