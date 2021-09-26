package com.duke.orca.android.kotlin.simpledonelist.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import com.duke.orca.android.kotlin.simpledonelist.application.Application

object PreferencesKeys {
    private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.settings"

    object Display {
        private const val OBJECT_NAME = "Display"
        val fontSize = floatPreferencesKey("$PACKAGE_NAME.$OBJECT_NAME.fontSize")
        val isDarkMode = booleanPreferencesKey("$PACKAGE_NAME.$OBJECT_NAME.isDarkMode")
    }

    object LockScreen {
        private const val OBJECT_NAME = "LockScreen"
        val displayAfterUnlocking = booleanPreferencesKey("$PACKAGE_NAME.$OBJECT_NAME.displayAfterUnlocking")
        val showOnLockScreen = booleanPreferencesKey("$PACKAGE_NAME.$OBJECT_NAME.showOnLockScreen")
        val unlockWithBackKey = booleanPreferencesKey("$PACKAGE_NAME.$OBJECT_NAME.unlockWithBackKey")
    }
}