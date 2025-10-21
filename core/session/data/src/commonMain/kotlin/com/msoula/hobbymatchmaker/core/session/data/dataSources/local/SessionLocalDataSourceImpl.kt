package com.msoula.hobbymatchmaker.core.session.data.dataSources.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.helpers.safeLocalWrite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SessionLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : SessionLocalDataSource {

    companion object {
        val IS_CONNECTED_KEY =
            booleanPreferencesKey("is_connected_key")

        val SHOULD_SHOW_GUEST_DIALOG_KEY =
            booleanPreferencesKey("should_show_guest_dialog_key")
    }

    override suspend fun setIsConnected(isConnected: Boolean): AppResult<Unit, AppError> = safeLocalWrite {
        dataStore.edit { it[IS_CONNECTED_KEY] = isConnected }
    }

    override fun observeIsConnected(): Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            preferences[IS_CONNECTED_KEY] ?: false
        }

    override suspend fun setShouldShowGuestDialog(shouldShow: Boolean): AppResult<Unit, AppError> =
        safeLocalWrite {
            dataStore.edit { it[SHOULD_SHOW_GUEST_DIALOG_KEY] = shouldShow }
        }

    override fun observeShouldShowGuestDialog(): Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences -> preferences[SHOULD_SHOW_GUEST_DIALOG_KEY] ?: true }
}
