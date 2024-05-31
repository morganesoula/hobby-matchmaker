package com.msoula.hobbymatchmaker.core.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.msoula.hobbymatchmaker.core.common.models.UserDataStore
import kotlinx.coroutines.flow.Flow
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
        val CONNEXION_MODE = stringPreferencesKey("connexion_mode_key")
        val EMAIL_KEY = stringPreferencesKey("email_key")
    }

    suspend fun saveAuthState(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_CONNECTED_KEY] = value
        }
    }

    suspend fun saveConnexionMode(connexionMode: String) {
        dataStore.edit { preferences ->
            preferences[CONNEXION_MODE] = connexionMode
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

    fun fetchConnexionMode() =
        dataStore.data.map { preferences -> preferences[CONNEXION_MODE] }

    suspend fun saveUser(user: UserDataStore) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[CONNEXION_MODE] = user.connexionMode
        }
    }

    val user: Flow<UserDataStore?> = dataStore.data
        .map { preferences ->
            val email = preferences[EMAIL_KEY]
            val connexionMode = preferences[CONNEXION_MODE]

            if (email != null && connexionMode != null) {
                UserDataStore(email, connexionMode)
            } else {
                null
            }
        }

    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.remove(EMAIL_KEY)
            preferences.remove(CONNEXION_MODE)
        }
    }
}
