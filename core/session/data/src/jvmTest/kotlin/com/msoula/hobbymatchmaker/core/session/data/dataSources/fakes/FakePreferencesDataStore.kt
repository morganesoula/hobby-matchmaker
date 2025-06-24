package com.msoula.hobbymatchmaker.core.session.data.dataSources.fakes

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePreferencesDataStore(
    initialPreferences: Preferences = emptyPreferences()
) : DataStore<Preferences> {

    private var prefs = initialPreferences
    private val flow = MutableStateFlow(prefs)

    override val data: Flow<Preferences> = flow

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        prefs = transform(prefs)
        flow.value = prefs
        return prefs
    }
}
