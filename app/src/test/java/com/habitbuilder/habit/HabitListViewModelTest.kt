package com.habitbuilder.habit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.habitbuilder.TestConstants.Companion.HABIT_ONE
import com.habitbuilder.TestConstants.Companion.HABIT_TWO
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.data.HabitRepository
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever


class HabitListViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val mockHabitRepository: HabitRepository = mock(HabitRepository::class.java)
    private val habitListViewModel = HabitListViewModel(mockHabitRepository)

    @Test
    fun getHabitList_returnsCorrectList(){
        val habitListLiveData:MutableLiveData<List<Habit>> = MutableLiveData(listOf(HABIT_ONE, HABIT_TWO))
        whenever(mockHabitRepository.getOrderedHabitList()).thenReturn(habitListLiveData)
        habitListViewModel.getHabitList().observeForever{}

        assertThat(habitListViewModel.getHabitList().value).containsExactly(HABIT_ONE, HABIT_TWO)
    }

}