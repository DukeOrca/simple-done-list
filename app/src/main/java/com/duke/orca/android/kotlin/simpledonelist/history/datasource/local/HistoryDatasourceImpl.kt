package com.duke.orca.android.kotlin.simpledonelist.history.datasource.local

import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.history.models.History
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import com.duke.orca.android.kotlin.simpledonelist.persistence.database.AppDatabase
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryDatasourceImpl @Inject constructor(private val database: AppDatabase) : HistoryDatasource {
    override suspend fun delete(history: History) {
        val julianDay = history.julianDay

        database.historyDao().delete(julianDay)
            .subscribeOn(Schedulers.io())
            .subscribe {
                database.doneDao().delete(julianDay.value)
            }
    }

    override suspend fun delete(done: Done) {
        database.doneDao().delete(done)
    }

    override fun get(julianDay: JulianDay): Flow<List<Done>> {
        return database.doneDao().get(julianDay.value)
    }

    override val histories = database.historyDao().get()
}