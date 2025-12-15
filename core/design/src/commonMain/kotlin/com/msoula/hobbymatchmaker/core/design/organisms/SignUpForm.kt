package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.PasswordTextField
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryButton
import com.msoula.hobbymatchmaker.core.design.atoms.PrimaryTextFieldWithIcon
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.atoms.rememberSubmitKeyBoardActions
import com.msoula.hobbymatchmaker.core.design.email
import com.msoula.hobbymatchmaker.core.design.firstname
import com.msoula.hobbymatchmaker.core.design.hide_password
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapPerson
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsAlternate_email
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsVisibility
import com.msoula.hobbymatchmaker.core.design.icons.MaterialSymbolsVisibility_off
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirement
import com.msoula.hobbymatchmaker.core.design.molecules.ValidationRequirementsList
import com.msoula.hobbymatchmaker.core.design.password
import com.msoula.hobbymatchmaker.core.design.show_password
import com.msoula.hobbymatchmaker.core.design.sign_up
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpForm(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    password: String,
    loading: Boolean,
    enabled: Boolean,
    nameRequirement: ImmutableList<ValidationRequirement> = persistentListOf(),
    emailRequirement: ImmutableList<ValidationRequirement> = persistentListOf(),
    passwordRequirement: ImmutableList<ValidationRequirement> = persistentListOf(),
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onFinish: () -> Unit
) {
    var hiddenPassword by remember { mutableStateOf(true) }

    val passwordFieldFocused = rememberSaveable { mutableStateOf(false) }
    val emailFieldFocused = rememberSaveable { mutableStateOf(false) }
    val nameFieldFocused = rememberSaveable { mutableStateOf(false) }

    // Name
    PrimaryTextFieldWithIcon(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.Sixteen)
            .onFocusChanged {
                nameFieldFocused.value = it.isFocused
            },
        text = name,
        label = stringResource(Res.string.firstname),
        contentDescription = stringResource(Res.string.firstname),
        singleLine = true,
        onValueChanged = onNameChanged,
        icon = BootstrapPerson,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next
        ),
        keyboardActions = null
    )

    if (nameRequirement.isNotEmpty() && nameFieldFocused.value) {
        if (nameRequirement.any { !it.isValid }) {
            ValidationRequirementsList(
                requirements = nameRequirement
            )
        }
    }

    SpacerHeight8()

    // Email
    PrimaryTextFieldWithIcon(
        text = email,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CustomSize.Sixteen)
            .onFocusChanged {
                emailFieldFocused.value = it.isFocused
            },
        label = stringResource(Res.string.email),
        contentDescription = stringResource(Res.string.email),
        singleLine = true,
        onValueChanged = onEmailChanged,
        icon = MaterialSymbolsAlternate_email,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            autoCorrectEnabled = false
        ),
        keyboardActions = null
    )

    if (emailRequirement.isNotEmpty() && emailFieldFocused.value) {
        if (emailRequirement.any { !it.isValid }) {
            ValidationRequirementsList(
                requirements = emailRequirement
            )
        }
    }

    SpacerHeight8()

    // Password
    PasswordTextField(
        modifier = modifier
            .onFocusChanged {
                passwordFieldFocused.value = it.isFocused
            },
        text = password,
        label = stringResource(Res.string.password),
        contentDescription = stringResource(Res.string.password),
        onEvent = onPasswordChanged,
        visualTransformation = if (hiddenPassword) PasswordVisualTransformation() else
            VisualTransformation.None,
        keyboardActions = rememberSubmitKeyBoardActions {
            onFinish()
        },
        trailingIcon = {
            IconButton(onClick = { hiddenPassword = !hiddenPassword }) {
                val description =
                    if (hiddenPassword) {
                        stringResource(Res.string.show_password)
                    } else {
                        stringResource(Res.string.hide_password)
                    }
                Icon(
                    imageVector = if (hiddenPassword) MaterialSymbolsVisibility else MaterialSymbolsVisibility_off,
                    contentDescription = description
                )
            }
        }
    )

    if (passwordRequirement.isNotEmpty() && passwordFieldFocused.value) {
        if (passwordRequirement.any { !it.isValid }) {
            ValidationRequirementsList(
                requirements = passwordRequirement
            )

            SpacerHeight8()
        }
    }

    SpacerHeight16()

    PrimaryButton(
        text = stringResource(Res.string.sign_up),
        onClick = onFinish,
        loading = loading,
        enabled = enabled
    )
}



