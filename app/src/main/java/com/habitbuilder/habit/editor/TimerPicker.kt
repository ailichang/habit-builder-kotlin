package com.habitbuilder.habit.editor

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.NumberPicker
import androidx.constraintlayout.widget.ConstraintLayout
import com.habitbuilder.R

class TimerPicker(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private var hourPicker: NumberPicker
    private var minutePicker: NumberPicker
    private var secondPicker: NumberPicker
    var currentHour = 0 //0-23
        private set(value) {
            field = value
            hourPicker.value = value
        }
    var currentMinute = 0 // 0-59
        private set(value) {
            field = value
            minutePicker.value = value
        }
    var currentSecond = 0 //0-59
        private set(value) {
            field = value
            secondPicker.value = value
        }

    init {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.picker_timer_duration, this, true)

        val formatter =
            NumberPicker.Formatter { value -> String.format("%02d", value) }

        hourPicker = view.findViewById(R.id.hour_duration_picker)
        minutePicker = view.findViewById(R.id.minute_duration_picker)
        secondPicker = view.findViewById(R.id.second_duration_picker)
        hourPicker.setFormatter(formatter)
        hourPicker.maxValue = 23
        hourPicker.minValue = 0
        hourPicker.value = currentHour
        minutePicker.setFormatter(formatter)
        minutePicker.maxValue = 59
        minutePicker.minValue = 0
        minutePicker.value = currentMinute
        secondPicker.setFormatter(formatter)
        secondPicker.maxValue = 59
        secondPicker.minValue = 0
        secondPicker.value = currentSecond

        hourPicker.setOnValueChangedListener { _, _, newVal ->
            currentHour = newVal
        }

        minutePicker.setOnValueChangedListener { _, _, newVal ->
            currentMinute = newVal
        }

        secondPicker.setOnValueChangedListener { _, _, newVal ->
            currentSecond = newVal
        }
    }

    fun setTime(hour: Int, minute: Int, second: Int) {
        currentHour = hour
        currentMinute = minute
        currentSecond = second
    }
}