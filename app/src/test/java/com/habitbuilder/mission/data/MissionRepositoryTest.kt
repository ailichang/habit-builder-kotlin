package com.habitbuilder.mission.data

import com.google.common.truth.Truth.assertThat
import com.habitbuilder.TestConstants.Companion.HABIT_ONE
import com.habitbuilder.TestConstants.Companion.HABIT_ONE_ID
import com.habitbuilder.TestConstants.Companion.HABIT_TWO_ID
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_DETAIL_HABIT_ONE
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_DETAIL_HABIT_TWO
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_HABIT_ONE
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_HABIT_TWO
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

class MissionRepositoryTest {

    private val mockMissionDao = mock(MissionDao::class.java)
    private val missionRepository = MissionRepository(mockMissionDao)

    @Test
    fun insert_callsMissionDaoInsert() = runTest {
        missionRepository.insert(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO)

        verify(mockMissionDao).insert(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO)
    }

    @Test
    fun update_callsMissionDaoUpdate() = runTest {
        missionRepository.update(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO)

        verify(mockMissionDao).update(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO)
    }

    @Test
    fun updateCondition_callsMissionDaoUpdateCondition() = runTest{
        val localDate = LocalDate.parse("2024-05-31")

        missionRepository.updateCondition(HABIT_ONE_ID, localDate, targetCount = HABIT_ONE.targetCount, duration = HABIT_ONE.targetDuration)

        verify(mockMissionDao).updateCondition(HABIT_ONE_ID, localDate.year, localDate.monthValue, localDate.dayOfMonth, HABIT_ONE.targetCount,  HABIT_ONE.targetDuration)
    }
    @Test
    fun delete_withMissions_callsMissionDaoDelete() = runTest {
        missionRepository.delete(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO)

        verify(mockMissionDao).delete(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO)
    }

    @Test
    fun delete_withHabitIdsAndLocalDate_callsMissionDaoDelete() = runTest {
        val localDate = LocalDate.parse("2024-05-31")
        val habitIds = listOf(HABIT_ONE_ID, HABIT_TWO_ID)

        missionRepository.delete(habitIds, localDate)

        verify(mockMissionDao).delete(habitIds, localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }

    @Test
    fun isDailyMissionCompleted_callsMissionDaoIsDailyMissionCompleted() = runTest{
        val localDate = LocalDate.parse("2024-05-31")

        missionRepository.isDailyMissionCompleted(HABIT_ONE_ID, localDate)

        verify(mockMissionDao).isDailyMissionCompleted(HABIT_ONE_ID, localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }

    @Test
    fun  getDailyMissions_callsMissionDaoGetDailyMissions() = runTest{
        val localDate = LocalDate.parse("2024-05-31")

        missionRepository.getDailyMissions(localDate)

        verify(mockMissionDao).getDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }
    @Test
    fun getDailyMissionHabitIds_callsMissionDaoGetDailyMissionsAndMapsToCorrectHabitIds() = runTest{
        val localDate = LocalDate.parse("2024-05-31")
        whenever(mockMissionDao.getDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth))
            .thenReturn(listOf(INCOMPLETE_MISSION_DETAIL_HABIT_ONE, INCOMPLETE_MISSION_DETAIL_HABIT_TWO))


        assertThat(missionRepository.getDailyMissionHabitIds(localDate)).containsExactly(HABIT_ONE_ID, HABIT_TWO_ID).inOrder()
    }

    @Test
    fun getMissions_callsMissionDaoGetMissions() = runTest{
        missionRepository.getMissions()

        verify(mockMissionDao).getMissions()
    }

    @Test
    fun getIncompleteDailyMissions_callsMissionDaoGetIncompleteDailyMissions() {
        val localDate = LocalDate.parse("2024-05-31")

        missionRepository.getIncompleteDailyMissions(localDate)

        verify(mockMissionDao).getIncompleteDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }

    @Test
    fun getCompletedDailyMissions_callsMissionDaoGetCompletedDailyMissions()  {
        val localDate = LocalDate.parse("2024-05-31")

        missionRepository.getCompletedDailyMissions(localDate)

        verify(mockMissionDao).getCompletedDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }

    @Test
    fun getMonthlyMissions_callsMissionDaoGetMonthlyMissions() = runTest{
        missionRepository.getMonthlyMissions(2024, 1)

        verify(mockMissionDao).getMonthlyMissions(2024, 1)
    }
}