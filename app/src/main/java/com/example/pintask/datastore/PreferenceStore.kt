package com.example.pintask.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.uiPreferenceDatastore : DataStore<Preferences> by preferencesDataStore(name = "ui_preference")

class PreferenceStore(context: Context) {
    private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode_on")

    //save preference
    suspend fun saveUIMode(context: Context,isDarkModeOn : Boolean){
        context.uiPreferenceDatastore.edit {
            it[IS_DARK_MODE] = isDarkModeOn
        }
    }

    //get preference
    val userUIPreference : Flow<Boolean> = context.uiPreferenceDatastore.data.map {
        it[IS_DARK_MODE] ?: false
    }
}