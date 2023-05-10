package com.bundela.todoapp.utils.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorePreference(var context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ui_mode_preference")

    suspend fun saveToDataStore(isNightMode: Boolean, key: Preferences.Key<Boolean>) {
        context.dataStore.edit { preferences ->
            preferences[key] = isNightMode
        }
    }

    val uiMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val uiMode = preferences[UI_MODE_KEY] ?: false
            uiMode
        }

    val uiListMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val uiMode = preferences[UI_LIST_MODE_KEY] ?: false
            uiMode
        }

    companion object {
         val UI_MODE_KEY = booleanPreferencesKey("ui_mode")
         val UI_LIST_MODE_KEY = booleanPreferencesKey("ui_list_mode")
    }
}

