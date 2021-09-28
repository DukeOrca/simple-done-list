package com.duke.orca.android.kotlin.simpledonelist.main.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.duke.orca.android.kotlin.simpledonelist.datastore.dataStore

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val settings = application.dataStore.data.asLiveData(viewModelScope.coroutineContext)

    private val _bannerAdViewVisibility = MutableLiveData<Int>()
    val bannerAdViewVisibility: LiveData<Int> = _bannerAdViewVisibility

    fun setBannerAdViewVisibility(visibility: Int) {
        _bannerAdViewVisibility.value = visibility
    }
}