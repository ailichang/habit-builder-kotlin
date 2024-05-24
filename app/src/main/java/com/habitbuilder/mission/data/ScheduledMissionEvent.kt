package com.habitbuilder.mission.data

import java.time.LocalDate

data class ScheduledMissionEvent(private val missionList: List<Mission>, val missionDate:LocalDate) {

    var hasBeenHandled:Boolean = false
        private set

    fun getMissionListIfNotHandled(): List<Mission>?{
        return if(hasBeenHandled){
            null
        } else {
            hasBeenHandled = true
            missionList
        }
    }

}