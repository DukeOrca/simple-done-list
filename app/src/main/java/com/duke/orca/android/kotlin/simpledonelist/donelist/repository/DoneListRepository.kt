package com.duke.orca.android.kotlin.simpledonelist.donelist.repository

import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import kotlinx.coroutines.flow.Flow

interface DoneListRepository {
    fun getDoneList(julianDay: Int): Flow<List<Done>>
    suspend fun insert(done: Done)
    suspend fun delete(done: Done)
    suspend fun insertJulianDay(julianDay: JulianDay)
}