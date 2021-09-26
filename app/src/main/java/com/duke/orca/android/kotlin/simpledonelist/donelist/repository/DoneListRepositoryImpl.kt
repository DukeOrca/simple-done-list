package com.duke.orca.android.kotlin.simpledonelist.donelist.repository

import com.duke.orca.android.kotlin.simpledonelist.donelist.datasource.local.DoneListDatasource
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DoneListRepositoryImpl @Inject constructor(private val datasource: DoneListDatasource) : DoneListRepository {
    override suspend fun insert(done: Done) {
        datasource.insert(done)
    }

    override suspend fun delete(done: Done) {
        datasource.delete(done)
    }

    override suspend fun insertJulianDay(julianDay: JulianDay) {
        datasource.insertJulianDay(julianDay)
    }

    override val doneList: Flow<List<Done>> = datasource.doneList
}