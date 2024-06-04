package com.habitbuilder.achievement.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ExperienceLevelTest {

    @Test
    fun getLevel_returnsCorrectLevels(){
        assertThat(ExperienceLevel.getLevel(0L)).isEqualTo(Level(1, 0, 1000))
        assertThat(ExperienceLevel.getLevel(1L)).isEqualTo(Level(1, 0, 1000))
        assertThat(ExperienceLevel.getLevel(600L)).isEqualTo(Level(1, 0, 1000))
        assertThat(ExperienceLevel.getLevel(1000L)).isEqualTo(Level(2, 1000, 2828))
        assertThat(ExperienceLevel.getLevel(1200L)).isEqualTo(Level(2, 1000, 2828))
        assertThat(ExperienceLevel.getLevel(2828L)).isEqualTo(Level(3, 2828, 5196))
        assertThat(ExperienceLevel.getLevel(5196L)).isEqualTo(Level(4, 5196, 8000))
        assertThat(ExperienceLevel.getLevel(8000L)).isEqualTo(Level(5, 8000, 11180))
        assertThat(ExperienceLevel.getLevel(11180L)).isEqualTo(Level(6, 11180, 14696))
        assertThat(ExperienceLevel.getLevel(14696)).isEqualTo(Level(7, 14696, 18520))
        assertThat(ExperienceLevel.getLevel(18520L)).isEqualTo(Level(8, 18520, 22627))
        assertThat(ExperienceLevel.getLevel(22627L)).isEqualTo(Level(9, 22627, 27000))
        assertThat(ExperienceLevel.getLevel(27000L)).isEqualTo(Level(10, 27000, 10000000))
        assertThat(ExperienceLevel.getLevel(27000L)).isEqualTo(Level(10, 27000, 10000000))
        assertThat(ExperienceLevel.getLevel(10000000L)).isEqualTo(Level(10, 27000, 10000000))
        assertThat(ExperienceLevel.getLevel(10000001L)).isEqualTo(Level(10, 27000, 10000000))
    }
}