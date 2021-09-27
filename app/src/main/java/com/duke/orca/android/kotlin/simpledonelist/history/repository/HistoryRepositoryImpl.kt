package com.duke.orca.android.kotlin.simpledonelist.history.repository

import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.history.datasource.local.HistoryDatasource
import com.duke.orca.android.kotlin.simpledonelist.history.models.History
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(private val datasource: HistoryDatasource) : HistoryRepository {
    override suspend fun delete(history: History) {
        datasource.delete(history)
    }

    override suspend fun delete(done: Done) {
        datasource.delete(done)
    }

    override fun get(julianDay: JulianDay): Flow<List<Done>> {
        return datasource.get(julianDay)
    }

    override val histories: Flow<List<History>> = datasource.histories
}