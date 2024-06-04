package com.habitbuilder

import com.habitbuilder.habit.data.Category
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.data.Priority
import com.habitbuilder.habit.data.Type
import com.habitbuilder.mission.data.Counter
import com.habitbuilder.mission.data.Mission
import com.habitbuilder.mission.data.MissionDetail
import com.habitbuilder.mission.data.Timer
import java.util.UUID

class TestConstants {
    companion object{
        val HABIT_ONE_ID: UUID = UUID.randomUUID()
        val HABIT_ONE = Habit(HABIT_ONE_ID, Type.TIMER, Category.FINANCE, Priority.HIGH,
            title = "Testing", description = "description", location = "location", startTime = "13:00", endTime = "15:00",
            targetDuration = 100L, createdDate = "2024-05-30", scheduledDays = arrayListOf(false, false, false, false, true, true, true)
        )
        val HABIT_TWO_ID: UUID = UUID.randomUUID()
        val HABIT_TWO = Habit(HABIT_TWO_ID, Type.COUNTER, Category.FITNESS, Priority.LOW,
            title = "Drink water", description = "description 2", location = "location", startTime = "01:00", endTime = "02:00",
            targetCount = 10, createdDate = "2024-05-29", scheduledDays = arrayListOf(true, false, true, false, true, false, true)
        )
        val HABIT_THREE_ID: UUID = UUID.randomUUID()
        val HABIT_THREE = Habit(HABIT_THREE_ID, Type.COUNTER, Category.FITNESS, Priority.MEDIUM,
            title = "Habit 3", description = "description 3", location = "location", startTime = "01:00", endTime = "02:00",
            targetCount = 5, createdDate = "2024-05-29", scheduledDays = arrayListOf(true, false, true, false, true, false, true)
        )
        val INCOMPLETE_MISSION_HABIT_ONE_ID:UUID = UUID.randomUUID()
        val INCOMPLETE_MISSION_HABIT_ONE = Mission(missionId = INCOMPLETE_MISSION_HABIT_ONE_ID, habitId = HABIT_ONE_ID, year= 2024, month = 5, day=31, timer = Timer(100L))
        val INCOMPLETE_MISSION_DETAIL_HABIT_ONE = MissionDetail(INCOMPLETE_MISSION_HABIT_ONE, HABIT_ONE)
        val COMPLETED_MISSION_HABIT_ONE_ID:UUID = UUID.randomUUID()
        val COMPLETED_MISSION_HABIT_ONE = Mission(missionId = COMPLETED_MISSION_HABIT_ONE_ID, habitId = HABIT_ONE_ID, year= 2024, month =6, day=2, isCompleted = true, timer = Timer(100L))
        val COMPLETED_MISSION_DETAIL_HABIT_ONE = MissionDetail(COMPLETED_MISSION_HABIT_ONE, HABIT_ONE)
        val INCOMPLETE_MISSION_HABIT_TWO_ID:UUID = UUID.randomUUID()
        val INCOMPLETE_MISSION_HABIT_TWO = Mission(missionId = INCOMPLETE_MISSION_HABIT_TWO_ID, habitId = HABIT_TWO_ID, year= 2024, month = 6, day=2, counter = Counter(10))
        val INCOMPLETE_MISSION_DETAIL_HABIT_TWO = MissionDetail(INCOMPLETE_MISSION_HABIT_TWO, HABIT_TWO)
        val COMPLETED_MISSION_HABIT_TWO_ID:UUID = UUID.randomUUID()
        val COMPLETED_MISSION_HABIT_TWO = Mission(missionId = COMPLETED_MISSION_HABIT_TWO_ID, habitId = HABIT_TWO_ID, year= 2024, month = 5, day=31, isCompleted = true, counter = Counter(10))
        val COMPLETED_MISSION_DETAIL_HABIT_TWO = MissionDetail(COMPLETED_MISSION_HABIT_TWO, HABIT_TWO)
        val INCOMPLETE_MISSION_HABIT_THREE_ID:UUID = UUID.randomUUID()
        val INCOMPLETE_MISSION_HABIT_THREE = Mission(missionId = INCOMPLETE_MISSION_HABIT_THREE_ID, habitId = HABIT_THREE_ID, year= 2024, month = 5, day=31, timer = Timer(100L))

    }
}