package com.example.runningtrakerapp.repository

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.runningtrakerapp.db.Dao
import com.example.runningtrakerapp.db.Run
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val dao: Dao
) {
    suspend fun insetRun(run: Run) = dao.insert(run)
    suspend fun deleteRun(run: Run) = dao.deleteRun(run)


    fun getAllRunsSortedByDate() = dao.getAllRunsSortedByDate()
    fun getAllRunsSortedByTimeInMillis() = dao.getAllRunsSortedByTimeInMillis()
    fun getAllRunsSortedByCaloriesBurned() = dao.getAllRunsSortedByCaloriesBurned()
    fun getAllRunsSortedByAvgSpeed() = dao.getAllRunsSortedByAvgSpeed()
    fun getAllRunsSortedByDistanceInMeters() = dao.getAllRunsSortedByDistanceInMeters()

    fun getTotalTimeInMillis() = dao.getTotalTimeInMillis()
    fun getTotalDistanceInMeters() = dao.getTotalDistanceInMeters()
    fun getTotalCaloriesBurned() = dao.getTotalCaloriesBurned()
    fun getTotalAvgSpeedInKMH() = dao.getTotalAvgSpeedInKMH()

    fun getTotalDistanceForWeek(startOfWeek: Long, endOfWeek: Long) =
        dao.getTotalDistanceForWeek(startOfWeek, endOfWeek)

}