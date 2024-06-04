package com.habitbuilder.achievement.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.habitbuilder.AppDatabase
import com.habitbuilder.TestConstants.Companion.HABIT_ONE
import com.habitbuilder.TestConstants.Companion.HABIT_ONE_ID
import com.habitbuilder.TestConstants.Companion.HABIT_TWO
import com.habitbuilder.TestConstants.Companion.HABIT_TWO_ID
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

@RunWith(RobolectricTestRunner::class)
class AchievementDaoTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private lateinit var database: AppDatabase
    private lateinit var achievementDao: AchievementDao
    private lateinit var habitDao: HabitDao
    private val habitOneAchievement = Achievement(id = 1, habitId = HABIT_ONE_ID, experiencePoints = 10L)
    private val habitTwoAchievement = Achievement(id = 2, habitId = HABIT_TWO_ID, experiencePoints = 20L)
    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        achievementDao = database.getAchievementDao()
        habitDao = database.getHabitDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        database.close()
    }

    @Test
    fun getAchievementHabitIds_returnsCorrectHabitIds() = runTest{
        habitDao.insert(HABIT_ONE, HABIT_TWO)
        achievementDao.insert(habitOneAchievement, habitTwoAchievement)

        val achievementHabitIds = achievementDao.getAchievementHabitIds()
        achievementHabitIds.observeForever {  }

        assertThat(achievementHabitIds.value).containsExactly(HABIT_ONE_ID, HABIT_TWO_ID)
    }

    @Test
    fun insert_returnsCorrectAchievementDetails() = runTest{
        habitDao.insert(HABIT_ONE, HABIT_TWO)
        val achievementDetails = achievementDao.getAchievementDetails()
        achievementDetails.observeForever {  }

        achievementDao.insert(habitOneAchievement, habitTwoAchievement)

        assertThat(achievementDetails.value).containsExactly(
            AchievementDetail(habitOneAchievement, HABIT_ONE), AchievementDetail(habitTwoAchievement, HABIT_TWO)
        )
    }

    @Test
    fun update_returnsCorrectAchievementDetails() = runTest{
        habitDao.insert(HABIT_ONE, HABIT_TWO)
        val achievementDetails = achievementDao.getAchievementDetails()
        achievementDetails.observeForever { }
        achievementDao.insert(habitOneAchievement)
        val previousAchievement = achievementDetails.value

        achievementDao.update(HABIT_ONE_ID, 10L)

        assertThat(previousAchievement).containsExactly(AchievementDetail(Achievement(1, HABIT_ONE_ID, 10L), HABIT_ONE))
        assertThat(achievementDetails.value).containsExactly(AchievementDetail(Achievement(1, HABIT_ONE_ID, 20L), HABIT_ONE))
    }

    @Test
    fun delete_returnsCorrectAchievementDetails() = runTest{
        habitDao.insert(HABIT_ONE, HABIT_TWO)
        val achievementDetails = achievementDao.getAchievementDetails()
        achievementDetails.observeForever { }
        achievementDao.insert(habitOneAchievement)
        val previousAchievement = achievementDetails.value

        achievementDao.deleteByHabitId(HABIT_ONE_ID)

        assertThat(previousAchievement).containsExactly(AchievementDetail(habitOneAchievement, HABIT_ONE))
        assertThat(achievementDetails.value).isEmpty()
    }

}