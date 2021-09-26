package com.duke.orca.android.kotlin.simpledonelist.history.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.duke.orca.android.kotlin.simpledonelist.donelist.model.Done
import kotlinx.parcelize.Parcelize

@Parcelize
data class History (
    @Embedded val julianDay: JulianDay,
    @Relation(parentColumn = "value", entityColumn = "julian_day")
    val doneList: List<Done>,
) : Parcelable