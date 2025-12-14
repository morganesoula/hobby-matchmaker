package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth4
import com.msoula.hobbymatchmaker.core.design.cancel
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapPerson
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsArrow_back
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsGroup_add
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTopBar(
    modifier: Modifier = Modifier,
    redirectToProfile: () -> Unit,
    redirectToSocial: () -> Unit
) {
    TopAppBar(
        title = {},
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent
        ),
        actions = {
            Row(
                modifier = Modifier.padding(end = CustomSize.Four)
            ) {
                IconButton(
                    onClick = { redirectToSocial() }
                ) {
                    Icon(
                        imageVector = MaterialSymbolsGroup_add,
                        contentDescription = "Invites",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = { redirectToProfile() }
                ) {
                    Icon(
                        imageVector = BootstrapPerson,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackNavigationTopBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {},
        modifier = modifier.statusBarsPadding(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        windowInsets = WindowInsets(0),
        navigationIcon = {
            IconButton(
                onClick = { onBack() },
                content = {
                    Icon(
                        imageVector = MaterialIconsArrow_back,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileTopBar(
    modifier: Modifier = Modifier,
    isIOS: Boolean,
    enableSave: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    TopAppBar(
        title = {},
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            Button(
                onClick = { onSave() },
                enabled = enableSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .4f)
                )
            ) {
                Text(
                    text = "Save",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        CustomSize.Four
                    )
                )
            }
        },
        navigationIcon = {
            if (isIOS) {
                IconButton(onClick = { onBack() }) {
                    Row {
                        Icon(
                            imageVector = MaterialIconsArrow_back,
                            contentDescription = "Back"
                        )
                        SpacerWidth4()
                        Text(
                            text = stringResource(Res.string.cancel),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun NavigationTopBarPreview() {
    NavigationTopBar(
        redirectToProfile = {},
        redirectToSocial = {}
    )
}
