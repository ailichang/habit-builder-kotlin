package com.habitbuilder.mission

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.habitbuilder.R
import com.habitbuilder.habit.data.Type
import com.habitbuilder.mission.data.MissionDetail
import com.habitbuilder.mission.dialog.MissionDetailDialogFragment
import com.habitbuilder.util.WidgetFormatter.Companion.setPriorityTag
import com.habitbuilder.util.WidgetFormatter.Companion.setTime
import com.google.android.material.chip.Chip

class MissionViewHolder(itemView: View, fragmentManager: FragmentManager): ViewHolder(itemView) {

    private val fragmentManager: FragmentManager
    private val title: TextView
    private val progress: TextView
    private val time: TextView
    private val progressBar: ProgressBar
    private val priorityTag: Chip

    companion object {
        private const val DIALOG_MISSION_DETAIL_TAG = "dialog_mission_detail"
    }
    init {
        this.fragmentManager = fragmentManager
        title = itemView.findViewById(R.id.mission_title)
        priorityTag = itemView.findViewById(R.id.mission_priority)
        time = itemView.findViewById(R.id.mission_time)
        progressBar = itemView.findViewById(R.id.mission_progressbar)
        progress = itemView.findViewById(R.id.mission_progressbar_text)
    }

    fun onBind(isEnabled: Boolean, mission: MissionDetail, position: Int) {
        itemView.isEnabled = isEnabled
        title.text = mission.habit.title
        setPriorityTag(priorityTag, mission.habit.priority, isEnabled)
        setTime(time, mission.habit.startTime, mission.habit.endTime)
        setProgressBar(mission)
        itemView.setOnClickListener {
            if (!isEnabled) return@setOnClickListener
            val missionDetailFragment = MissionDetailDialogFragment()
            val args = Bundle()
            args.putParcelable(MissionFragment.MISSION_KEY, mission)
            missionDetailFragment.arguments = args
            missionDetailFragment.show(fragmentManager, DIALOG_MISSION_DETAIL_TAG)
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