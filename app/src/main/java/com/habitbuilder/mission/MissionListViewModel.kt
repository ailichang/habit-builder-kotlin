package com.habitbuilder.mission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.habitbuilder.IoDispatcher
import com.habitbuilder.achievement.data.AchievementRepository
import com.habitbuilder.achievement.data.ExperienceUpdateType
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.data.HabitRepository
import com.habitbuilder.mission.data.Mission
import com.habitbuilder.mission.data.MissionDetail
import com.habitbuilder.mission.data.MissionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.util.UUID
import java.util.stream.Collectors
import javax.inject.Inject

@HiltViewModel
class MissionListViewModel @Inject constructor(
    private val missionRepository: MissionRepository,
    private val habitRepository: HabitRepository,
    private val achievementRepository: AchievementRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    val currentLocalDate: MutableLiveData<LocalDate> = MutableLiveData()

    private var scheduledMissionLocalDate: LocalDate? = null
    private val mutex = Mutex()

    fun getCompletedDailyMissions(): LiveData<List<MissionDetail>> {
        return currentLocalDate.switchMap { missionRepository.getCompletedDailyMissions(it) }
    }

    fun getIncompleteDailyMissions(): LiveData<List<MissionDetail>> {
        return currentLocalDate.switchMap { missionRepository.getIncompleteDailyMissions(it) }
    }

    fun scheduleNewMission(localDate: LocalDate){
        viewModelScope.launch (dispatcher) {
            mutex.withLock {
                val currentMissionHabitIds:List<UUID>? = missionRepository.getDailyMissionHabitIds(localDate)
                val scheduledHabits:List<Habit>? = habitRepository.getScheduledHabits(localDate)
                if (scheduledMissionLocalDate?.isEqual(localDate) == true) return@launch
                scheduledHabits?.let { list ->
                    val missions:Array<Mission> = list.stream()
                        .filter{ habit -> currentMissionHabitIds.isNullOrEmpty() || !currentMissionHabitIds.contains(habit.habitId)}
                        .map { habit -> Mission(habit, localDate)}.toArray{size -> arrayOfNulls<Mission>(size)}
                    if(missions.isNotEmpty()) {
                        insertMissions(missions)
                    }
                }
                missionRepository.getDailyMissions(localDate)?.let { list ->
                    val missionDetails:List<MissionDetail> = list.stream()
                        .filter{ missionDetail -> scheduledHabits.isNullOrEmpty() || !scheduledHabits.any { habit -> habit.habitId == missionDetail.mission.habitId }}
                        .collect(Collectors.toList())
                    if(missionDetails.isNotEmpty()) {
                        deleteMissions(missionDetails)
                    }
                }
                scheduledMissionLocalDate = localDate
            }
        }
    }

    fun updateMission(mission:Mission) {
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
    private suspend fun insertMissions(missions: Array<Mission>) {
        missionRepository.insert(*missions)
    }
    private suspend fun deleteMissions(missionDetails: List<MissionDetail>) {
        missionRepository.delete(*missionDetails.map{ detail -> detail.mission}.toTypedArray())
        for (missionDetail in missionDetails){
            if(missionDetail.mission.isCompleted){
                achievementRepository.update(missionDetail.habit, ExperienceUpdateType.DECREASE)
            }
        }
    }
}