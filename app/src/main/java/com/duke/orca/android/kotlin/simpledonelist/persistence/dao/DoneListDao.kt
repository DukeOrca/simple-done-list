package com.duke.orca.android.kotlin.simpledonelist.persistence.dao

import androidx.room.*
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import kotlinx.coroutines.flow.Flow

@Dao
interface DoneListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(done: Done)

    @Delete
    suspend fun delete(done: Done)

    @Transaction
    @Query("DELETE FROM done WHERE julian_day = :julianDay")
    fun delete(julianDay: Int)

    @Transaction
    @Query("SELECT * FROM done WHERE julian_day = :julianDay ORDER BY written_time DESC")
    fun get(julianDay: Int): Flow<List<Done>>
}