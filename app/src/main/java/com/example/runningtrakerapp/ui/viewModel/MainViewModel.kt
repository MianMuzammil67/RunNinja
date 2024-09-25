package com.example.runningtrakerapp.ui.viewModel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.runningtrakerapp.db.Run
import com.example.runningtrakerapp.repository.MainRepository
import com.example.runningtrakerapp.utill.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {


    private val runSortedByDate = repository.getAllRunsSortedByDate().asLiveData()
    private val runSortedByDistance = repository.getAllRunsSortedByDistanceInMeters().asLiveData()
    private val runsSortedByTimeInMillis = repository.getAllRunsSortedByTimeInMillis().asLiveData()
    private val runSortedByCalories = repository.getAllRunsSortedByCaloriesBurned().asLiveData()
    private val runSortedByAvgSpeed = repository.getAllRunsSortedByAvgSpeed().asLiveData()

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE

    init {
        runs.addSource(runSortedByDate) { result ->
            if(sortType == SortType.DATE) {
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByAvgSpeed) { result ->
            if(sortType == SortType.AVG_SPEED) {
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByCalories) { result ->
            if(sortType == SortType.CALORIES_BURNED) {
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByDistance) { result ->
            if(sortType == SortType.DISTANCE) {
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsSortedByTimeInMillis) { result ->
            if(sortType == SortType.RUNNING_TIME) {
                result?.let { runs.value = it }
            }
        }
    }
    fun sortRun(sortType: SortType) = when(sortType) {
        SortType.DATE -> runSortedByDate.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runSortedByAvgSpeed.value?.let { runs.value = it }
        SortType.DISTANCE -> runSortedByDistance.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runSortedByCalories.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }

    fun saveRun(run: Run) = viewModelScope.launch {
        repository.insetRun(run)
    }



}