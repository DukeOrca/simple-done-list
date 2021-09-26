package com.duke.orca.android.kotlin.simpledonelist.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.history.models.History
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import com.duke.orca.android.kotlin.simpledonelist.history.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val repository: HistoryRepository) : ViewModel() {
    val histories = repository.histories.asLiveData()

    fun getDoneList(julianDay: JulianDay) = repository.get(julianDay)

    fun delete(history: History) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(history)
        }
    }

    fun deleteList(list: List<History>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteList(list.map { it.julianDay })
        }
    }

    fun deleteDone(done: Done) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDone(done)
        }
    }
}