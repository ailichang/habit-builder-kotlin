package com.habitbuilder.tracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.mission.data.Mission
import com.habitbuilder.mission.data.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(private val missionRepository: MissionRepository):
    ViewModel() {

    var monthlyHabitRecordList:LiveData<List<MonthlyHabitRecord>>
    val currentLocalDate: MutableLiveData<LocalDate> = MutableLiveData()

    private val existingMonthlyMissions: LiveData<Map<Habit, List<Mission>>>
    private val nonExistingMonthlyMissions: LiveData<Map<Habit, List<Mission>>>
    private val mergeMonthlyMissions = MediatorLiveData<Map<Habit, List<Mission>>>()
    init {
        currentLocalDate.value = LocalDate.now()
        existingMonthlyMissions = currentLocalDate.switchMap { missionRepository.getMonthlyMissions(it.year, it.monthValue)}
        nonExistingMonthlyMissions = missionRepository.getNonExistMissions()
        mergeMonthlyMissions.addSource(existingMonthlyMissions) { setMonthlyHabitRecordList() }
        mergeMonthlyMissions.addSource(nonExistingMonthlyMissions) { setMonthlyHabitRecordList()}
        monthlyHabitRecordList = currentLocalDate.switchMap { localDate ->
            mergeMonthlyMissions.map { missionMap ->
                missionMap.entries.map {
                    MonthlyHabitRecord(localDate.year, localDate.monthValue, it.key, it.value)
                }
            }
        }

    }

    private fun setMonthlyHabitRecordList() {
        val monthlyMissionsMap: MutableMap<Habit, List<Mission>> = HashMap()
        existingMonthlyMissions.value?.let {
            monthlyMissionsMap.putAll(it)
        }
        nonExistingMonthlyMissions.value?.let {
            monthlyMissionsMap.putAll(it)
        }
        mergeMonthlyMissions.value = monthlyMissionsMap
    }
}