package com.msoula.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msoula.design.R.drawable as ImageRes
import com.msoula.design.R.string as StringRes

@Composable
fun HMMTextFieldAuthComponent(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeHolderText: String,
    visualTransformation: VisualTransformation? = null,
    keyboardOptions: KeyboardOptions? = null
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
        visualTransformation = visualTransformation ?: VisualTransformation.None,
        keyboardOptions = keyboardOptions ?: KeyboardOptions.Default
    )
}

@Composable
fun HMMButtonAuthComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = false,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = modifier.wrapContentSize(), color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text(
                text = text,
                modifier = modifier.padding(8.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
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

@Composable
fun HMMSocialMediaRow(
    modifier: Modifier,
    onFacebookButtonClicked: () -> Unit,
    onGoogleButtonClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { onFacebookButtonClicked() },
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)),
            modifier = modifier.size(80.dp)
        ) {
            Image(
                modifier = modifier,
                painter = painterResource(id = ImageRes.facebook_logo),
                contentDescription = stringResource(id = StringRes.facebook_alt)
            )
        }

        Button(
            onClick = { onGoogleButtonClicked() },
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)),
            modifier = modifier.size(80.dp)
        ) {
            Image(
                modifier = modifier,
                painter = painterResource(id = ImageRes.google_logo),
                contentDescription = stringResource(id = StringRes.google_alt)
            )
        }
    }
}