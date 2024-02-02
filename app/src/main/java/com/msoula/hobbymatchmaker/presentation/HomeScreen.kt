package com.msoula.hobbymatchmaker.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

import com.msoula.auth.R.string as StringRes

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    logOut: () -> Unit
) {
    Scaffold { paddingValues ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = modifier.fillMaxSize()) {
                Text(
                    modifier = modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    text = "Hello there, you are logged in"
                )

                Spacer(modifier = modifier.height(100.dp))

                Button(onClick = { logOut() }, modifier = modifier.wrapContentSize()) {
                    Text(text = stringResource(id = StringRes.sign_out))
                }
            }
        }
    }
}