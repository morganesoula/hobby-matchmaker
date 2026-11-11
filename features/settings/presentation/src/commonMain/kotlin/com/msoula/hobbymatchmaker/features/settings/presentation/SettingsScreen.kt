package com.msoula.hobbymatchmaker.features.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.DestructiveConfirmationDialog
import com.msoula.hobbymatchmaker.core.design.delete_account
import com.msoula.hobbymatchmaker.core.design.delete_account_dialog_cancel
import com.msoula.hobbymatchmaker.core.design.delete_account_dialog_confirm
import com.msoula.hobbymatchmaker.core.design.delete_account_dialog_message
import com.msoula.hobbymatchmaker.core.design.delete_account_dialog_title
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsError_outline
import com.msoula.hobbymatchmaker.core.design.icons.LucideLock
import com.msoula.hobbymatchmaker.core.design.icons.LucideLogOut
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsDelete
import com.msoula.hobbymatchmaker.core.design.log_out
import com.msoula.hobbymatchmaker.core.design.molecules.BackNavigationTopBar
import com.msoula.hobbymatchmaker.core.design.molecules.SettingsActionCard
import com.msoula.hobbymatchmaker.core.design.molecules.SettingsSectionLabel
import com.msoula.hobbymatchmaker.core.design.report_concern
import com.msoula.hobbymatchmaker.core.design.report_concern_email_subject
import com.msoula.hobbymatchmaker.core.design.settings_account_section
import com.msoula.hobbymatchmaker.core.design.settings_privacy_description
import com.msoula.hobbymatchmaker.core.design.settings_privacy_title
import com.msoula.hobbymatchmaker.core.design.settings_report_description
import com.msoula.hobbymatchmaker.core.design.settings_support_section
import com.msoula.hobbymatchmaker.core.design.settings_title
import com.msoula.hobbymatchmaker.core.design.settings_version
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.core.design.util.PRIVACY_POLICY_URL
import com.msoula.hobbymatchmaker.core.design.util.supportMailtoUri
import com.msoula.hobbymatchmaker.features.settings.presentation.models.AppVersionUiModel
import com.msoula.hobbymatchmaker.features.settings.presentation.models.SettingsEventModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsContent(
    appVersion: AppVersionUiModel,
    isDeleteAccountDialogVisible: Boolean,
    isDeletingAccount: Boolean,
    snackBarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onEvent: (SettingsEventModel) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val reportSubject = stringResource(Res.string.report_concern_email_subject)

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = data.visuals.message)
                }
            }
        },
        topBar = {
            BackNavigationTopBar(
                title = stringResource(Res.string.settings_title),
                showBackArrow = isIosPlatform(),
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(CustomSize.Sixteen)
        ) {
            SettingsSectionLabel(stringResource(Res.string.settings_support_section))

            SettingsActionCard(
                icon = MaterialIconsError_outline,
                title = stringResource(Res.string.report_concern),
                description = stringResource(Res.string.settings_report_description),
                showArrow = true,
                onClick = {
                    // No email app: nothing opens, the support address is on the privacy page
                    runCatching { uriHandler.openUri(supportMailtoUri(reportSubject)) }
                        .onFailure { Logger.e("No app to open the report email: ${it.message}") }
                }
            )

            SettingsActionCard(
                icon = LucideLock,
                title = stringResource(Res.string.settings_privacy_title),
                description = stringResource(Res.string.settings_privacy_description),
                showArrow = true,
                onClick = {
                    runCatching { uriHandler.openUri(PRIVACY_POLICY_URL) }
                        .onFailure { Logger.e("No app to open the privacy policy: ${it.message}") }
                }
            )

            SettingsSectionLabel(stringResource(Res.string.settings_account_section))

            SettingsActionCard(
                icon = LucideLogOut,
                title = stringResource(Res.string.log_out),
                onClick = { onEvent(SettingsEventModel.OnLogOutClicked) }
            )

            SettingsActionCard(
                icon = MaterialSymbolsDelete,
                title = stringResource(Res.string.delete_account),
                description = stringResource(Res.string.delete_account_dialog_message),
                isDestructive = true,
                onClick = { onEvent(SettingsEventModel.OnDeleteAccountClicked) }
            )

            Text(
                text = stringResource(Res.string.settings_version, appVersion.versionName, appVersion.buildNumber),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = CustomSize.ThirtyTwo)
            )
        }

        if (isDeleteAccountDialogVisible) {
            DestructiveConfirmationDialog(
                title = stringResource(Res.string.delete_account_dialog_title),
                message = stringResource(Res.string.delete_account_dialog_message),
                confirmButtonText = stringResource(Res.string.delete_account_dialog_confirm),
                cancelButtonText = stringResource(Res.string.delete_account_dialog_cancel),
                isLoading = isDeletingAccount,
                onDismiss = { onEvent(SettingsEventModel.OnDeleteAccountDismissed) },
                onConfirm = { onEvent(SettingsEventModel.OnDeleteAccountConfirmed) }
            )
        }
    }
}
