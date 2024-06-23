package com.msoula.hobbymatchmaker.core.session.data.data_sources.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.msoula.hobbymatchmaker.core.session.domain.data_sources.SessionLocalDataSource
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.models.toSessionConnexionModeDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("settings")

class SessionLocalDataSourceImpl @Inject constructor(
    context: Context
) : SessionLocalDataSource {

    private val dataStore = context.dataStore

    companion object {
        val IS_CONNECTED_KEY = booleanPreferencesKey("is_connected_key")
        val CONNEXION_MODE = stringPreferencesKey("connexion_mode_key")
        val EMAIL_KEY = stringPreferencesKey("email_key")
    }

    private val user: Flow<SessionUserDomainModel?> = dataStore.data
        .map { preferences ->
            val email = preferences[EMAIL_KEY]
            val connexionMode = preferences[CONNEXION_MODE]

            if (email != null && connexionMode != null) {
                SessionUserDomainModel(email, connexionMode.toSessionConnexionModeDomainModel())
            } else {
                null
            }
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

    override fun getConnexionMode(): Flow<String?> =
        dataStore.data.map { preferences -> preferences[CONNEXION_MODE] }

    override suspend fun saveUser(user: SessionUserDomainModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[CONNEXION_MODE] = user.connexionMode.name
        }
    }

    override suspend fun clearSessionData() {
        dataStore.edit { preferences ->
            preferences.remove(EMAIL_KEY)
            preferences.remove(CONNEXION_MODE)
        }
    }
}
