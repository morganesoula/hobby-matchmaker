package com.msoula.hobbymatchmaker.core.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AuthenticationDataStore @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val IS_CONNECTED_KEY = booleanPreferencesKey("is_connected_key")
    }

    suspend fun saveAuthState(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_CONNECTED_KEY] = value
        }
    }

    fun observeAuthState() = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[IS_CONNECTED_KEY] ?: false
        }
}
