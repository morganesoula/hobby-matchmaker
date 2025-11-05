package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.MainTitle
import com.msoula.hobbymatchmaker.core.design.atoms.MediumTitle
import com.msoula.hobbymatchmaker.core.design.welcome_back_subtitle
import com.msoula.hobbymatchmaker.core.design.welcome_back_title
import com.msoula.hobbymatchmaker.core.design.welcome_subtitle
import com.msoula.hobbymatchmaker.core.design.welcome_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthenticationScreenTop(
    modifier: Modifier = Modifier,
    isSignInScreen: Boolean
) {
    MainTitle(
        text = if (isSignInScreen) stringResource(Res.string.welcome_back_title)
        else stringResource(Res.string.welcome_title)
    )

    MediumTitle(
        text = if (isSignInScreen) stringResource(Res.string.welcome_back_subtitle)
        else stringResource(Res.string.welcome_subtitle)
    )
}
