package com.habitbuilder.habit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.data.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    fun getHabitList(): LiveData<List<Habit>> {
        return habitRepository.getOrderedHabitList()
    }
}
