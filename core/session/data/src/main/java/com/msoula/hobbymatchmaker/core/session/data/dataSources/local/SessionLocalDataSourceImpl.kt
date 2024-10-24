package com.msoula.hobbymatchmaker.core.session.data.dataSources.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.msoula.hobbymatchmaker.core.session.domain.dataSources.SessionLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore("settings")

class SessionLocalDataSourceImpl(
    context: Context
) : SessionLocalDataSource {

    private val dataStore = context.dataStore

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
