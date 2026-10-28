package com.msoula.hobbymatchmaker.features.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryButton
import com.msoula.hobbymatchmaker.core.design.atoms.TextButtonWithStartIcon
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapArrowRight
import com.msoula.hobbymatchmaker.core.design.icons.LucideLogOut
import com.msoula.hobbymatchmaker.core.design.models.PseudoStatusUiModel
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirement
import com.msoula.hobbymatchmaker.core.design.organisms.PickPseudoForm
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_close
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_continue
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_exit_caption
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_logout
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_subtitle
import com.msoula.hobbymatchmaker.core.design.pick_pseudo_title
import com.msoula.hobbymatchmaker.core.design.pseudo_rule_chars
import com.msoula.hobbymatchmaker.core.design.pseudo_rule_dot
import com.msoula.hobbymatchmaker.core.design.pseudo_rule_length
import com.msoula.hobbymatchmaker.core.design.templates.PickPseudoLayout
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import com.msoula.hobbymatchmaker.features.profile.domain.models.PseudoValidationError
import com.msoula.hobbymatchmaker.features.profile.domain.validators.PseudoValidator
import com.msoula.hobbymatchmaker.features.profile.presentation.models.PickPseudoEventModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.PickPseudoStatusModel
import com.msoula.hobbymatchmaker.features.profile.presentation.models.PickPseudoUiModel
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun PickPseudoScreen(
    formState: PickPseudoUiModel,
    snackBarHostState: SnackbarHostState,
    onEvent: (PickPseudoEventModel) -> Unit
) {
    // Android quits through its system back button; iOS has none, so a close button logs out instead
    val isIos = isIosPlatform()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        PickPseudoLayout(
            padding = padding,
            title = stringResource(Res.string.pick_pseudo_title),
            subtitle = stringResource(Res.string.pick_pseudo_subtitle),
            caption = if (isIos) null else stringResource(Res.string.pick_pseudo_exit_caption),
            closeContentDescription = stringResource(Res.string.pick_pseudo_close),
            onClose = if (isIos) ({ onEvent(PickPseudoEventModel.OnLogOutClicked) }) else null,
            form = {
                PickPseudoForm(
                    firstName = formState.firstName,
                    isFirstNameEditable = formState.isFirstNameEditable,
                    pseudo = formState.pseudo,
                    pseudoMaxLength = PseudoValidator.MAX_LENGTH,
                    pseudoStatus = formState.status.toPseudoStatusUiModel(),
                    onFirstNameChanged = { onEvent(PickPseudoEventModel.OnFirstNameChanged(it)) },
                    onPseudoChanged = { onEvent(PickPseudoEventModel.OnPseudoChanged(it)) }
                )
            },
            actions = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(CustomSize.Eight)
                ) {
                    PrimaryButton(
                        text = stringResource(Res.string.pick_pseudo_continue),
                        onClick = { onEvent(PickPseudoEventModel.OnContinueClicked) },
                        loading = formState.isSubmitting,
                        enabled = formState.canSubmit,
                        trailingIcon = BootstrapArrowRight
                    )
                    TextButtonWithStartIcon(
                        icon = LucideLogOut,
                        text = stringResource(Res.string.pick_pseudo_logout),
                        onClick = { onEvent(PickPseudoEventModel.OnLogOutClicked) }
                    )
                }
            }
        )
    }
}

@Composable
private fun PickPseudoStatusModel.toPseudoStatusUiModel(): PseudoStatusUiModel = when (this) {
    PickPseudoStatusModel.Idle -> PseudoStatusUiModel.Idle
    PickPseudoStatusModel.Checking -> PseudoStatusUiModel.Checking
    PickPseudoStatusModel.Available -> PseudoStatusUiModel.Available
    PickPseudoStatusModel.Taken -> PseudoStatusUiModel.Taken
    is PickPseudoStatusModel.Invalid -> PseudoStatusUiModel.Invalid(
        persistentListOf(
            ValidationRequirement(
                text = stringResource(Res.string.pseudo_rule_length),
                isValid = PseudoValidationError.InvalidLength !in errors
            ),
            ValidationRequirement(
                text = stringResource(Res.string.pseudo_rule_chars),
                isValid = PseudoValidationError.InvalidChars !in errors
            ),
            ValidationRequirement(
                text = stringResource(Res.string.pseudo_rule_dot),
                isValid = PseudoValidationError.InvalidDot !in errors
            )
        )
    )
}
