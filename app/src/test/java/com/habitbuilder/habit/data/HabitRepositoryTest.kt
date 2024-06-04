package com.habitbuilder.habit.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.habitbuilder.TestConstants.Companion.HABIT_ONE
import com.habitbuilder.TestConstants.Companion.HABIT_TWO
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

class HabitRepositoryTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val mockHabitDao:HabitDao = mock(HabitDao::class.java)
    private val habitRepository:HabitRepository = HabitRepository(mockHabitDao)

    @Test
    fun getHabits_returnsCorrectHabits() = runTest{
        whenever(mockHabitDao.getHabits()).thenReturn(listOf(HABIT_ONE, HABIT_TWO))

        assertThat(habitRepository.getHabits()).containsExactly(HABIT_ONE, HABIT_TWO)
    }

    @Test
    fun getOrderedHabitList_returnsCorrectHabitList(){
        val habitListLiveData: MutableLiveData<List<Habit>> = MutableLiveData(listOf(HABIT_ONE, HABIT_TWO))
        whenever(mockHabitDao.getHabitsOrderByPriority()).thenReturn(habitListLiveData)

        habitRepository.getOrderedHabitList().observeForever{}

        assertThat(habitRepository.getOrderedHabitList().value).containsExactly(HABIT_ONE, HABIT_TWO)
    }

    @Test
    fun getScheduledHabits_returnsCorrectScheduledHabitList() = runTest{
        val habitList: List<Habit> = listOf(HABIT_ONE, HABIT_TWO)
        whenever(mockHabitDao.getHabits()).thenReturn(habitList)
        val selectedDate:LocalDate = LocalDate.parse("2024-05-27")

        val scheduledHabits = habitRepository.getScheduledHabits(selectedDate)

        assertThat(HABIT_ONE.isScheduled(selectedDate)).isFalse()
        assertThat(HABIT_TWO.isScheduled(selectedDate)).isTrue()
        assertThat(scheduledHabits).containsExactly(HABIT_TWO)
    }
    @Test
    fun insert_callsHabitDaoInsert() = runTest{
        habitRepository.insert(HABIT_ONE)

        verify(mockHabitDao).insert(HABIT_ONE)
    }

    @Test
    fun update_callsHabitDaoUpdate() = runTest{
        habitRepository.update(HABIT_ONE)

        verify(mockHabitDao).update(HABIT_ONE)
    }

    @Test
    fun delete_callsHabitDaoDelete() = runTest{
        habitRepository.delete(HABIT_ONE)

        verify(mockHabitDao).delete(HABIT_ONE)
    }

}