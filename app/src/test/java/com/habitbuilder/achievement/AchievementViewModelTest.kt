package com.habitbuilder.achievement

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.habitbuilder.TestConstants.Companion.HABIT_ONE
import com.habitbuilder.TestConstants.Companion.HABIT_ONE_ID
import com.habitbuilder.TestConstants.Companion.HABIT_TWO
import com.habitbuilder.TestConstants.Companion.HABIT_TWO_ID
import com.habitbuilder.achievement.data.Achievement
import com.habitbuilder.achievement.data.AchievementDetail
import com.habitbuilder.achievement.data.AchievementRepository
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.UUID

class AchievementViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val mockAchievementRepository: AchievementRepository = mock(AchievementRepository::class.java)
    private val achievementViewModel = AchievementViewModel(mockAchievementRepository)
    @Test
    fun getAchievementDetails_returnsCorrectAchievementDetails(){
        val habitOneAchievementDetail = AchievementDetail(Achievement(id = 1, habitId = HABIT_ONE_ID, experiencePoints = 10L), habit = HABIT_ONE)
        val habitTwoAchievementDetail = AchievementDetail(Achievement(id = 2, habitId = HABIT_TWO_ID, experiencePoints = 20L), habit = HABIT_TWO)
        whenever(mockAchievementRepository.achievementDetails).thenReturn(MutableLiveData(listOf(habitOneAchievementDetail, habitTwoAchievementDetail)))

        val achievementDetailListLiveData = achievementViewModel.getAchievementDetails()
        achievementDetailListLiveData.observeForever {  }

        assertThat(achievementDetailListLiveData.value).containsExactly(habitOneAchievementDetail, habitTwoAchievementDetail)
    }
}