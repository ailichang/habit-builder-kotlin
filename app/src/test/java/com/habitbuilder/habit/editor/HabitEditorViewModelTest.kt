package com.habitbuilder.habit.editor

import com.google.common.truth.Truth.assertThat
import com.habitbuilder.TestConstants.Companion.HABIT_ONE
import com.habitbuilder.TestConstants.Companion.HABIT_ONE_ID
import com.habitbuilder.achievement.data.Achievement
import com.habitbuilder.achievement.data.AchievementRepository
import com.habitbuilder.achievement.data.ExperienceUpdateType
import com.habitbuilder.habit.data.Category
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.data.HabitRepository
import com.habitbuilder.habit.data.Priority
import com.habitbuilder.habit.data.Type
import com.habitbuilder.mission.data.Mission
import com.habitbuilder.mission.data.MissionRepository
import com.habitbuilder.mission.data.Timer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.UUID


class HabitEditorViewModelTest {

    private val mockHabitRepository: HabitRepository = mock(HabitRepository::class.java)
    private val mockMissionRepository: MissionRepository = mock(MissionRepository::class.java)
    private val mockAchievementRepository: AchievementRepository = mock(AchievementRepository::class.java)
    private val testDispatcher = StandardTestDispatcher()
    private val habitEditorViewModel = HabitEditorViewModel(mockHabitRepository, mockMissionRepository, mockAchievementRepository, testDispatcher)
    private val updatedHabitOne = Habit(HABIT_ONE_ID, Type.TIMER, Category.LEARNING, Priority.MEDIUM,
        title = "Unit testing", description = "description...", location = "somewhere", startTime = "18:00", endTime = "19:00",
        targetDuration = 600L, createdDate = "2024-05-31", scheduledDays = arrayListOf(false, true, false, true, false, false, true)
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun create_callsHabitRepositoryInsert() = runTest(testDispatcher){
        habitEditorViewModel.create(HABIT_ONE)
        advanceUntilIdle()

        verify(mockHabitRepository).insert(HABIT_ONE)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun create_isScheduled_callsMissionRepositoryInsert()= runTest(testDispatcher){
        val localDate = LocalDate.of(2024, 5, 31)
        val uuid = UUID.fromString("0eb9ce18-d8d8-4446-a6f2-552e382b8c66")
        mockStatic(LocalDate::class.java).use { theMock ->
            theMock.`when`<LocalDate> { LocalDate.now() }.thenReturn(localDate)
            mockStatic(UUID::class.java).use {
                it.`when`<UUID>{UUID.randomUUID()}.thenReturn(uuid)

                habitEditorViewModel.create(HABIT_ONE)
                advanceUntilIdle()

                assertThat(HABIT_ONE.isScheduled(localDate)).isTrue()
                verify(mockMissionRepository).insert(Mission(missionId = uuid, habitId = HABIT_ONE_ID, year = 2024, month = 5, day = 31, timer = Timer(
                    HABIT_ONE.targetDuration)))
            }

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun create_isNotScheduled_callsMissionRepositoryInsert()= runTest(testDispatcher){
        val localDate = LocalDate.of(2024, 5, 29)
        val uuid = UUID.fromString("0eb9ce18-d8d8-4446-a6f2-552e382b8c66")
        mockStatic(LocalDate::class.java).use { theMock ->
            theMock.`when`<LocalDate> { LocalDate.now() }.thenReturn(localDate)
            mockStatic(UUID::class.java).use {
                it.`when`<UUID>{UUID.randomUUID()}.thenReturn(uuid)

                habitEditorViewModel.create(HABIT_ONE)
                advanceUntilIdle()

                assertThat(HABIT_ONE.isScheduled(localDate)).isFalse()
                verify(mockMissionRepository, never()).insert(Mission(missionId = uuid, habitId = HABIT_ONE_ID, year = 2024, month = 5, day = 29, timer = Timer(
                    HABIT_ONE.targetDuration)))
            }

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun create_callsAchievementRepositoryInsert()= runTest(testDispatcher){
        habitEditorViewModel.create(HABIT_ONE)
        advanceUntilIdle()

        verify(mockAchievementRepository).insert(Achievement(habitId = HABIT_ONE.habitId))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun delete_callsHabitRepositoryDelete() = runTest(testDispatcher){
        habitEditorViewModel.delete(HABIT_ONE)
        advanceUntilIdle()

        verify(mockHabitRepository).delete(HABIT_ONE)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun update_callsHabitRepositoryUpdate()= runTest(testDispatcher){
        habitEditorViewModel.update(HABIT_ONE, updatedHabitOne)
        advanceUntilIdle()

        verify(mockHabitRepository).update(updatedHabitOne)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun update_callsMissionRepositoryUpdateCondition()= runTest(testDispatcher){
        val localDate = LocalDate.of(2024, 5, 29)
        mockStatic(LocalDate::class.java).use { theMock ->
            theMock.`when`<LocalDate> { LocalDate.now() }.thenReturn(localDate)

            habitEditorViewModel.update(HABIT_ONE, updatedHabitOne)
            advanceUntilIdle()

            verify(mockMissionRepository).updateCondition(habitId = HABIT_ONE_ID, localDate, targetCount = 0, duration = 600L)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun update_wasNotScheduledAndIsScheduled_callsMissionRepositoryInsert()= runTest(testDispatcher){
        val localDate = LocalDate.of(2024, 5, 28)
        val uuid = UUID.fromString("0eb9ce18-d8d8-4446-a6f2-552e382b8c66")
        mockStatic(LocalDate::class.java).use { theMock ->
            theMock.`when`<LocalDate> { LocalDate.now() }.thenReturn(localDate)
            mockStatic(UUID::class.java).use {
                it.`when`<UUID>{UUID.randomUUID()}.thenReturn(uuid)

                habitEditorViewModel.update(HABIT_ONE, updatedHabitOne)
                advanceUntilIdle()

                assertThat(HABIT_ONE.isScheduled(localDate)).isFalse()
                assertThat(updatedHabitOne.isScheduled(localDate)).isTrue()
                verify(mockMissionRepository).insert(Mission(missionId = uuid, habitId = HABIT_ONE_ID, year = localDate.year, month = localDate.monthValue, day = localDate.dayOfMonth, timer = Timer(
                    updatedHabitOne.targetDuration)))
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun update_wasScheduledAndIsNotScheduled_callsMissionRepositoryDelete()= runTest(testDispatcher){
        val localDate = LocalDate.of(2024, 6, 1)
        mockStatic(LocalDate::class.java).use { theMock ->
            theMock.`when`<LocalDate> { LocalDate.now() }.thenReturn(localDate)

            habitEditorViewModel.update(HABIT_ONE, updatedHabitOne)
            advanceUntilIdle()

            assertThat(HABIT_ONE.isScheduled(localDate)).isTrue()
            assertThat(updatedHabitOne.isScheduled(localDate)).isFalse()
            verify(mockMissionRepository).delete(listOf(HABIT_ONE_ID), localDate)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun update_wasScheduledAndIsNotScheduledAndMissionCompleted_callsAchievementRepositoryUpdate()= runTest(testDispatcher){
        val localDate = LocalDate.of(2024, 6, 1)
        mockStatic(LocalDate::class.java).use { theMock ->
            theMock.`when`<LocalDate> { LocalDate.now() }.thenReturn(localDate)
            whenever(mockMissionRepository.isDailyMissionCompleted(HABIT_ONE_ID, localDate)).thenReturn(true)

            habitEditorViewModel.update(HABIT_ONE, updatedHabitOne)
            advanceUntilIdle()

            assertThat(HABIT_ONE.isScheduled(localDate)).isTrue()
            assertThat(updatedHabitOne.isScheduled(localDate)).isFalse()
            verify(mockAchievementRepository).update(HABIT_ONE, ExperienceUpdateType.DECREASE)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun update_wasScheduledAndIsNotScheduledAndMissionNotCompleted_doesNotCallAchievementRepositoryUpdate()= runTest(testDispatcher){
        val localDate = LocalDate.of(2024, 6, 1)
        mockStatic(LocalDate::class.java).use { theMock ->
            theMock.`when`<LocalDate> { LocalDate.now() }.thenReturn(localDate)
            whenever(mockMissionRepository.isDailyMissionCompleted(HABIT_ONE_ID, localDate)).thenReturn(false)

            habitEditorViewModel.update(HABIT_ONE, updatedHabitOne)
            advanceUntilIdle()

            assertThat(HABIT_ONE.isScheduled(localDate)).isTrue()
            assertThat(updatedHabitOne.isScheduled(localDate)).isFalse()
            verify(mockAchievementRepository, never()).update(HABIT_ONE, ExperienceUpdateType.DECREASE)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun update_wasScheduledAndIsScheduled_doesNotInsertDeleteMissionAndUpdateAchievement()= runTest(testDispatcher){
        val localDate = LocalDate.of(2024, 6, 2)
        mockStatic(LocalDate::class.java).use { theMock ->
            theMock.`when`<LocalDate> { LocalDate.now() }.thenReturn(localDate)

            habitEditorViewModel.update(HABIT_ONE, updatedHabitOne)
            advanceUntilIdle()

            assertThat(HABIT_ONE.isScheduled(localDate)).isTrue()
            assertThat(updatedHabitOne.isScheduled(localDate)).isTrue()
            verify(mockMissionRepository, never()).insert(any())
            verify(mockMissionRepository, never()).delete(listOf(anyOrNull()), any())
            verify(mockAchievementRepository, never()).update(any(), any())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun update_wasNotScheduledAndIsNotScheduled_doesNotInsertDeleteMissionAndUpdateAchievement()= runTest(testDispatcher){
        val localDate = LocalDate.of(2024, 5, 27)
        mockStatic(LocalDate::class.java).use { theMock ->
            theMock.`when`<LocalDate> { LocalDate.now() }.thenReturn(localDate)

            habitEditorViewModel.update(HABIT_ONE, updatedHabitOne)
            advanceUntilIdle()

            assertThat(HABIT_ONE.isScheduled(localDate)).isFalse()
            assertThat(updatedHabitOne.isScheduled(localDate)).isFalse()
            verify(mockMissionRepository, never()).insert(any())
            verify(mockMissionRepository, never()).delete(listOf(anyOrNull()), any())
            verify(mockAchievementRepository, never()).update(any(), any())
        }
    }
}