package com.msoula.hobbymatchmaker.core.design.component

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

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


