package com.example.runningtrakerapp.ui.viewModel

import android.content.SharedPreferences
import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.runningtrakerapp.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class WeekGoalViewModel @Inject constructor(
    @Named("weeklyGoalPreferences") private val weeklyGoalSharedPreferences: SharedPreferences,
    val repository: MainRepository
) : ViewModel() {

    private val startOfWeek: Long = getStartOfWeekTimestamp()
    private val endOfWeek: Long = getEndOfWeekTimestamp()

//    val getTotalDistanceForWeek = viewModelScope.launch {
//        repository.getTotalDistanceForWeek(startOfWeek, endOfWeek).asLiveData()


    val getTotalDistanceForWeek =
        repository.getTotalDistanceForWeek(startOfWeek, endOfWeek).asLiveData()

    private val _weeklyGoalLiveData = MutableLiveData<Int>()
    val weeklyGoalLiveData: LiveData<Int> get() = _weeklyGoalLiveData


    init {
//        _weeklyGoalLiveData.postValue(getWeeklyGoal())
        _weeklyGoalLiveData.value = getWeeklyGoal()

        logWeekBoundaries()
//        observeWeeklyDistance(startOfWeek,endOfWeek)
    }

    private fun logWeekBoundaries() {
        Log.d("WeekGoalViewModel", "Start of the week (Monday): $startOfWeek")
        Log.d("WeekGoalViewModel", "End of the week (Sunday): $endOfWeek")
    }

    private fun observeWeeklyDistance(startOfWeek :Long , endOfWeek :Long) {
        viewModelScope.launch {
            repository.getTotalDistanceForWeek(startOfWeek, endOfWeek).collect { distance ->
                Log.d("WeekGoalViewModel", "Total distance for week: ${distance ?: 0} meters")
                _weeklyGoalLiveData .value = distance
            }
        }
    }

    fun setWeeklyGoal(goal: Int) {
        val editor = weeklyGoalSharedPreferences.edit()
        editor.putInt("weekly_goal_key", goal)
        editor.apply()

        _weeklyGoalLiveData.postValue(goal)
//        _weeklyGoalStateFlow.value = goal
        Log.d("getWeeklyGoal", "set goal =  $goal")


    }

    fun getWeeklyGoal(): Int {
        val goal= weeklyGoalSharedPreferences.getInt("weekly_goal_key", 0)
        Log.d("getWeeklyGoal", "getWeeklyGoal =  $goal")
        return goal
    }
}

private fun getStartOfWeekTimestamp(): Long {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}

private fun getEndOfWeekTimestamp(): Long {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis
}
