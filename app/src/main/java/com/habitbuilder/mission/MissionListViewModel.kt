package com.habitbuilder.mission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.habitbuilder.IoDispatcher
import com.habitbuilder.achievement.data.AchievementRepository
import com.habitbuilder.achievement.data.ExperienceUpdateType
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.data.HabitRepository
import com.habitbuilder.habit.data.ScheduledHabitsEvent
import com.habitbuilder.mission.data.Mission
import com.habitbuilder.mission.data.MissionDetail
import com.habitbuilder.mission.data.MissionRepository
import com.habitbuilder.mission.data.ScheduledMissionEvent
import com.habitbuilder.mission.data.SearchDailyMissionsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.stream.Collectors
import javax.inject.Inject

@HiltViewModel
class MissionListViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val habitRepository: HabitRepository,
    private val achievementRepository: AchievementRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    val newScheduledMission: MediatorLiveData<ScheduledMissionEvent> = MediatorLiveData<ScheduledMissionEvent>()
    val currentLocalDate: MutableLiveData<LocalDate> = MutableLiveData()
    private val currentDailyMissions: LiveData<SearchDailyMissionsEvent>
    private val scheduledHabits: LiveData<ScheduledHabitsEvent>

    init {
        currentDailyMissions = getDailyMissions()
        scheduledHabits = getScheduledHabits()
        newScheduledMission.addSource(currentDailyMissions) { setNewScheduledMissionEvent() }
        newScheduledMission.addSource(scheduledHabits) { setNewScheduledMissionEvent() }
    }

    fun getCompletedDailyMissions(): LiveData<List<MissionDetail>> {
        return currentLocalDate.switchMap {
            missionRepository.getCompletedDailyMissions(
                it.year,
                it.monthValue,
                it.dayOfMonth
            )
        }
    }

    fun getIncompleteDailyMissions(): LiveData<List<MissionDetail>> {
        return currentLocalDate.switchMap {
            missionRepository.getIncompleteDailyMissions(
                it.year,
                it.monthValue,
                it.dayOfMonth
            )
        }
    }

    fun insert(vararg missions: Mission) {
        viewModelScope.launch (dispatcher){
            missionRepository.insert(*missions)
        }
    }

    fun update(mission:Mission) {
        viewModelScope.launch (dispatcher) {
            missionRepository.update(mission)
        }
    }

    fun increaseExperiencePoints(habit: Habit){
        viewModelScope.launch (dispatcher) {
            achievementRepository.update(habit, ExperienceUpdateType.INCREASE)
        }
    }

    fun setMissionComplete(missionDetail: MissionDetail, updateType: ExperienceUpdateType){
        viewModelScope.launch (dispatcher) {
            missionRepository.update(missionDetail.mission)
            achievementRepository.update(missionDetail.habit, updateType)
        }
    }

    fun delete(mission: Mission) {
        viewModelScope.launch (dispatcher) {
            missionRepository.delete(mission)
        }
    }

    private fun getScheduledHabits(): LiveData<ScheduledHabitsEvent> {
        return currentLocalDate.switchMap { localDate ->
            habitRepository.getHabitList().map { habitList ->
                ScheduledHabitsEvent(habitList.stream().filter { habit -> habit.isScheduled(localDate.dayOfWeek)}.collect(Collectors.toList()), localDate)
            }
        }
    }

    private fun getDailyMissions(): LiveData<SearchDailyMissionsEvent> {
        return currentLocalDate.switchMap {
            missionRepository.getDailyMissions(it.year, it.monthValue, it.dayOfMonth).map {
                list -> SearchDailyMissionsEvent(list, it)
            }
        }
    }



    private fun setNewScheduledMissionEvent() {
        if (currentLocalDate.value == null || scheduledHabits.value == null || currentDailyMissions.value == null) return
        currentLocalDate.value?.let { date ->
            val isScheduledHabitsDifferentDate:Boolean = scheduledHabits.value?.scheduledDate?.isEqual(date) == false
            val isScheduledHabitsEmpty:Boolean = scheduledHabits.value?.habitList?.isEmpty() == true
            val isCurrentDailyMissionsDifferentDate:Boolean = currentDailyMissions.value?.scheduledDate?.isEqual(date) == false
            val isCurrentDailyMissionsNotEmpty:Boolean = currentDailyMissions.value?.missionList?.isEmpty() == false
            val isNewScheduledMissionsEventSameDate:Boolean = newScheduledMission.value?.missionDate?.isEqual(date) == true
            val isNewScheduledMissionsEventNotExist:Boolean = newScheduledMission.value == null
            val isNewScheduledMissionsEventHandled:Boolean = newScheduledMission.value?.hasBeenHandled == true
            if(isScheduledHabitsEmpty ||  isScheduledHabitsDifferentDate || isCurrentDailyMissionsDifferentDate
                || (isNewScheduledMissionsEventSameDate && isNewScheduledMissionsEventHandled)
                || (isCurrentDailyMissionsNotEmpty && isNewScheduledMissionsEventNotExist)) return
            scheduledHabits.value?.let { event ->
                val missions: ArrayList<Mission> = ArrayList()
                event.habitList.forEach{ habit ->
                    currentDailyMissions.value?.let{
                        if(!it.missionList.stream().map{mission -> mission.habitId}.collect(Collectors.toList()).contains(habit.habitId)){
                            missions.add(Mission(habit, date))
                        }
                    }
                }
                if(missions.isNotEmpty()) {
                    val scheduledMissionEvent = ScheduledMissionEvent(missions, date)
                    newScheduledMission.value = scheduledMissionEvent
                }
            }
        }
    }
}