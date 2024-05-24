package com.habitbuilder.mission.data

import java.time.LocalDate

data class SearchDailyMissionsEvent(val missionList: List<Mission>, val scheduledDate: LocalDate)
