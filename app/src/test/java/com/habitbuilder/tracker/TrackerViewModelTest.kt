package com.habitbuilder.tracker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.habitbuilder.TestConstants.Companion.COMPLETED_MISSION_HABIT_ONE
import com.habitbuilder.TestConstants.Companion.COMPLETED_MISSION_HABIT_TWO
import com.habitbuilder.TestConstants.Companion.HABIT_ONE
import com.habitbuilder.TestConstants.Companion.HABIT_THREE
import com.habitbuilder.TestConstants.Companion.HABIT_TWO
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_HABIT_ONE
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_HABIT_TWO
import com.habitbuilder.habit.data.HabitRepository
import com.habitbuilder.mission.data.MissionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class TrackerViewModelTest {

    @get:Rule var rule: TestRule = InstantTaskExecutorRule()
    private val mockMissionRepository: MissionRepository = mock(MissionRepository::class.java)
    private val mockHabitRepository: HabitRepository = mock(HabitRepository::class.java)

    @Test
    fun monthlyHabitRecordList_returnsCorrectMonthlyHabitRecordList() = runTest{
        whenever(mockHabitRepository.getHabits())
            .thenReturn(listOf(HABIT_ONE, HABIT_TWO, HABIT_THREE))
        whenever(mockMissionRepository.getMonthlyMissions(2024, 5))
            .thenReturn(mapOf(HABIT_ONE to listOf(INCOMPLETE_MISSION_HABIT_ONE), HABIT_TWO to listOf(COMPLETED_MISSION_HABIT_TWO)))

        val localDate = LocalDate.of(2024, 5, 31)
        val trackerViewModel = TrackerViewModel(mockMissionRepository, mockHabitRepository)
        trackerViewModel.currentLocalDate.value = localDate

        val monthlyHabitRecordListLiveData = trackerViewModel.monthlyHabitRecordList
        monthlyHabitRecordListLiveData.observeForever {  }

        assertThat(monthlyHabitRecordListLiveData.value).containsExactly(
            MonthlyHabitRecord(2024, 5, HABIT_ONE, listOf(INCOMPLETE_MISSION_HABIT_ONE)),
            MonthlyHabitRecord(2024, 5, HABIT_TWO, listOf(COMPLETED_MISSION_HABIT_TWO)),
            MonthlyHabitRecord(2024, 5, HABIT_THREE, listOf()))
    }

    @Test
    fun monthlyHabitRecordList_localDateChanged_returnsCorrectMonthlyHabitRecordList() = runTest{
        whenever(mockHabitRepository.getHabits())
            .thenReturn(listOf(HABIT_ONE, HABIT_TWO, HABIT_THREE))
        whenever(mockMissionRepository.getMonthlyMissions(2024, 5))
            .thenReturn(mapOf(HABIT_ONE to listOf(INCOMPLETE_MISSION_HABIT_ONE), HABIT_TWO to listOf(COMPLETED_MISSION_HABIT_TWO)))
        whenever(mockMissionRepository.getMonthlyMissions(2024, 6))
            .thenReturn(mapOf(HABIT_ONE to listOf(COMPLETED_MISSION_HABIT_ONE), HABIT_TWO to listOf(INCOMPLETE_MISSION_HABIT_TWO)))

        val trackerViewModel = TrackerViewModel(mockMissionRepository, mockHabitRepository)
        trackerViewModel.currentLocalDate.value = LocalDate.of(2024, 5, 31)

        val monthlyHabitRecordListLiveData = trackerViewModel.monthlyHabitRecordList
        monthlyHabitRecordListLiveData.observeForever {  }

        trackerViewModel.currentLocalDate.value = LocalDate.of(2024, 6, 1)

        assertThat(monthlyHabitRecordListLiveData.value).containsExactly(
            MonthlyHabitRecord(2024, 6, HABIT_ONE, listOf(COMPLETED_MISSION_HABIT_ONE)),
            MonthlyHabitRecord(2024, 6, HABIT_TWO, listOf(INCOMPLETE_MISSION_HABIT_TWO)),
            MonthlyHabitRecord(2024, 6, HABIT_THREE, listOf()))
    }
}