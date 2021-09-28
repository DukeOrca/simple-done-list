package com.duke.orca.android.kotlin.simpledonelist.datastore

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.duke.orca.android.kotlin.simpledonelist.application.Application
import com.duke.orca.android.kotlin.simpledonelist.settings.PreferencesKeys
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "${Application.PACKAGE_NAME}.settings")

object DataStore {
    fun getBoolean(context: Context, key: Preferences.Key<Boolean>, defValue: Boolean): Boolean = runBlocking {
        context.dataStore.data.map {
            it[key] ?: defValue
        }.first()
    }

    fun getFloat(context: Context, key: Preferences.Key<Float>, defValue: Float): Float = runBlocking {
        context.dataStore.data.map {
            it[key] ?: defValue
        }.first()
    }

    fun getIsFirstTime(context: Context): Boolean {
        return getBoolean(context, PreferencesKeys.isFirstTime, true)
    }

    fun putFirstTime(context: Context, isFirstTime: Boolean) = runBlocking {
        context.dataStore.edit {
            it[PreferencesKeys.isFirstTime] = isFirstTime
        }
    }

    object Display {
        fun getFontSize(context: Context) = getFloat(context, PreferencesKeys.Display.fontSize, 16F)
        fun isDarkMode(context: Context): Boolean {
            val defValue = when(context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                AppCompatDelegate.MODE_NIGHT_YES, Configuration.UI_MODE_NIGHT_YES -> true
                AppCompatDelegate.MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_NO -> false
                AppCompatDelegate.MODE_NIGHT_UNSPECIFIED, Configuration.UI_MODE_NIGHT_UNDEFINED -> true
                else -> true
            }

            return getBoolean(context, PreferencesKeys.Display.isDarkMode, defValue)
        }

        suspend fun putFontSize(context: Context, fontSize: Float) {
            context.dataStore.edit {
                it[PreferencesKeys.Display.fontSize] = fontSize
            }
        }

        suspend fun putDarkMode(context: Context, darkMode: Boolean) {
            context.dataStore.edit {
                it[PreferencesKeys.Display.isDarkMode] = darkMode
            }
        }
    }
}