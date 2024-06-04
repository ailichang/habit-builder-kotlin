package com.habitbuilder.habit.data

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
class HabitDaoTest {

    @get:Rule
    var rule:TestRule = InstantTaskExecutorRule()
    private lateinit var database: AppDatabase
    private lateinit var habitDao: HabitDao

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        habitDao = database.getHabitDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        database.close()
    }

    @Test
    fun insertHabit_returnsCorrectHabitList() = runTest{
        val previousHabitList = habitDao.getHabits()

        habitDao.insert(*arrayOf(HABIT_ONE, HABIT_TWO))

        assertThat(previousHabitList).isEmpty()
        assertThat(habitDao.getHabits()).containsExactly(HABIT_ONE, HABIT_TWO)
    }

    @Test
    fun deleteHabit_returnsCorrectHabitList() = runTest{
        habitDao.insert(*arrayOf(HABIT_ONE, HABIT_TWO))
        val previousHabitList = habitDao.getHabits()

        habitDao.delete(HABIT_ONE)

        assertThat(previousHabitList).containsExactly(HABIT_ONE, HABIT_TWO)
        assertThat(habitDao.getHabits()).containsExactly(HABIT_TWO)
    }

    @Test
    fun updateHabit_returnsCorrectHabitList() = runTest{
        habitDao.insert(*arrayOf(HABIT_ONE))
        val previousHabitList = habitDao.getHabits()
        val updatedHabit = Habit(HABIT_ONE_ID, Type.TIMER, Category.FINANCE, Priority.MEDIUM,
            title = "Testing", description = "description...", location = "location", startTime = "13:00", endTime = "15:00",
            targetDuration = 90L, createdDate = "2024-05-29", scheduledDays = arrayListOf(false, false, false, false, false, true, true))


        habitDao.update(updatedHabit)

        assertThat(previousHabitList).contains(HABIT_ONE)
        assertThat(habitDao.getHabits()).containsExactly(updatedHabit)
    }

    @Test
    fun getHabitsOrderByPriority_returnsCorrectOrderedHabitList() = runTest{
        habitDao.insert(*arrayOf(HABIT_TWO, HABIT_ONE))

        val habitListLiveData = habitDao.getHabitsOrderByPriority()
        habitListLiveData.observeForever{}

        assertThat(habitListLiveData.value).containsExactly(HABIT_ONE, HABIT_TWO).inOrder()
    }

}