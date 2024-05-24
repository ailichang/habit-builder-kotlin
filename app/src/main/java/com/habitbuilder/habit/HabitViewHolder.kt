package com.habitbuilder.habit

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.habitbuilder.R
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.editor.HabitEditorFragment
import com.habitbuilder.util.WidgetFormatter.Companion.setCategoryFilterTag
import com.habitbuilder.util.WidgetFormatter.Companion.setCondition
import com.habitbuilder.util.WidgetFormatter.Companion.setPriorityTag
import com.habitbuilder.util.WidgetFormatter.Companion.setScheduledDays
import com.habitbuilder.util.WidgetFormatter.Companion.setTime
import com.google.android.material.chip.Chip
import dagger.hilt.android.internal.managers.ViewComponentManager.FragmentContextWrapper


class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val expandableView: View
    private val title: TextView
    private val location: TextView
    private val condition: TextView
    private val reward: TextView
    private val time: TextView
    private val scheduledDays: TextView
    private val note: TextView
    private val priorityTag: Chip
    private val categoryTag: Chip
    private val editButton: Button
    companion object {
        private const val TAG = "HabitViewHolder"
    }
    init {
        title = itemView.findViewById(R.id.habit_title)
        location = itemView.findViewById(R.id.habit_location)
        condition = itemView.findViewById(R.id.habit_condition)
        categoryTag = itemView.findViewById(R.id.habit_category)
        priorityTag = itemView.findViewById(R.id.habit_priority)
        expandableView = itemView.findViewById(R.id.task_expandable_view)
        reward = itemView.findViewById(R.id.habit_reward)
        time = itemView.findViewById(R.id.habit_time)
        scheduledDays = itemView.findViewById(R.id.habit_scheduled_days)
        note = itemView.findViewById(R.id.habit_note)
        editButton = itemView.findViewById(R.id.edit_habit_button)
    }

    fun onBind(habit: Habit) {
        title.text = habit.title
        location.text =
            if (TextUtils.isEmpty(habit.location)) location.context.getString(R.string.location_anywhere) else habit.location
        reward.text = reward.resources.getQuantityString(
            R.plurals.experience_points,
            habit.experiencePoints.toInt(),
            habit.experiencePoints.toInt()
        )
        setCategoryFilterTag(categoryTag, habit.category)
        setPriorityTag(priorityTag, habit.priority, true)
        setCondition(condition, habit)
        setTime(time, habit.startTime, habit.endTime)
        setScheduledDays(scheduledDays, habit.scheduledDays)
        note.text =
            if (TextUtils.isEmpty(habit.description)) note.context.getString(R.string.note_none) else habit.description
        editButton.setOnClickListener {
            val args = Bundle()
            args.putBoolean(HabitEditorFragment.IS_NEW_HABIT_KEY, false)
            args.putParcelable(HabitFragment.HABIT_KEY, habit)
            val habitEditorFragment = HabitEditorFragment()
            habitEditorFragment.arguments = args
            val fragmentManager =
                ((itemView.context as FragmentContextWrapper).baseContext as AppCompatActivity).supportFragmentManager
            val transaction =
                fragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.enter_anim,
                R.anim.exit_anim,
                R.anim.enter_anim,
                R.anim.exit_anim
            )
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            transaction.replace(
                R.id.nav_host_fragment,
                habitEditorFragment,
                HabitEditorFragment::class.qualifiedName
            )
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}
