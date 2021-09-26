package com.duke.orca.android.kotlin.simpledonelist.history.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "julian_day")
@Parcelize
data class JulianDay(
    @PrimaryKey
    val value: Int
) : Parcelable