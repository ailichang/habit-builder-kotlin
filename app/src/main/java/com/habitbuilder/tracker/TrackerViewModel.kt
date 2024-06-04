package com.habitbuilder.tracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.habitbuilder.habit.data.HabitRepository
import com.habitbuilder.mission.data.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TrackerViewModel @Inject constructor(private val missionRepository: MissionRepository, private val habitRepository: HabitRepository):
    ViewModel() {

    val currentLocalDate: MutableLiveData<LocalDate> = MutableLiveData()
    var monthlyHabitRecordList: LiveData<List<MonthlyHabitRecord>> =
        currentLocalDate.switchMap {localDate ->
            liveData{
                emit(setMonthlyHabitRecordList(localDate))
            }
        }

    private suspend fun setMonthlyHabitRecordList(localDate: LocalDate):List<MonthlyHabitRecord>{
        val mergeList = mutableListOf<MonthlyHabitRecord>()
        val monthlyMissionMap = missionRepository.getMonthlyMissions(localDate.year, localDate.monthValue)
        monthlyMissionMap?.let {
            val existRecordList = it.map { map -> MonthlyHabitRecord(localDate.year, localDate.monthValue, map.key, map.value)}
            mergeList.addAll(existRecordList)
        }
        habitRepository.getHabits()?.let { list ->
            val nonExistHabits = list.filterNot { habit ->  monthlyMissionMap?.keys?.contains(habit)==true }
            val nonExistRecordList = nonExistHabits.map {habit -> MonthlyHabitRecord(localDate.year, localDate.monthValue, habit, listOf())}
            mergeList.addAll(nonExistRecordList)
        }
        return mergeList
    }
}