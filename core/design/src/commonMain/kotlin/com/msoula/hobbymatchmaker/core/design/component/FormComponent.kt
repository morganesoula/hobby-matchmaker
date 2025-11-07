package com.msoula.hobbymatchmaker.core.design.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msoula.hobbymatchmaker.core.design.theme.HMMTextFieldColors

@Composable
fun HMMTextFieldAuthComponent(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    icon: ImageVector? = null,
    contentDescription: String = "",
    keyboardOptions: KeyboardOptions? = null,
    keyboardActions: KeyboardActions? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(text = label) },
        leadingIcon = {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        singleLine = true,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = HMMTextFieldColors(),
        keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
        keyboardActions = keyboardActions ?: rememberSubmitKeyBoardActions {}
    )
}

@Composable
fun HMMFormHelperText(
    modifier: Modifier = Modifier,
    isVisible: MutableState<Boolean>,
    titleHint: String,
    hint: String,
) {
    if (isVisible.value) {
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
fun Modifier.keyboardDismissOnTap(): Modifier {
    val controller = LocalSoftwareKeyboardController.current
    return clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { controller?.hide() }
}
