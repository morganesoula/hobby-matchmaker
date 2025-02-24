package com.msoula.hobbymatchmaker.core.session.data.dataSources.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.msoula.hobbymatchmaker.core.session.domain.dataSources.SessionLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SessionLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : SessionLocalDataSource {

    companion object {
        val IS_CONNECTED_KEY = booleanPreferencesKey("is_connected_key")
    }

    override suspend fun setIsConnected(isConnected: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_CONNECTED_KEY] = isConnected
        }
    }

    override fun observeIsConnected(): Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[IS_CONNECTED_KEY] ?: false
        }
}
