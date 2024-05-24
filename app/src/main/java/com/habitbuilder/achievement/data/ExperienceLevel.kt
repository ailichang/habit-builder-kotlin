package com.habitbuilder.achievement.data

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class ExperienceLevel {
    companion object{
        private const val MIN_LEVEL = 1
        private const val MAX_LEVEL = 10
        private const val MIN_EXPERIENCE_POINTS: Long = 0
        private const val MAX_EXPERIENCE_POINTS: Long = 10000000
        private const val COEFFICIENT = 1000
        private const val POWER = 1.5

        fun getLevel(experiencePoints: Long): Level {
            val level = binarySearch(MIN_LEVEL, MAX_LEVEL, experiencePoints)
            return Level(
                level.toLong(),
                getLevelLowerThreshold(level),
                getLevelHigherThreshold(level)
            )
        }

        private fun getLevelLowerThreshold(level: Int): Long {
            return if (level <= MIN_LEVEL) {
                MIN_EXPERIENCE_POINTS
            } else (COEFFICIENT * min(MAX_LEVEL - 1, level - 1).toDouble().pow(POWER)).toLong()
        }

        private fun getLevelHigherThreshold(level: Int): Long {
            return if (level >= MAX_LEVEL) {
                MAX_EXPERIENCE_POINTS
            } else (COEFFICIENT * max(MIN_LEVEL, level).toDouble().pow(POWER)).toLong()
        }

        private fun binarySearch(low: Int, high: Int, targetExp: Long): Int {
            if (targetExp <= getLevelLowerThreshold(low)) {
                return low
            } else if (targetExp >= getLevelHigherThreshold(high)) {
                return high
            }
            val mid = low + ((high - low) / 2f).toInt()
            val midExp = getLevelLowerThreshold(mid)
            if (midExp <= targetExp && targetExp < getLevelHigherThreshold(mid)) {
                return mid
            }
            return if (targetExp > midExp) {
                binarySearch(mid + 1, high, targetExp)
            } else binarySearch(low, mid - 1, targetExp)
        }
    }
}