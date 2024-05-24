package com.habitbuilder.habit.editor

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.habitbuilder.R
import com.habitbuilder.util.TimeUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Calendar
import java.util.Locale

class TimerDurationPickerFragment (private val onTimePickedListener: OnTimePickedListener): DialogFragment() {
    interface OnTimePickedListener {
        fun onTimePicked(time: String)
    }

    private lateinit var dialogView: View
    private lateinit var timerDurationPicker: TimerPicker
    private var calendar: Calendar? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = MaterialAlertDialogBuilder(requireActivity())
        dialogView =
            requireActivity().layoutInflater.inflate(R.layout.dialog_timer_duration, null, false)
        dialogBuilder.setView(dialogView)
            .setPositiveButton(R.string.dialog_complete) { _, _ ->
                val time = String.format(
                    Locale.getDefault(),
                    "%02d:%02d:%02d",
                    timerDurationPicker.currentHour,
                    timerDurationPicker.currentMinute,
                    timerDurationPicker.currentSecond
                )
                onTimePickedListener.onTimePicked(time)
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
        return dialogView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timerDurationPicker = view.findViewById(R.id.timer_duration_picker)
    }

    override fun onStart() {
        super.onStart()
        calendar?.let {
            timerDurationPicker.setTime(it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE), it.get(Calendar.SECOND))
        }
    }

    fun setDuration(time: String) {
        val seconds: Long = TimeUtil.stringHMSToSeconds(time)
        calendar = Calendar.getInstance()
        calendar?.let {
            it[Calendar.HOUR_OF_DAY] = 0
            it[Calendar.MINUTE] = 0
            it[Calendar.SECOND] = seconds.toInt()
        }
    }
}