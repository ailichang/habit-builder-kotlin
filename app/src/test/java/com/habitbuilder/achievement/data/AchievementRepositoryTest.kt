package com.habitbuilder.achievement.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.habitbuilder.TestConstants.Companion.HABIT_ONE
import com.habitbuilder.TestConstants.Companion.HABIT_ONE_ID
import com.habitbuilder.TestConstants.Companion.HABIT_TWO_ID
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

class AchievementRepositoryTest {

    private val mockAchievementDao = mock(AchievementDao::class.java)

    private val achievementRepository = AchievementRepository(mockAchievementDao)
    private val habitOneAchievement = Achievement(id = 1, habitId = HABIT_ONE_ID, experiencePoints = 10L)
    private val habitTwoAchievement = Achievement(id = 2, habitId = HABIT_TWO_ID, experiencePoints = 20L)
    @Test
    fun insert_callsAchievementDaoInsert() = runTest{
        achievementRepository.insert(habitOneAchievement, habitTwoAchievement)

        verify(mockAchievementDao).insert(habitOneAchievement, habitTwoAchievement)
    }

    @Test
    fun update_increaseType_callsAchievementDaoUpdateWithCorrectExperiencePoints() = runTest{
        achievementRepository.update(HABIT_ONE, ExperienceUpdateType.INCREASE)

        verify(mockAchievementDao).update(HABIT_ONE_ID, HABIT_ONE.experiencePoints)
    }

    @Test
    fun update_decreaseType_callsAchievementDaoUpdateWithCorrectExperiencePoints() = runTest{
        achievementRepository.update(HABIT_ONE, ExperienceUpdateType.DECREASE)

        verify(mockAchievementDao).update(HABIT_ONE_ID, -HABIT_ONE.experiencePoints)
    }

    @Test
    fun update_noneType_callsAchievementDaoUpdateWithCorrectExperiencePoints() = runTest{
        achievementRepository.update(HABIT_ONE, ExperienceUpdateType.NONE)

        verify(mockAchievementDao).update(HABIT_ONE_ID, 0)
    }

    @Test
    fun delete_callsAchievementDaoDeleteByHabitId() = runTest {
        achievementRepository.delete(HABIT_ONE_ID)

        verify(mockAchievementDao).deleteByHabitId(HABIT_ONE_ID)
    }
}