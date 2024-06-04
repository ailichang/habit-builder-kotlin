package com.habitbuilder.mission

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.habitbuilder.R
import com.habitbuilder.habit.data.Type
import com.habitbuilder.mission.data.MissionDetail
import com.habitbuilder.util.TimeUtil
import com.habitbuilder.util.WidgetFormatter.Companion.setCondition
import com.habitbuilder.util.WidgetFormatter.Companion.setTime
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.mission.data.Mission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MissionDetailDialogFragment(private val missionDetailDialogCallback: MissionDetailDialogCallback): DialogFragment() {
    interface MissionDetailDialogCallback{
        fun onIncreaseExperiencePoints(habit: Habit)
        fun onUpdateMission(mission:Mission)
    }

    private lateinit var dialogView: View
    private lateinit var  counterView: View
    private lateinit var  timerView: View
    private lateinit var  timerStartButton: Button
    private lateinit var  timerCancelButton: Button
    private lateinit var  timerPauseButton: Button
    private lateinit var  timerResetButton: Button
    private lateinit var  counterPlusButton: Button
    private lateinit var  counterMinusButton: Button
    private lateinit var  counterResetButton: Button
    private lateinit var  timerText: TextView
    private lateinit var  counterNum: TextView
    private var currentMissionDetail: MissionDetail? = null
    private var timerRemainingTime: MutableLiveData<Long>? = null
    private var counterCurrentCount: MutableLiveData<Int>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (requireArguments().containsKey(MissionFragment.MISSION_KEY)) {
            currentMissionDetail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireArguments().getParcelable(MissionFragment.MISSION_KEY, MissionDetail::class.java)
            } else {
                requireArguments().getParcelable(MissionFragment.MISSION_KEY)
            }
        }

        val dialogBuilder = MaterialAlertDialogBuilder(requireActivity())
        dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_mission_detail, null, false)
        val titleView = requireActivity().layoutInflater.inflate(
            R.layout.dialog_title,
            /*root=*/null,
            /*attachToRoot=*/false
        )
        val title = titleView.findViewById<TextView>(R.id.dialog_title)
        currentMissionDetail?.let {
            title.text =
                getString(
                    R.string.dialog_complete_mission_title,
                    it.habit.title
                )
        }

        dialogBuilder
            .setCustomTitle(titleView)
            .setView(dialogView)
            .setPositiveButton(R.string.dialog_complete) { _, _ ->
                currentMissionDetail?.apply{
                    this.mission.setMissionCompleted(true)
                    missionDetailDialogCallback.onIncreaseExperiencePoints(this.habit)
                }
                dismiss()
            }
            .setNegativeButton(R.string.dialog_cancel) { _, _ -> dismiss() }
        return dialogBuilder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        counterView = dialogView.findViewById(R.id.mission_counter_view)
        timerView = dialogView.findViewById(R.id.mission_timer_view)
        timerText = dialogView.findViewById(R.id.mission_timer_text)
        counterNum = dialogView.findViewById(R.id.mission_counter_number)
        timerStartButton = dialogView.findViewById(R.id.timer_start_button)
        timerCancelButton = dialogView.findViewById(R.id.timer_cancel_button)
        timerPauseButton = dialogView.findViewById(R.id.timer_pause_button)
        timerResetButton = dialogView.findViewById(R.id.timer_reset_button)
        counterMinusButton = dialogView.findViewById(R.id.counter_minus_button)
        counterPlusButton = dialogView.findViewById(R.id.counter_plus_button)
        counterResetButton = dialogView.findViewById(R.id.counter_reset_button)
        val condition = dialogView.findViewById<TextView>(R.id.mission_condition)
        val location = dialogView.findViewById<TextView>(R.id.mission_location)
        val time = dialogView.findViewById<TextView>(R.id.mission_time)
        val reward = dialogView.findViewById<TextView>(R.id.mission_reward)
        val note = dialogView.findViewById<TextView>(R.id.mission_note)

        currentMissionDetail?.let {
            val experiencePointsText = dialogView.resources.getQuantityString(
                R.plurals.experience_points,
                it.habit.experiencePoints.toInt(),
                it.habit.experiencePoints.toInt()
            )
            reward.text = experiencePointsText
            setCondition(condition, it.habit)
            location.text = if (TextUtils.isEmpty(it.habit.location)) location.context.getString(R.string.location_anywhere) else it.habit.location
            setTime(time, it.habit.startTime, it.habit.endTime)
            note.text = if (TextUtils.isEmpty(it.habit.description)) note.context.getString(R.string.note_none) else it.habit.description

            when (it.habit.type) {
                Type.TIMER -> setTimerView()
                Type.COUNTER -> setCounterView()
            }
        }
        return dialogView
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        currentMissionDetail?.let {
            if (it.habit.type === Type.TIMER){
                it.mission.timer?.let { timer ->
                    if(timer.isTimerInitialized && !timer.isTimerFinished){
                        timer.pause()
                    }
                }
            }
            missionDetailDialogCallback.onUpdateMission(it.mission)
        }
        timerRemainingTime?.removeObservers(this)
        counterCurrentCount?.removeObservers(this)
    }

    private fun setTimerView() {
        currentMissionDetail?.let {detail ->
            detail.mission.timer?.let { timer ->
                counterView.visibility = View.GONE
                timerView.visibility = View.VISIBLE
                timerStartButton.setText(if (timer.elapsedTime == 0L) R.string.timer_start else R.string.timer_resume)
                timerStartButton.visibility = if (timer.isTargetReached) View.GONE else View.VISIBLE
                timerResetButton.visibility = if (timer.isTargetReached) View.VISIBLE else View.GONE
                timerCancelButton.visibility = if (timer.isTargetReached) View.GONE else View.VISIBLE
                timerCancelButton.isEnabled = timer.isTargetReached
                timerPauseButton.visibility = View.GONE
                timer.remainingTime
                timerRemainingTime = timer.remainingTime
                timerText.text = timer.remainingTimeText
                timerRemainingTime?.observe(this) { time: Long ->
                    timerText.text =
                        TimeUtil.secondsToHMSString(time, TimeUtil.DurationStyle.COLON)
                    if (time == 0L) {
                        timerResetButton.visibility = View.VISIBLE
                        timerStartButton.visibility = View.GONE
                        timerPauseButton.visibility = View.GONE
                        timerCancelButton.visibility = View.GONE
                        detail.mission.setMissionCompleted(true)
                        missionDetailDialogCallback.onIncreaseExperiencePoints(detail.habit)
                        dismiss()
                    }
                }
                timerResetButton.setOnClickListener {
                    timer.reset()
                    timerResetButton.visibility = View.GONE
                    timerStartButton.setText(R.string.timer_start)
                    timerStartButton.visibility = View.VISIBLE
                    timerPauseButton.visibility = View.GONE
                    timerCancelButton.visibility = View.VISIBLE
                    timerCancelButton.isEnabled = false
                }
                timerStartButton.setOnClickListener {
                    timer.start()
                    timerStartButton.visibility = View.GONE
                    timerResetButton.visibility = View.GONE
                    timerPauseButton.visibility = View.VISIBLE
                    timerCancelButton.visibility = View.VISIBLE
                    timerCancelButton.isEnabled = true
                }
                timerPauseButton.setOnClickListener {
                    timer.pause()
                    timerStartButton.setText(R.string.timer_resume)
                    timerStartButton.visibility = View.VISIBLE
                    timerPauseButton.visibility = View.GONE
                    timerCancelButton.visibility = View.VISIBLE
                    timerCancelButton.isEnabled = true
                }
                timerCancelButton.setOnClickListener {
                    timer.cancel()
                    timerStartButton.setText(R.string.timer_start)
                    timerStartButton.visibility = View.VISIBLE
                    timerPauseButton.visibility = View.GONE
                    timerCancelButton.isEnabled = false
                }
            }
        }
    }

    private fun setCounterView() {
        currentMissionDetail?.let {detail ->
            detail.mission.counter?.let { counter ->
                counterView.visibility = View.VISIBLE
                timerView.visibility = View.GONE
                counterCurrentCount = counter.currentCountLiveData
                counterCurrentCount?.observe(viewLifecycleOwner) {
                    counterNum.text = counter.progressText
                    if (counter.isTargetReached) {
                        detail.mission.setMissionCompleted(true)
                        missionDetailDialogCallback.onIncreaseExperiencePoints(detail.habit)
                        dismiss()
                    }
                }
                counterPlusButton.setOnClickListener {
                    counter.add()
                }
                counterMinusButton.setOnClickListener {
                    counter.minus()
                }
                counterResetButton.setOnClickListener {
                    counter.reset()
                }
            }
        }
    }
}