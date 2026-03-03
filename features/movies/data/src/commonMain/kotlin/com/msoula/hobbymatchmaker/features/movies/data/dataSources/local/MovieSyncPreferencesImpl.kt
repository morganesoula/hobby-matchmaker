package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.msoula.hobbymatchmaker.core.common.safeLocalWrite
import kotlinx.coroutines.flow.first

class MovieSyncPreferencesImpl(
    private val dataStore: DataStore<Preferences>
) : MovieSyncPreferences {

    companion object {
        private const val NO_SYNC_TIMESTAMP = 0L
        val LAST_SYNC_TIMESTAMP_KEY =
            longPreferencesKey("last_sync_timestamp_key")

        val LAST_LOADED_PAGE_KEY =
            intPreferencesKey("last_loaded_page_key")
    }

    override suspend fun getLastSyncTimestamp(): Long =
        dataStore.data.first()[LAST_SYNC_TIMESTAMP_KEY] ?: NO_SYNC_TIMESTAMP

    override suspend fun setLastSyncTimestamp(timestamp: Long) =
        safeLocalWrite {
            dataStore.edit { it[LAST_SYNC_TIMESTAMP_KEY] = timestamp }
        }

    override suspend fun getLastLoadedPage(): Int =
        dataStore.data.first()[LAST_LOADED_PAGE_KEY] ?: 3

    override suspend fun setLastLoadedPage(page: Int) =
        safeLocalWrite {
            dataStore.edit { it[LAST_LOADED_PAGE_KEY] = page }
        }
}
