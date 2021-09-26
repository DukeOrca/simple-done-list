package com.duke.orca.android.kotlin.simpledonelist.donelist.datasource.local

import android.icu.util.Calendar
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import com.duke.orca.android.kotlin.simpledonelist.persistence.database.AppDatabase
import javax.inject.Inject

class DoneListDatasourceImpl @Inject constructor(private val database: AppDatabase) : DoneListDatasource {
    private val julianDay = Calendar.getInstance().get(Calendar.JULIAN_DAY)

    override suspend fun insert(done: Done) {
        database.doneDao().insert(done)
    }

    override suspend fun delete(done: Done) {
        database.doneDao().delete(done)
    }

    override suspend fun insertJulianDay(julianDay: JulianDay) {
        database.historyDao().insert(julianDay)
    }

    override val doneList = database.doneDao().get(julianDay)
}