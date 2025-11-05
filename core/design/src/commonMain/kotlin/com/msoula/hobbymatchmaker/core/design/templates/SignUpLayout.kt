package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWeight1
import com.msoula.hobbymatchmaker.core.design.component.keyboardDismissOnTap
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
fun SignUpLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    form: @Composable () -> Unit,
    bottomSection: @Composable () -> Unit,
    snackbarHost: @Composable () -> Unit,
    loadingOverlay: @Composable () -> Unit = {}
) {
    Scaffold(
        snackbarHost = snackbarHost
    ) { paddingValues ->
        Box(
            modifier.fillMaxSize()
                .padding(
                    top = CustomSize.Sixteen,
                    start = CustomSize.Sixteen,
                    end = CustomSize.Sixteen
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .keyboardDismissOnTap()
            ) {
                topBar()
                SpacerHeight16()
                form()
                SpacerWeight1()
                bottomSection()
            }

            loadingOverlay()
        }
    }
}
