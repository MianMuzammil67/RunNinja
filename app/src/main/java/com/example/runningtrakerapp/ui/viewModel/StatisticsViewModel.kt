package com.example.runningtrakerapp.ui.viewModel

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.runningtrakerapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val repository: MainRepository
) : ViewModel() {

//    private val startOfWeek: Long = getStartOfWeekTimestamp()
//    private val endOfWeek: Long = getEndOfWeekTimestamp()

    val totalTimeRun = repository.getTotalTimeInMillis().asLiveData()
    val totalDistance = repository.getTotalDistanceInMeters().asLiveData()
    val totalCalories = repository.getTotalCaloriesBurned().asLiveData()
    val totalAvgSpeed = repository.getTotalAvgSpeedInKMH().asLiveData()


    val runsSortedByDate = repository.getAllRunsSortedByDate().asLiveData()

//    val getTotalDistanceForWeek =
//        repository.getTotalDistanceForWeek(startOfWeek, endOfWeek).asLiveData()


//    init {
//        // Log the start and end of the week for debugging
//        logWeekBoundaries()
//        observeWeeklyDistance()
//    }
//
//    private fun logWeekBoundaries() {
//        Log.d("StatisticsViewModel", "Start of the week (Monday): $startOfWeek")
//        Log.d("StatisticsViewModel", "End of the week (Sunday): $endOfWeek")
//    }

//    private fun observeWeeklyDistance() {
//        viewModelScope.launch {
//            repository.getTotalDistanceForWeek(startOfWeek, endOfWeek).collect { distance ->
//                Log.d("StatisticsViewModel", "Total distance for week: ${distance ?: 0} meters")
//            }
//        }
//    }

}

//private fun getStartOfWeekTimestamp(): Long {
//    val calendar = Calendar.getInstance()
//    calendar.firstDayOfWeek = Calendar.MONDAY
//    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
//    calendar.set(Calendar.HOUR_OF_DAY, 0)
//    calendar.set(Calendar.MINUTE, 0)
//    calendar.set(Calendar.SECOND, 0)
//    calendar.set(Calendar.MILLISECOND, 0)
//    return calendar.timeInMillis
//}
//
//private fun getEndOfWeekTimestamp(): Long {
//    val calendar = Calendar.getInstance()
//    calendar.firstDayOfWeek = Calendar.MONDAY
//    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
//    calendar.set(Calendar.HOUR_OF_DAY, 23)
//    calendar.set(Calendar.MINUTE, 59)
//    calendar.set(Calendar.SECOND, 59)
//    calendar.set(Calendar.MILLISECOND, 999)
//    return calendar.timeInMillis
//}

