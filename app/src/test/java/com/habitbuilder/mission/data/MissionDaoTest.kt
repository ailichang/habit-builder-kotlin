package com.habitbuilder.mission.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.habitbuilder.AppDatabase
import com.habitbuilder.TestConstants.Companion.COMPLETED_MISSION_DETAIL_HABIT_ONE
import com.habitbuilder.TestConstants.Companion.COMPLETED_MISSION_HABIT_ONE
import com.habitbuilder.TestConstants.Companion.COMPLETED_MISSION_HABIT_TWO
import com.habitbuilder.TestConstants.Companion.HABIT_ONE
import com.habitbuilder.TestConstants.Companion.HABIT_ONE_ID
import com.habitbuilder.TestConstants.Companion.HABIT_THREE
import com.habitbuilder.TestConstants.Companion.HABIT_TWO
import com.habitbuilder.TestConstants.Companion.HABIT_TWO_ID
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_DETAIL_HABIT_ONE
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_HABIT_ONE
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_HABIT_ONE_ID
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_HABIT_TWO
import com.habitbuilder.TestConstants.Companion.INCOMPLETE_MISSION_HABIT_TWO_ID
import com.habitbuilder.habit.data.HabitDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class MissionDaoTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private lateinit var database: AppDatabase
    private lateinit var missionDao:MissionDao
    private lateinit var habitDao: HabitDao

    @Before
    fun createDatabase() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        missionDao = database.getMissionDao()
        habitDao = database.getHabitDao()
        habitDao.insert(HABIT_ONE, HABIT_TWO, HABIT_THREE)
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        database.close()
    }
    @Test
    fun insert_insertsCorrectHabits() = runTest {
        val previousMissionList = missionDao.getMissions()

        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO))

        assertThat(previousMissionList).isEmpty()
        assertThat(missionDao.getMissions()).containsExactly(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO)
    }

    @Test
    fun update_updatesCorrectHabits() = runTest {
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO))
        val updatedMissionOne = Mission(missionId = INCOMPLETE_MISSION_HABIT_ONE_ID, habitId = HABIT_ONE_ID, year= 2024, month = 5, day=31, timer = Timer(90L))
        val updatedMissionTwo = Mission(missionId = INCOMPLETE_MISSION_HABIT_TWO_ID, habitId = HABIT_TWO_ID, year= 2024, month = 5, day=31, counter = Counter(30))
        val previousMissionList = missionDao.getMissions()

        missionDao.update(updatedMissionOne, updatedMissionTwo)

        assertThat(previousMissionList).containsExactly(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO)
        assertThat(missionDao.getMissions()).containsExactly(updatedMissionOne, updatedMissionTwo)
    }

    @Test
    fun updateCondition_updatesCorrectHabit() = runTest{
        val localDate = LocalDate.parse("2024-05-31")
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO))
        val updatedMissionOne = Mission(missionId = INCOMPLETE_MISSION_HABIT_ONE_ID, habitId = HABIT_ONE_ID, year= 2024, month = 5, day=31, timer = Timer(0L), counter = Counter(15))
        val previousMissionList = missionDao.getMissions()

        missionDao.updateCondition(HABIT_ONE_ID, localDate.year, localDate.monthValue, localDate.dayOfMonth, targetCount = 15, duration = 0L)

        assertThat(previousMissionList).containsExactly(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO)
        assertThat(missionDao.getMissions()).containsExactly(updatedMissionOne, INCOMPLETE_MISSION_HABIT_TWO)
    }
    @Test
    fun delete_withMissions_deletesCorrectHabit() = runTest {
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO))
        val previousMissionList = missionDao.getMissions()

        missionDao.delete(INCOMPLETE_MISSION_HABIT_ONE)

        assertThat(previousMissionList).containsExactly(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO)
        assertThat(missionDao.getMissions()).containsExactly(INCOMPLETE_MISSION_HABIT_TWO)
    }

    @Test
    fun delete_withHabitIdsAndLocalDate_deletesCorrectHabits() = runTest {
        val localDate = LocalDate.parse("2024-05-31")
        val habitIds = listOf(HABIT_ONE_ID, HABIT_TWO_ID)
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))
        val previousMissionList = missionDao.getMissions()

        missionDao.delete(habitIds, localDate.year, localDate.monthValue, localDate.dayOfMonth)

        assertThat(previousMissionList).containsExactly(INCOMPLETE_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO)
        assertThat(missionDao.getMissions()).isEmpty()
    }

    @Test
    fun isDailyMissionCompleted_completedMission_returnsTrue() = runTest{
        val localDate = LocalDate.parse("2024-06-02")
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))

        assertThat(missionDao.isDailyMissionCompleted(HABIT_ONE_ID, localDate.year, localDate.monthValue, localDate.dayOfMonth)).isTrue()
    }

    @Test
    fun isDailyMissionCompleted_incompleteMission_returnsFalse() = runTest{
        val localDate = LocalDate.parse("2024-05-31")
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))

        assertThat(missionDao.isDailyMissionCompleted(HABIT_ONE_ID, localDate.year, localDate.monthValue, localDate.dayOfMonth)).isFalse()
    }

    @Test
    fun isDailyMissionCompleted_noMissionOnThatDate_returnsNull() = runTest{
        val localDate = LocalDate.parse("2024-05-30")
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))

        assertThat(missionDao.isDailyMissionCompleted(HABIT_ONE_ID, localDate.year, localDate.monthValue, localDate.dayOfMonth)).isNull()
    }

    @Test
    fun  getDailyMissions_missionExistsOnThatDate_returnsCorrectMissionDetails() = runTest{
        val localDate = LocalDate.parse("2024-06-02")
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))

        assertThat(missionDao.getDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)).containsExactly(COMPLETED_MISSION_DETAIL_HABIT_ONE)
    }

    @Test
    fun  getDailyMissions_noMissionOnThatDate_returnsEmptyList() = runTest{
        val localDate = LocalDate.parse("2024-05-30")
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))

        assertThat(missionDao.getDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)).isEmpty()
    }

    @Test
    fun getMissions_returnsCorrectMissions() = runTest{
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))

        assertThat(missionDao.getMissions()).containsExactly(INCOMPLETE_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO)
    }

    @Test
    fun getMissions_emptyMissions_returnsEmptyList() = runTest{
        assertThat(missionDao.getMissions()).isEmpty()
    }
    @Test
    fun getIncompleteDailyMissions_returnsIncompleteDailyMissionDetails() = runTest{
        val localDate = LocalDate.parse("2024-05-31")
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))

        val incompleteDailyListLiveData = missionDao.getIncompleteDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)
        incompleteDailyListLiveData.observeForever { }

        assertThat(incompleteDailyListLiveData.value).containsExactly(INCOMPLETE_MISSION_DETAIL_HABIT_ONE)
    }

    @Test
    fun getIncompleteDailyMissions_emptyMissionsOnThatDate_returnsEmptyList() = runTest{
        val localDate = LocalDate.parse("2024-05-30")
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))

        val incompleteDailyListLiveData = missionDao.getIncompleteDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)
        incompleteDailyListLiveData.observeForever { }

        assertThat(incompleteDailyListLiveData.value).isEmpty()
    }

    @Test
    fun getCompletedDailyMissions_returnsCompletedDailyMissionDetails() = runTest {
        val localDate = LocalDate.parse("2024-06-02")
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))

        val completeDailyListLiveData = missionDao.getCompletedDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)
        completeDailyListLiveData.observeForever { }

        assertThat(completeDailyListLiveData.value).containsExactly(COMPLETED_MISSION_DETAIL_HABIT_ONE)
    }

    @Test
    fun getCompletedDailyMissions_emptyMissionsOnThatDate_returnsEmptyList() = runTest{
        val localDate = LocalDate.parse("2024-05-30")
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))

        val completeDailyListLiveData = missionDao.getCompletedDailyMissions(localDate.year, localDate.monthValue, localDate.dayOfMonth)
        completeDailyListLiveData.observeForever { }

        assertThat(completeDailyListLiveData.value).isEmpty()
    }
    @Test
    fun getMonthlyMissions_returnsCorrectMonthlyMissions() = runTest{
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))

        val monthlyMission = missionDao.getMonthlyMissions(2024, 5)

        assertThat(monthlyMission).containsExactlyEntriesIn(
            mapOf(HABIT_ONE to listOf(INCOMPLETE_MISSION_HABIT_ONE), HABIT_TWO to listOf(COMPLETED_MISSION_HABIT_TWO)))
    }

    @Test
    fun getMonthlyMissions_noMissionsOnThatMonth_returnsEmptyMap() = runTest{
        missionDao.insert(*arrayOf(INCOMPLETE_MISSION_HABIT_ONE, INCOMPLETE_MISSION_HABIT_TWO, COMPLETED_MISSION_HABIT_ONE, COMPLETED_MISSION_HABIT_TWO))

        val monthlyMission = missionDao.getMonthlyMissions(2024, 7)

        assertThat(monthlyMission).isEmpty()
    }
}