package com.habitbuilder.mission

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.habitbuilder.TestConstants.Companion.COMPLETED_MISSION_DETAIL_HABIT_ONE
import com.habitbuilder.TestConstants.Companion.COMPLETED_MISSION_DETAIL_HABIT_TWO
import com.habitbuilder.TestConstants.Companion.COMPLETED_MISSION_HABIT_ONE
import com.habitbuilder.TestConstants.Companion.COMPLETED_MISSION_HABIT_TWO
import com.habitbuilder.TestConstants.Companion.HABIT_ONE
import com.habitbuilder.TestConstants.Companion.HABIT_ONE_ID
import com.habitbuilder.TestConstants.Companion.HABIT_TWO
import com.habitbuilder.TestConstants.Companion.HABIT_TWO_ID
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_DETAIL_HABIT_ONE
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_DETAIL_HABIT_TWO
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_HABIT_ONE
import com.habitbuilder.achievement.data.AchievementRepository
import com.habitbuilder.achievement.data.ExperienceUpdateType
import com.habitbuilder.habit.data.HabitRepository
import com.habitbuilder.mission.data.Counter
import com.habitbuilder.mission.data.Mission
import com.habitbuilder.mission.data.MissionRepository
import com.habitbuilder.mission.data.Timer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.UUID

class MissionListViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val mockHabitRepository: HabitRepository = Mockito.mock(HabitRepository::class.java)
    private val mockMissionRepository: MissionRepository =
        Mockito.mock(MissionRepository::class.java)
    private val mockAchievementRepository: AchievementRepository =
        Mockito.mock(AchievementRepository::class.java)
    private val testDispatcher = StandardTestDispatcher()
    private val missionListViewModel = MissionListViewModel(mockMissionRepository, mockHabitRepository, mockAchievementRepository, testDispatcher)

    @Test
    fun getCompletedDailyMissions_localDateChanged_returnsCorrectCompletedDailyMissions(){
        whenever(mockMissionRepository.getCompletedDailyMissions(LocalDate.of(2024,5,31)))
            .thenReturn(MutableLiveData(listOf(COMPLETED_MISSION_DETAIL_HABIT_ONE)))
        whenever(mockMissionRepository.getCompletedDailyMissions(LocalDate.of(2024,6,2)))
            .thenReturn(MutableLiveData(listOf(COMPLETED_MISSION_DETAIL_HABIT_TWO)))
        missionListViewModel.getCompletedDailyMissions().observeForever {}
        missionListViewModel.currentLocalDate.value = LocalDate.of(2024,5,31)
        val mayThirtyCompletedMissions = missionListViewModel.getCompletedDailyMissions().value

        missionListViewModel.currentLocalDate.value = LocalDate.of(2024,6,2)

        assertThat(mayThirtyCompletedMissions).containsExactly(COMPLETED_MISSION_DETAIL_HABIT_ONE)
        assertThat(missionListViewModel.getCompletedDailyMissions().value).containsExactly(COMPLETED_MISSION_DETAIL_HABIT_TWO)
    }

    @Test
    fun getIncompleteDailyMissions_localDateChanged_returnsCorrectIncompleteDailyMissions(){
        whenever(mockMissionRepository.getIncompleteDailyMissions(LocalDate.of(2024,5,31)))
            .thenReturn(MutableLiveData(listOf(INCOMPLETE_MISSION_DETAIL_HABIT_ONE)))
        whenever(mockMissionRepository.getIncompleteDailyMissions(LocalDate.of(2024,6,2)))
            .thenReturn(MutableLiveData(listOf(INCOMPLETE_MISSION_DETAIL_HABIT_TWO)))
        missionListViewModel.getIncompleteDailyMissions().observeForever {}
        missionListViewModel.currentLocalDate.value = LocalDate.of(2024,5,31)
        val mayThirtyIncompleteMissions = missionListViewModel.getIncompleteDailyMissions().value

        missionListViewModel.currentLocalDate.value = LocalDate.of(2024,6,2)

        assertThat(mayThirtyIncompleteMissions).containsExactly(INCOMPLETE_MISSION_DETAIL_HABIT_ONE)
        assertThat(missionListViewModel.getIncompleteDailyMissions().value).containsExactly(INCOMPLETE_MISSION_DETAIL_HABIT_TWO)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun scheduleNewMission_noScheduledHabitsAndNoDailyMissions_doesNotCallMissionRepositoryInsertAndDelete() = runTest (testDispatcher){
        val localDate = LocalDate.of(2024,6,1)
        whenever(mockMissionRepository.getDailyMissions(localDate)).thenReturn(null)
        whenever(mockMissionRepository.getDailyMissionHabitIds(localDate)).thenReturn(null)
        whenever(mockHabitRepository.getScheduledHabits(localDate)).thenReturn(null)

        missionListViewModel.scheduleNewMission(localDate)
        advanceUntilIdle()

        verify(mockMissionRepository, never()).insert(any())
        verify(mockMissionRepository, never()).delete(any())
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun scheduleNewMission_scheduledHabitsExistsAndNoDailyMissions_callsMissionRepositoryInsert() = runTest (testDispatcher){
        val localDate = LocalDate.of(2024,6,1)
        whenever(mockMissionRepository.getDailyMissions(localDate)).thenReturn(null)
        whenever(mockMissionRepository.getDailyMissionHabitIds(localDate)).thenReturn(null)
        whenever(mockHabitRepository.getScheduledHabits(localDate)).thenReturn(listOf(HABIT_ONE))
        val uuid = UUID.fromString("0eb9ce18-d8d8-4446-a6f2-552e382b8c66")
        mockStatic(UUID::class.java).use {
            it.`when`<UUID>{ UUID.randomUUID()}.thenReturn(uuid)

            missionListViewModel.scheduleNewMission(localDate)
            advanceUntilIdle()

            verify(mockMissionRepository).insert(Mission(missionId = uuid, habitId = HABIT_ONE_ID, year = 2024, month = 6, day = 1, timer = Timer(HABIT_ONE.targetDuration)))
            verify(mockMissionRepository, never()).delete(any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun scheduleNewMission_scheduledHabitsNotContainedInDailyMissionHabits_callsMissionRepositoryInsert() = runTest (testDispatcher){
        val localDate = LocalDate.of(2024,6,2)
        whenever(mockMissionRepository.getDailyMissions(localDate)).thenReturn(listOf(COMPLETED_MISSION_DETAIL_HABIT_ONE))
        whenever(mockMissionRepository.getDailyMissionHabitIds(localDate)).thenReturn(listOf(HABIT_ONE_ID))
        whenever(mockHabitRepository.getScheduledHabits(localDate)).thenReturn(listOf(HABIT_ONE, HABIT_TWO))
        val uuid = UUID.fromString("0eb9ce18-d8d8-4446-a6f2-552e382b8c66")
        mockStatic(UUID::class.java).use {
            it.`when`<UUID>{ UUID.randomUUID()}.thenReturn(uuid)

            missionListViewModel.scheduleNewMission(localDate)
            advanceUntilIdle()

            verify(mockMissionRepository).insert(Mission(missionId = uuid, habitId = HABIT_TWO_ID, year = 2024, month = 6, day = 2, counter = Counter(
                HABIT_TWO.targetCount)))
            verify(mockMissionRepository, never()).delete(any())
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun scheduleNewMission_dailyMissionHabitsExactlySameAsScheduledHabits_doesNotCallMissionRepositoryInsertAndDelete() = runTest (testDispatcher){
        val localDate = LocalDate.of(2024,6,2)
        whenever(mockMissionRepository.getDailyMissions(localDate)).thenReturn(listOf(COMPLETED_MISSION_DETAIL_HABIT_ONE))
        whenever(mockMissionRepository.getDailyMissionHabitIds(localDate)).thenReturn(listOf(HABIT_ONE_ID))
        whenever(mockHabitRepository.getScheduledHabits(localDate)).thenReturn(listOf(HABIT_ONE))

        missionListViewModel.scheduleNewMission(localDate)
        advanceUntilIdle()

        verify(mockMissionRepository, never()).insert(any())
        verify(mockMissionRepository, never()).delete(any())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun scheduleNewMission_dailyMissionExistsAndNoScheduledHabits_callsMissionRepositoryDelete() = runTest (testDispatcher){
        val localDate = LocalDate.of(2024,6,2)
        whenever(mockMissionRepository.getDailyMissions(localDate)).thenReturn(listOf(COMPLETED_MISSION_DETAIL_HABIT_ONE, COMPLETED_MISSION_DETAIL_HABIT_TWO))
        whenever(mockMissionRepository.getDailyMissionHabitIds(localDate)).thenReturn(listOf(HABIT_ONE_ID, HABIT_TWO_ID))
        whenever(mockHabitRepository.getScheduledHabits(localDate)).thenReturn(null)

        missionListViewModel.scheduleNewMission(localDate)
        advanceUntilIdle()

        verify(mockMissionRepository).delete(*listOf(COMPLETED_MISSION_HABIT_ONE,COMPLETED_MISSION_HABIT_TWO).toTypedArray())
        verify(mockMissionRepository, never()).insert(any())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun scheduleNewMission_dailyMissionHabitsNotContainedInScheduledHabits_callsMissionRepositoryDelete() = runTest (testDispatcher){
        val localDate = LocalDate.of(2024,6,2)
        whenever(mockMissionRepository.getDailyMissions(localDate)).thenReturn(listOf(COMPLETED_MISSION_DETAIL_HABIT_ONE, COMPLETED_MISSION_DETAIL_HABIT_TWO))
        whenever(mockMissionRepository.getDailyMissionHabitIds(localDate)).thenReturn(listOf(HABIT_ONE_ID, HABIT_TWO_ID))
        whenever(mockHabitRepository.getScheduledHabits(localDate)).thenReturn(listOf(HABIT_ONE))

        missionListViewModel.scheduleNewMission(localDate)
        advanceUntilIdle()

        verify(mockMissionRepository).delete(*listOf(COMPLETED_MISSION_HABIT_TWO).toTypedArray())
        verify(mockMissionRepository, never()).insert(any())
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun scheduleNewMission_calledTwice_callsMissionRepositoryInsertDeleteOnce() = runTest (testDispatcher){
        val localDate = LocalDate.of(2024,6,2)
        whenever(mockMissionRepository.getDailyMissions(localDate)).thenReturn(listOf(COMPLETED_MISSION_DETAIL_HABIT_TWO))
        whenever(mockMissionRepository.getDailyMissionHabitIds(localDate)).thenReturn(listOf(HABIT_TWO_ID))
        whenever(mockHabitRepository.getScheduledHabits(localDate)).thenReturn(listOf(HABIT_ONE))
        val uuid = UUID.fromString("0eb9ce18-d8d8-4446-a6f2-552e382b8c66")
        mockStatic(UUID::class.java).use {
            it.`when`<UUID>{ UUID.randomUUID()}.thenReturn(uuid)
            val newMission = Mission(missionId = uuid, habitId = HABIT_ONE_ID, year = 2024, month = 6, day = 2, timer = Timer(
                HABIT_ONE.targetDuration))
            val removeMissions = listOf(COMPLETED_MISSION_HABIT_TWO)

            missionListViewModel.scheduleNewMission(localDate)
            advanceUntilIdle()
            missionListViewModel.scheduleNewMission(localDate)
            advanceUntilIdle()

            verify(mockMissionRepository, times(1)).insert(newMission)
            verify(mockMissionRepository, times(1)).delete(*removeMissions.toTypedArray())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateMission_callsMissionRepositoryUpdate() = runTest(testDispatcher){
        missionListViewModel.updateMission(INCOMPLETE_MISSION_HABIT_ONE)
        advanceUntilIdle()

        verify(mockMissionRepository).update(INCOMPLETE_MISSION_HABIT_ONE)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun increaseExperiencePoints_callsAchievementRepositoryUpdate() = runTest (testDispatcher){
        missionListViewModel.increaseExperiencePoints(HABIT_ONE)
        advanceUntilIdle()

        verify(mockAchievementRepository).update(HABIT_ONE, ExperienceUpdateType.INCREASE)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun setMissionComplete_callsMissionRepositoryUpdate() = runTest (testDispatcher){
        missionListViewModel.setMissionComplete(COMPLETED_MISSION_DETAIL_HABIT_ONE, ExperienceUpdateType.INCREASE)
        advanceUntilIdle()

        verify(mockMissionRepository).update(COMPLETED_MISSION_HABIT_ONE)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun setMissionComplete_increase_callsAchievementRepositoryUpdateWithExperienceUpdateTypeIncrease() = runTest (testDispatcher){
        missionListViewModel.setMissionComplete(COMPLETED_MISSION_DETAIL_HABIT_ONE, ExperienceUpdateType.INCREASE)
        advanceUntilIdle()

        verify(mockAchievementRepository).update(HABIT_ONE, ExperienceUpdateType.INCREASE)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun setMissionComplete_decrease_callsAchievementRepositoryUpdateWithExperienceUpdateTypeDecrease() = runTest (testDispatcher){
        missionListViewModel.setMissionComplete(COMPLETED_MISSION_DETAIL_HABIT_ONE, ExperienceUpdateType.DECREASE)
        advanceUntilIdle()

        verify(mockAchievementRepository).update(HABIT_ONE, ExperienceUpdateType.DECREASE)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun setMissionComplete_none_callsAchievementRepositoryUpdateWithExperienceUpdateTypeNone() = runTest (testDispatcher){
        missionListViewModel.setMissionComplete(COMPLETED_MISSION_DETAIL_HABIT_ONE, ExperienceUpdateType.NONE)
        advanceUntilIdle()

        verify(mockAchievementRepository).update(HABIT_ONE, ExperienceUpdateType.NONE)
    }

}