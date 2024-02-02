package com.msoula.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HMMTextFieldAuthComponent(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeHolderText: String,
    visualTransformation: VisualTransformation? = null
) {
    TextField(
        modifier = modifier
            .padding(start = 24.dp, end = 24.dp)
            .fillMaxWidth(),
        value = value,
        onValueChange = { onValueChange(it) },
        placeholder = { Text(text = placeHolderText) },
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            cursorColor = MaterialTheme.colorScheme.secondary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        visualTransformation = visualTransformation ?: VisualTransformation.None
    )
}

@Composable
fun HMMButtonAuthComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = modifier.padding(8.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HMMFormHelperText(modifier: Modifier, isVisible: Boolean, titleHint: String, hint: String) {
    if (isVisible) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = titleHint, fontSize = 10.sp)
            Text(
                text = hint,
                fontSize = 10.sp
            )
        }
        Spacer(modifier = modifier.height(4.dp))
    }
}

@Composable
fun HMMErrorText(modifier: Modifier, errorText: String) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = errorText,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = modifier.height(8.dp))
}