package com.habitbuilder.mission

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.habitbuilder.R
import com.habitbuilder.habit.data.Type
import com.habitbuilder.mission.data.MissionDetail
import com.habitbuilder.util.WidgetFormatter.Companion.setPriorityTag
import com.habitbuilder.util.WidgetFormatter.Companion.setTime
import com.google.android.material.chip.Chip

class MissionViewHolder(
    itemView: View,
    private val missionClickedCallback: MissionClickedCallback
): ViewHolder(itemView) {
    interface MissionClickedCallback {
        fun onMissionClicked(missionDetail:MissionDetail)
    }
    private val title: TextView
    private val progress: TextView
    private val time: TextView
    private val progressBar: ProgressBar
    private val priorityTag: Chip

    companion object {
        const val DIALOG_MISSION_DETAIL_TAG = "dialog_mission_detail"
    }
    init {
        title = itemView.findViewById(R.id.mission_title)
        priorityTag = itemView.findViewById(R.id.mission_priority)
        time = itemView.findViewById(R.id.mission_time)
        progressBar = itemView.findViewById(R.id.mission_progressbar)
        progress = itemView.findViewById(R.id.mission_progressbar_text)
    }

    fun onBind(isEnabled: Boolean, missionDetail: MissionDetail, position: Int) {
        itemView.isEnabled = isEnabled
        title.text = missionDetail.habit.title
        setPriorityTag(priorityTag, missionDetail.habit.priority, isEnabled)
        setTime(time, missionDetail.habit.startTime, missionDetail.habit.endTime)
        setProgressBar(missionDetail)
        itemView.setOnClickListener {
            if (!isEnabled) return@setOnClickListener
            missionClickedCallback.onMissionClicked(missionDetail)
        }
    }

    private fun setProgressBar(missionDetail: MissionDetail) {
        var progressMaxValue = 1
        var progressValue = 0
        var progressText = ""
        when (missionDetail.habit.type) {
            Type.TIMER ->
                missionDetail.mission.timer?.let {
                    progressMaxValue = it.duration.toInt()
                    progressValue = it.elapsedTime.toInt()
                    progressText = it.progressText
                }

            Type.COUNTER ->
                missionDetail.mission.counter?.let {
                    progressMaxValue = it.targetCount
                    progressValue = it.currentCount
                    progressText = it.progressText
                }
        }
        progressBar.max = progressMaxValue
        progressBar.progress = progressValue
        progress.text = progressText
    }
}