package com.duke.orca.android.kotlin.simpledonelist.donelist.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "done")
@Parcelize
data class Done(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val content: String,
    @ColumnInfo(name = "julian_day")
    val julianDay: Int,
    @ColumnInfo(name = "written_time")
    val writtenTime: Long
) : Parcelable