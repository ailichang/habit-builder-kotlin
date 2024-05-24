package com.habitbuilder

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.habitbuilder.habit.data.HabitDao
import org.junit.After
import org.junit.Before

class HabitRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var habitDao: HabitDao


    @Before
    fun setUpDatabase(){
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        habitDao = database.getHabitDao()
    }

    @After
    fun closeDatabase(){
        database.close()
    }
//    @Test
//    fun insertHabit_returnsTrue() {
//        val habit = Habit(
//            UUID.fromString("1"),
//            Type.TIMER,
//            Category.FINANCE,
//            Priority.HIGH,
//            Frequency.EVERYDAY,
//            "title",
//            "description",
//            "location",
//            "0:00",
//            "1:00",
//            5L,
//            10L,
//            0,
//            "2023-10-20",
//            false,
//            "2023-10-20",
//            arrayListOf(false)
//        )
//
//        habitDao.insertAll(habit)
//    }

}