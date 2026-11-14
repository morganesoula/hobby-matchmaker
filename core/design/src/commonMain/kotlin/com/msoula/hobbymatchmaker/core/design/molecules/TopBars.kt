package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.edit_profile_save_button_title
import com.msoula.hobbymatchmaker.core.design.edit_profile_title
import com.msoula.hobbymatchmaker.core.design.back
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth4
import com.msoula.hobbymatchmaker.core.design.cancel
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapPerson
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsArrow_back
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsGroup_add
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTopBar(
    modifier: Modifier = Modifier,
    pendingInvitesCount: Int = 0,
    redirectToProfile: () -> Unit,
    redirectToSocial: () -> Unit
) {
    TopAppBar(
        title = {},
        windowInsets = WindowInsets(0),
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
                    BadgedBox(
                        badge = {
                            if (pendingInvitesCount > 0) {
                                Badge { Text(if (pendingInvitesCount > 9) "9+" else pendingInvitesCount.toString()) }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = MaterialSymbolsGroup_add,
                            contentDescription = "Invites",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
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
    title: String? = null,
    showBackArrow: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        modifier = modifier.statusBarsPadding(),
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        windowInsets = WindowInsets(0),
        navigationIcon = {
            if (showBackArrow) {
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
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(Res.string.edit_profile_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        modifier = modifier.statusBarsPadding(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        windowInsets = WindowInsets(0),
        actions = {
            TextButton(onClick = { onSave() }, enabled = enableSave) {
                Text(
                    text = stringResource(Res.string.edit_profile_save_button_title),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = if (enableSave) 1f else .4f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        navigationIcon = {
            if (isIOS) {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        imageVector = MaterialIconsArrow_back,
                        contentDescription = stringResource(Res.string.back)
                    )
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
