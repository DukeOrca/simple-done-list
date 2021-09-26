package com.duke.orca.android.kotlin.simpledonelist.donelist.viewmodels

import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.duke.orca.android.kotlin.simpledonelist.application.BLANK
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done

class EditDoneDialogViewModel : ViewModel() {
    private val calendar = Calendar.getInstance()
    fun getJulianDay() = calendar.get(Calendar.JULIAN_DAY)
    fun getWrittenTime() = calendar.timeInMillis

    private val _content = MutableLiveData<String>()
    val content: LiveData<String> = _content

    var existingContent = BLANK

    private val _done = MutableLiveData<Done>()
    val done: LiveData<Done> = _done

    fun setContent(content: String) {
        _content.value = content
    }

    fun setDone(done: Done) {
        _done.value = done
    }
}