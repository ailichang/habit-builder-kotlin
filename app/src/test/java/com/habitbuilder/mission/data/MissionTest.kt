package com.habitbuilder.mission.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.habitbuilder.TestConstants.Companion.HABIT_ONE
import com.habitbuilder.TestConstants.Companion.HABIT_TWO
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.time.LocalDate

class MissionTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    @Test
    fun constructor_counterType_setsCounter(){
        val counterMission = Mission(HABIT_TWO, LocalDate.now())

        assertThat(counterMission.timer).isNull()
        assertThat(counterMission.counter).isEqualTo(Counter(HABIT_TWO.targetCount))
    }

    @Test
    fun newMission_timerType_setsTimer(){
        val timerMission = Mission(HABIT_ONE, LocalDate.now())

        assertThat(timerMission.counter).isNull()
        assertThat(timerMission.timer).isEqualTo(Timer(HABIT_ONE.targetDuration))
    }

    @Test
    fun setMissionCompleted_timerTypeAndIsCompleted_setsIsCompletedAndRemainingTimeToZero(){
        val timerMission = Mission(HABIT_ONE, LocalDate.now())
        timerMission.timer?.elapsedTime = 10L

        timerMission.setMissionCompleted(true)

        assertThat(timerMission.isCompleted).isTrue()
        assertThat(timerMission.timer?.elapsedTime).isEqualTo(HABIT_ONE.targetDuration)
        assertThat(timerMission.timer?.remainingTime?.value).isEqualTo(0)
    }

    @Test
    fun setMissionCompleted_timerTypeAndIsNotCompleted_setsIsNotCompletedAndRemainingTimeToTargetDuration(){
        val timerMission = Mission(HABIT_ONE, LocalDate.now())
        timerMission.timer?.elapsedTime = 10L

        timerMission.setMissionCompleted(false)

        assertThat(timerMission.isCompleted).isFalse()
        assertThat(timerMission.timer?.elapsedTime).isEqualTo(0)
        assertThat(timerMission.timer?.remainingTime?.value).isEqualTo(HABIT_ONE.targetDuration)
    }

    @Test
    fun setMissionCompleted_counterTypeAndIsCompleted_setsIsCompletedAndCurrentCountToTargetCount(){
        val counterMission = Mission(HABIT_TWO, LocalDate.now())
        counterMission.counter?.setCount(5)

        counterMission.setMissionCompleted(true)

        assertThat(counterMission.isCompleted).isTrue()
        assertThat(counterMission.counter?.currentCount).isEqualTo(HABIT_TWO.targetCount)
        assertThat(counterMission.counter?.currentCountLiveData?.value).isEqualTo(HABIT_TWO.targetCount)
    }

    @Test
    fun setMissionCompleted_counterTypeAndIsNotCompleted_setsIsNotCompletedAndCurrentCountToZero(){
        val counterMission = Mission(HABIT_TWO, LocalDate.now())
        counterMission.counter?.setCount(5)

        counterMission.setMissionCompleted(false)

        assertThat(counterMission.isCompleted).isFalse()
        assertThat(counterMission.counter?.currentCount).isEqualTo(0)
        assertThat(counterMission.counter?.currentCountLiveData?.value).isEqualTo(0)
    }
}