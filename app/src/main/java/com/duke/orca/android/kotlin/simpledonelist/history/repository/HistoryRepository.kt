package com.duke.orca.android.kotlin.simpledonelist.history.repository

import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.history.models.History
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun delete(history: History)
    suspend fun deleteList(list: List<JulianDay>)

    suspend fun deleteDone(done: Done)

    fun get(julianDay: JulianDay): Flow<List<Done>>

    val histories: Flow<List<History>>
}