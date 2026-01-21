package com.msoula.hobbymatchmaker.core.session.data.dataSources.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeLocalWrite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SessionLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : SessionLocalDataSource {

    companion object {
        val IS_CONNECTED_KEY =
            booleanPreferencesKey("is_connected_key")

        val DONT_ASK_CHECKBOX_VALUE =
            booleanPreferencesKey("dont_ask_checkbox_value_key")

        val USER_PROFILE_UUID =
            stringPreferencesKey("user_profile_uuid")
    }

    override suspend fun setIsConnected(isConnected: Boolean): AppResult<Unit, AppError> =
        safeLocalWrite {
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

    override suspend fun setDontAskGuestValue(dontAsk: Boolean): AppResult<Unit, AppError> =
        safeLocalWrite {
            dataStore.edit { it[DONT_ASK_CHECKBOX_VALUE] = dontAsk }
        }

    override fun observeDontAskCheckboxValue(): Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences -> preferences[DONT_ASK_CHECKBOX_VALUE] ?: false }

    override suspend fun setCurrentUid(currentUid: String): AppResult<Unit, AppError> =
        safeLocalWrite {
            dataStore.edit { it[USER_PROFILE_UUID] = currentUid }
        }

    override fun observeCurrentUid(): Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences -> preferences[USER_PROFILE_UUID] ?: "" }

    override suspend fun getCurrentUserUid(): String =
        dataStore.data.first()[USER_PROFILE_UUID] ?: ""

    override suspend fun clearCurrentUserUid(): AppResult<Unit, AppError> =
        safeLocalWrite {
            dataStore.edit { preferences ->
                if (preferences.contains(USER_PROFILE_UUID)) {
                    preferences.remove(USER_PROFILE_UUID)
                }
            }
        }
}
