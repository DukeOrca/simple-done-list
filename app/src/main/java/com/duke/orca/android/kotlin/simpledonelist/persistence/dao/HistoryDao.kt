package com.duke.orca.android.kotlin.simpledonelist.persistence.dao

import androidx.room.*
import com.duke.orca.android.kotlin.simpledonelist.history.models.History
import com.duke.orca.android.kotlin.simpledonelist.history.models.JulianDay
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(julianDay: JulianDay)

    @Delete
    fun delete(julianDay: JulianDay): Completable

    @Transaction
    @Query("SELECT * FROM julian_day ORDER BY value DESC")
    fun get(): Flow<List<History>>
}