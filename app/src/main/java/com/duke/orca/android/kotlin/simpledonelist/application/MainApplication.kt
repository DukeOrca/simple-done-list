package com.duke.orca.android.kotlin.simpledonelist.application

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.duke.orca.android.kotlin.simpledonelist.BuildConfig
import com.duke.orca.android.kotlin.simpledonelist.datastore.DataStore
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        AppCompatDelegate.setDefaultNightMode(getDarkMode())
        MobileAds.initialize(this)
    }

    private fun getDarkMode(): Int {
        return if (DataStore.Display.isDarkMode(this))
            AppCompatDelegate.MODE_NIGHT_YES
        else
            AppCompatDelegate.MODE_NIGHT_NO
    }

    companion object {
        private lateinit var instance: MainApplication

        fun getApplicationContext(): Context = instance.applicationContext
    }
}