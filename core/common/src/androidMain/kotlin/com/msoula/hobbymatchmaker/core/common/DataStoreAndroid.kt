package com.msoula.hobbymatchmaker.core.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.msoula.hobbymatchmaker.core.common.data.createDataStore
import com.msoula.hobbymatchmaker.core.common.data.dataStoreFileName

fun createDataStore(context: Context): DataStore<Preferences> {
    return createDataStore(
        producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
    )
}
