package com.example.runningtrakerapp.db

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "running_table")
@Parcelize
data class Run(
    var image : Bitmap? = null,
    var timestamp : Long = 0L,
    var timeInMillis : Long = 0L,
    var avgSpeedInKMH : Int = 0,
    var distanceInMeters : Int = 0,
    var caloriesBurned: Int = 0
):Parcelable  {
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null
}