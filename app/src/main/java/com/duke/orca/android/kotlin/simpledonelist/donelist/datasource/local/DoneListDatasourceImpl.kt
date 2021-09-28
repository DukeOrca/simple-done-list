package com.duke.orca.android.kotlin.simpledonelist.donelist.datasource.local

import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import com.duke.orca.android.kotlin.simpledonelist.persistence.database.AppDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DoneListDatasourceImpl @Inject constructor(private val database: AppDatabase) : DoneListDatasource {
    override fun getDoneList(julianDay: Int): Flow<List<Done>> {
        return database.doneDao().get(julianDay)
    }

    override suspend fun insert(done: Done) {
        database.doneDao().insert(done)
    }

    override suspend fun delete(done: Done) {
        database.doneDao().delete(done)
    }

    override suspend fun insertJulianDay(julianDay: JulianDay) {
        database.historyDao().insert(julianDay)
    }
}