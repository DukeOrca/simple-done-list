package com.duke.orca.android.kotlin.simpledonelist.history.datasource.local

import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.history.models.History
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import kotlinx.coroutines.flow.Flow

interface HistoryDatasource {
    suspend fun delete(history: History)
    suspend fun delete(done: Done)

    fun get(julianDay: JulianDay): Flow<List<Done>>

    val histories: Flow<List<History>>
}