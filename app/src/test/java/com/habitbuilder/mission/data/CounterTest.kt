package com.habitbuilder.mission.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class CounterTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun constructor_setsCorrectValues(){
        val counter = Counter(targetCount = 10)

        assertThat(counter.targetCount).isEqualTo(10)
        assertThat(counter.currentCount).isEqualTo(0)
        assertThat(counter.currentCountLiveData.value).isEqualTo(0)
    }
    @Test
    fun setCount_setsCorrectValues(){
        val counter = Counter(targetCount = 10)

        counter.setCount(5)

        assertThat(counter.currentCount).isEqualTo(5)
        assertThat(counter.currentCountLiveData.value).isEqualTo(5)
    }
    @Test
    fun add_notReachedTargetCount_setsCorrectValues(){
        val counter = Counter(targetCount = 10, currentCount = 5)

        counter.add()

        assertThat(counter.currentCount).isEqualTo(6)
        assertThat(counter.currentCountLiveData.value).isEqualTo(6)
    }

    @Test
    fun add_reachedTargetCount_setsCorrectValues(){
        val counter = Counter(targetCount = 10, currentCount = 10)

        counter.add()

        assertThat(counter.currentCount).isEqualTo(10)
        assertThat(counter.currentCountLiveData.value).isEqualTo(10)
    }
    @Test
    fun minus_currentCountIsNotZero_setsCorrectValue(){
        val counter = Counter(targetCount = 10, currentCount = 5)

        counter.minus()

        assertThat(counter.currentCount).isEqualTo(4)
        assertThat(counter.currentCountLiveData.value).isEqualTo(4)
    }

    @Test
    fun minus_currentCountIsZero_setsCorrectValue(){
        val counter = Counter(targetCount = 10, currentCount = 0)

        counter.minus()

        assertThat(counter.currentCount).isEqualTo(0)
        assertThat(counter.currentCountLiveData.value).isEqualTo(0)
    }

    @Test
    fun reset_setsValuesToZeros(){
        val counter = Counter(targetCount = 10, currentCount = 5)

        counter.reset()

        assertThat(counter.currentCount).isEqualTo(0)
        assertThat(counter.currentCountLiveData.value).isEqualTo(0)
    }

    @Test
    fun isTargetReached_currentCountLesserThanTargetCount_returnsFalse(){
        val counter = Counter(targetCount = 10, currentCount = 5)

        assertThat(counter.isTargetReached).isFalse()
    }

    @Test
    fun isTargetReached_currentCountReachedTargetCount_returnsTrue(){
        val counter = Counter(targetCount = 10, currentCount = 10)

        assertThat(counter.isTargetReached).isTrue()
    }

    @Test
    fun progressText_currentCountLesserThanTargetCount_returnsCorrectText(){
        val counter = Counter(targetCount = 10, currentCount = 5)

        assertThat(counter.progressText).isEqualTo("5/10")
    }

    @Test
    fun progressText_currentCountReachedTargetCount_returnsCorrectText(){
        val counter = Counter(targetCount = 10, currentCount = 10)

        assertThat(counter.progressText).isEqualTo("10/10")
    }
}