package com.habitbuilder.habit.data

import java.util.UUID


class HabitDifference {
    enum class Type {
        INSERT,
        DELETE
    }

    val differenceType: Type
    val habitId: UUID
    var habit: Habit? = null

    constructor(differenceType: Type, habit: Habit) {
        this.differenceType = differenceType
        this.habit = habit
        habitId = habit.habitId
    }

    constructor(differenceType: Type, habitId: UUID) {
        this.differenceType = differenceType
        this.habitId = habitId
    }

    override fun toString(): String {
        return """
             HabitDiff = {diffType=$differenceType
             habitId=$habitId
             habit=${habit}}
             """.trimIndent()
    }
}

