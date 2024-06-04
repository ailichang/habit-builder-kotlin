package com.habitbuilder.mission.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class TimerTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    @Test
    fun init_setsCorrectValues(){
        val timer = Timer(duration = 100L, elapsedTime = 10L)

        assertThat(timer.duration).isEqualTo(100L)
        assertThat(timer.isTimerInitialized).isFalse()
        assertThat(timer.elapsedTime).isEqualTo(10L)
        assertThat(timer.remainingTime.value).isEqualTo(90L)
    }

    @Test
    fun setTimer_setsCorrectTimeValues(){
        val timer = Timer(duration = 100L)

        timer.setTime(30L)

        assertThat(timer.elapsedTime).isEqualTo(30L)
        assertThat(timer.remainingTime.value).isEqualTo(70L)
    }

    @Test
    fun progress_returnsCorrectValue(){
        val timer = Timer(duration = 100L, elapsedTime = 30L)

        assertThat(timer.progress).isEqualTo(0.3)
    }
    @Test
    fun progressText_returnsCorrectText(){
        val timer = Timer(duration = 100L)
        timer.setTime(30L)

        assertThat(timer.progressText).isEqualTo("30%")
    }
    @Test
    fun remainingText_returnsCorrectText(){
        val timer = Timer(duration = 100L, elapsedTime = 30L)

        assertThat(timer.remainingTime.value).isEqualTo(70L)
        assertThat(timer.remainingTimeText).isEqualTo("01:10")
    }

    @Test
    fun isTargetReached_elapsedTimeIsSmallerThanDuration_returnsFalse(){
        val timer = Timer(duration = 100L, elapsedTime = 30L)

        assertThat(timer.isTargetReached).isFalse()
    }

    @Test
    fun isTargetReached_elapsedTimeIsNotSmallerThanDuration_returnsTrue(){
        val timer = Timer(duration = 100L)
        timer.setTime(100L)

        assertThat(timer.isTargetReached).isTrue()
    }

    @Test
    fun resetTimer_resetsCorrectTimeValues(){
        val timer = Timer(duration = 100L, 50L)

        timer.reset()

        assertThat(timer.elapsedTime).isEqualTo(0)
        assertThat(timer.remainingTime.value).isEqualTo(100L)
    }

    @Test
    fun start_setsIsTimerInitializedTrue(){
        val timer = Timer(duration = 100L)
        timer.start()

        assertThat(timer.isTimerInitialized).isTrue()
    }

    @Test
    fun cancel_resetsCorrectValues(){
        val timer = Timer(duration = 100L)
        timer.start()

        timer.cancel()

        assertThat(timer.isTimerInitialized).isFalse()
        assertThat(timer.isTimerFinished).isFalse()
        assertThat(timer.elapsedTime).isEqualTo(0)
        assertThat(timer.remainingTime.value).isEqualTo(100L)
    }
}