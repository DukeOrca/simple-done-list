package com.duke.orca.android.kotlin.simpledonelist.donelist.viewmodels

import android.content.Context
import androidx.lifecycle.*
import com.duke.orca.android.kotlin.simpledonelist.admob.AdLoader
import com.duke.orca.android.kotlin.simpledonelist.application.Duration
import com.duke.orca.android.kotlin.simpledonelist.donelist.adapter.AdapterItem
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.donelist.repository.DoneListRepository
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Flow
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class DoneListViewModel @Inject constructor(
    @ApplicationContext applicationContext: Context,
    private val repository: DoneListRepository
) : ViewModel() {
    private val nativeAds = MutableLiveData<List<NativeAd>>()
    private val doneList = MutableLiveData<List<Done>>()

    val adapterItems = MediatorLiveData<List<AdapterItem>>().apply {
        addSource(doneList) {
            value = combine(doneList, nativeAds)
        }

        addSource(nativeAds) {
            value = combine(doneList, nativeAds)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            delay(Duration.LONG)
            loadNativeAd(applicationContext)
        }
    }

    fun getDoneList(julianDay: Int): LiveData<List<Done>> {
        return repository.getDoneList(julianDay).asLiveData(viewModelScope.coroutineContext)
    }

    fun setDoneList(list: List<Done>) {
        doneList.value = list
    }

    fun insert(done: Done) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(done)
        }
    }

    fun delete(done: Done) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(done)
        }
    }

    fun insertJulianDay(julianDay: JulianDay) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertJulianDay(julianDay)
        }
    }

    private fun loadNativeAd(context: Context) {
        AdLoader.loadNativeAd(context) {
            addNativeAd(it)
        }
    }

    private fun addNativeAd(nativeAd: NativeAd) {
        val value = nativeAds.value?.toMutableList() ?: mutableListOf()

        value.add(nativeAd)
        nativeAds.value = value
    }

    private fun combine(
        source1: LiveData<List<Done>>,
        source2: LiveData<List<NativeAd>>
    ): List<AdapterItem> {
        val doneList = source1.value ?: emptyList()
        val nativeAds = source2.value ?: emptyList()

        val adapterItems = arrayListOf<AdapterItem>()

        adapterItems.addAll(doneList.map { AdapterItem.DoneWrapper(it) })

        for (i in 0 until nativeAds.count()) {
            if (i == 0) {
                adapterItems.add(0, AdapterItem.NativeAdWrapper(nativeAds[0]))
            } else {
                val index = i * AD_INTERVAL

                if (index <= adapterItems.count()) {
                    adapterItems.add(index, AdapterItem.NativeAdWrapper(nativeAds[i]))
                } else {
                    break
                }
            }
        }

        return adapterItems
    }

    companion object {
        private const val AD_INTERVAL = 8
    }
}