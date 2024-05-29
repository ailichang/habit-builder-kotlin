package com.habitbuilder.habit.editor

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.habitbuilder.R
import com.habitbuilder.habit.HabitFragment.Companion.HABIT_KEY
import com.habitbuilder.habit.data.Category
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.habit.data.Priority
import com.habitbuilder.habit.data.Type
import com.habitbuilder.util.TimeUtil
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@AndroidEntryPoint
class HabitEditorFragment : Fragment(){
    companion object{
        const val IS_NEW_HABIT_KEY = "IS_NEW_HABIT"
        const val DURATION_PICKER_TAG = "TIMER_DURATION_PICKER"
        const val TIME_PICKER_TAG = "TIME_PICKER"
    }
    private var isNew = false
    private var hasUpdatedMissionType = false
    private var previousHabit:Habit? = null
    private var currentHabit: Habit? = null
    private val habitEditorViewModel by viewModels<HabitEditorViewModel>()
    private lateinit var startTimePicker: MaterialTimePicker
    private lateinit var endTimePicker: MaterialTimePicker
    private lateinit var timerDurationPicker: TimerDurationPickerFragment
    private lateinit var typeRadioGroup: RadioGroup
    private lateinit var counterButton: RadioButton
    private lateinit var timerButton: RadioButton
    private lateinit var habitTitleInput: TextInputLayout
    private lateinit var counterInput: TextInputLayout
    private lateinit var durationInput: TextInputLayout
    private lateinit var locationInput: TextInputLayout
    private lateinit var startTimeInput: TextInputLayout
    private lateinit var endTimeInput: TextInputLayout
    private lateinit var descriptionInput: TextInputLayout
    private lateinit var priorityChipGroup: ChipGroup
    private lateinit var categoryChipGroup: ChipGroup
    private lateinit var scheduledDayChipGroup: ChipGroup
    private lateinit var scheduleMsg: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hasUpdatedMissionType = false
        val arguments = requireArguments()
        if (arguments.containsKey(IS_NEW_HABIT_KEY)) {
            isNew = arguments.getBoolean(IS_NEW_HABIT_KEY)
        }
        if (arguments.containsKey(HABIT_KEY)) {
            currentHabit = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments.getParcelable(HABIT_KEY, Habit::class.java)
            } else {
                arguments.getParcelable(HABIT_KEY)
            }
            previousHabit = currentHabit
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_edit_habit, container, false)
        val appBarLayout = requireActivity().findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout.setExpanded(false, false)
        val collapsingToolbarLayout =
            requireActivity().findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbarLayout.title =
            getString(if (isNew) R.string.create_habit_toolbar_title else R.string.edit_habit_toolbar_title)
        collapsingToolbarLayout.visibility = View.VISIBLE
        val materialToolbar = requireActivity().findViewById<MaterialToolbar>(R.id.tool_bar)
        materialToolbar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        materialToolbar.setNavigationIconTint(requireContext().getColor(R.color.colorOnPrimary))
        materialToolbar.setNavigationOnClickListener { onBackPressed() }
        val bottomNavigationView =
            requireActivity().findViewById<NavigationBarView>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(
                    if (isNew) R.menu.top_create_menu else R.menu.top_edit_menu,
                    menu
                )
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.action_save) {
                    if (save()) {
                        onBackPressed()
                        return true
                    }
                } else if (menuItem.itemId == R.id.action_delete) {
                    val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
                    val alertDialog = dialogBuilder.setTitle(R.string.permanently_delete_habit)
                        .setMessage(R.string.permanently_delete_habit_message)
                        .setPositiveButton(R.string.action_delete) { _, _ ->
                            delete()
                            onBackPressed()
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ -> }
                        .create()
                    alertDialog.show()
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.STARTED)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        habitTitleInput = view.findViewById(R.id.habit_title_text_field)
        counterButton = view.findViewById(R.id.habit_counter_radio_button)
        typeRadioGroup = view.findViewById(R.id.habit_type_radiogroup)
        timerButton = view.findViewById(R.id.habit_timer_radio_button)
        counterInput = view.findViewById(R.id.habit_counter_text_field)
        durationInput = view.findViewById(R.id.habit_timer_text_field)
        startTimeInput = view.findViewById(R.id.habit_start_time_text_field)
        endTimeInput = view.findViewById(R.id.habit_end_time_text_field)
        locationInput = view.findViewById(R.id.habit_location_text_field)
        priorityChipGroup = view.findViewById(R.id.habit_priority_chip_group)
        categoryChipGroup = view.findViewById(R.id.habit_category_chip_group)
        scheduledDayChipGroup = view.findViewById(R.id.weekday_chip_group)
        descriptionInput = view.findViewById(R.id.habit_description_text_field)
        val titleHelpIcon = view.findViewById<ImageView>(R.id.habit_title_help)
        val conditionHelpIcon = view.findViewById<ImageView>(R.id.habit_condition_help)
        val priorityHelpIcon = view.findViewById<ImageView>(R.id.habit_priority_help)
        val scheduleHelpIcon = view.findViewById<ImageView>(R.id.habit_schedule_help)
        scheduleMsg = view.findViewById(R.id.habit_schedule_hint)
        priorityChipGroup.check(R.id.habit_priority_low_button)
        typeRadioGroup.check(R.id.habit_counter_radio_button)
        counterButton.isActivated = true
        counterButton.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            counterInput.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        timerButton.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            durationInput.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        timerDurationPicker = TimerDurationPickerFragment(object: TimerDurationPickerFragment.OnTimePickedListener{
            override fun onTimePicked(time: String) {
                durationInput.editText?.setText(time)
            }
        })
        durationInput.editText?.setOnClickListener {
            if (!TextUtils.isEmpty(durationInput.editText?.text.toString())) {
                timerDurationPicker.setDuration(durationInput.editText?.text.toString())
            }
            timerDurationPicker.show(
                parentFragmentManager,
                DURATION_PICKER_TAG
            )
        }
        startTimePicker = createTimePicker(R.string.time_picker_select_start_time, currentHabit?.startTime, startTimeInput)
        endTimePicker = createTimePicker(R.string.time_picker_select_end_time, currentHabit?.endTime, endTimeInput)

        startTimeInput.editText?.setOnClickListener {
            startTimePicker.show(
                parentFragmentManager,
                TIME_PICKER_TAG
            )
        }
        endTimeInput.editText?.setOnClickListener {
            endTimePicker.show(
                parentFragmentManager,
                TIME_PICKER_TAG
            )
        }
        titleHelpIcon.setOnClickListener {
            showHintDialog(
                R.string.habit_editor_title,
                R.string.habit_title_hint
            )
        }
        conditionHelpIcon.setOnClickListener {
            showHintDialog(
                R.string.habit_complete_condition,
                R.string.habit_condition_hint
            )
        }
        priorityHelpIcon.setOnClickListener {
            showHintDialog(
                R.string.habit_priority,
                R.string.priority_reward_hint
            )
        }
        scheduleHelpIcon.setOnClickListener {
            showHintDialog(
                R.string.habit_mission_schedule,
                R.string.habit_schedule_hint
            )
        }


        if (!isNew) {
            setUpHabitEditor()
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun save(): Boolean {
        if (validateCurrentHabit()) {
            prepareCurrentHabit()
            if (isNew) {
                currentHabit?.let { createHabit(it) }
            } else {
                currentHabit?.let { newHabit ->
                    previousHabit?.let { oldHabit ->
                        updateHabit(oldHabit, newHabit)
                    }
                }
            }
            return true
        }
        return false
    }

    private fun delete() {
        prepareCurrentHabit()
        currentHabit?.let { deleteHabit(it) }
    }

    private fun createTimePicker(@StringRes titleId: Int, currentTime:String?, inputLayout: TextInputLayout):MaterialTimePicker {
        val timePickerBuilder = MaterialTimePicker.Builder()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        timePickerBuilder
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setTitleText(titleId)
        if (!TextUtils.isEmpty(currentTime)) {
            val time = LocalTime.parse(currentTime, timeFormatter)
            timePickerBuilder.setHour(time.hour).setMinute(time.minute)
        }
        val timePicker = timePickerBuilder.build()
        timePicker.addOnPositiveButtonClickListener {
            val localTime =
                LocalTime.of(timePicker.hour, timePicker.minute)
            inputLayout.editText?.setText(localTime.format(timeFormatter))
        }
        return timePicker
    }
    private fun showHintDialog(@StringRes titleId: Int, @StringRes messageId: Int) {
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        alertDialogBuilder
            .setTitle(titleId)
            .setMessage(messageId)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        alertDialogBuilder.create().show()
    }

    private fun createHabit(habit: Habit) {
        habitEditorViewModel.create(habit)
        Snackbar.make(requireView(),getText(R.string.message_habit_created), Snackbar.LENGTH_SHORT ).show()
    }

    private fun updateHabit(oldHabit:Habit, newHabit:Habit) {
        habitEditorViewModel.update(oldHabit, newHabit)
        Snackbar.make(requireView(),getText(R.string.message_habit_updated), Snackbar.LENGTH_SHORT ).show()
    }

    private fun deleteHabit(habit: Habit) {
        habitEditorViewModel.delete(habit)
        Snackbar.make(requireView(),getText(R.string.message_habit_deleted), Snackbar.LENGTH_SHORT ).show()
    }

    private fun setUpHabitEditor() {
        habitTitleInput.editText?.setText(currentHabit?.title)
        startTimeInput.editText?.setText(currentHabit?.startTime)
        endTimeInput.editText?.setText(currentHabit?.endTime)
        locationInput.editText?.setText(currentHabit?.location)
        descriptionInput.editText?.setText(currentHabit?.description)
        currentHabit?.targetDuration?.let {
            timerDurationPicker.setDuration(TimeUtil.secondsToHMSString(it, TimeUtil.DurationStyle.COLON))
        }
        when (currentHabit?.type) {
            Type.COUNTER -> {
                typeRadioGroup.check(R.id.habit_counter_radio_button)
                currentHabit?.targetCount?.let {
                    counterInput.editText?.setText(it.toString())
                }
                counterButton.isActivated = true
            }

            Type.TIMER -> {
                typeRadioGroup.check(R.id.habit_timer_radio_button)
                currentHabit?.targetDuration?.let {
                    durationInput.editText?.setText(TimeUtil.secondsToHMSString(it, TimeUtil.DurationStyle.COLON))
                }
                timerButton.isActivated = true
            }
            else -> {}
        }
        when (currentHabit?.priority) {
            Priority.HIGH -> priorityChipGroup.check(R.id.habit_priority_high_button)
            Priority.MEDIUM-> priorityChipGroup.check(R.id.habit_priority_medium_button)
            Priority.LOW -> priorityChipGroup.check(R.id.habit_priority_low_button)
            else -> {}
        }
        when (currentHabit?.category) {
            Category.FINANCE -> categoryChipGroup.check(R.id.finance_button)
            Category.FITNESS -> categoryChipGroup.check(R.id.fitness_button)
            Category.HEALTH -> categoryChipGroup.check(R.id.health_button)
            Category.HOBBY -> categoryChipGroup.check(R.id.hobby_button)
            Category.HOUSE_CHORE -> categoryChipGroup.check(R.id.house_chore_button)
            Category.LEARNING -> categoryChipGroup.check(R.id.learning_button)
            Category.WORK -> categoryChipGroup.check(R.id.work_button)
            else -> {}
        }
        scheduledDayChipGroup.clearCheck()
        currentHabit?.scheduledDays?.let {
            for (day in 0 until it.size) {
                if (it[day]) {
                    when (day) {
                        0 -> scheduledDayChipGroup.check(R.id.chip_monday)
                        1 -> scheduledDayChipGroup.check(R.id.chip_tuesday)
                        2 -> scheduledDayChipGroup.check(R.id.chip_wednesday)
                        3 -> scheduledDayChipGroup.check(R.id.chip_thursday)
                        4 -> scheduledDayChipGroup.check(R.id.chip_friday)
                        5 -> scheduledDayChipGroup.check(R.id.chip_saturday)
                        6 -> scheduledDayChipGroup.check(R.id.chip_sunday)
                        else -> {}
                    }
                }
            }
        }
    }

    private fun prepareCurrentHabit() {
        val title = habitTitleInput.editText?.text.toString()
        val startTime = startTimeInput.editText?.text.toString()
        val endTime = endTimeInput.editText?.text.toString()
        val location = locationInput.editText?.text.toString()
        val description = descriptionInput.editText?.text.toString()
        val priority:Priority = getPriority()
        hasUpdatedMissionType = currentHabit?.type !== getType()
        currentHabit = when(getType()){
            Type.COUNTER ->
                Habit(habitId = if(isNew) UUID.randomUUID() else currentHabit!!.habitId, title = title, description = description, startTime = startTime,
                    endTime = endTime, location = location, priority = priority,
                    category = getCategory(), scheduledDays = getScheduledDays(),
                    type = getType(), targetCount = getTargetCount())
            else ->
                Habit(habitId = if(isNew) UUID.randomUUID() else currentHabit!!.habitId, title = title, description = description, startTime = startTime,
                    endTime = endTime, location = location, priority = priority,
                    category = getCategory(), scheduledDays = getScheduledDays(),
                    type = getType(), targetDuration = getTargetDuration())

        }
    }

    private fun validateCurrentHabit(): Boolean {
        val isTitleValid = !TextUtils.isEmpty(habitTitleInput.editText?.text.toString())
        habitTitleInput.error = getString(R.string.error_empty_title)
        counterInput.error = getString(R.string.error_count_number)
        durationInput.error = getString(R.string.error_target_duration)
        habitTitleInput.isErrorEnabled = !isTitleValid
        val isCountValid = getType() == Type.COUNTER && getTargetCount() > 0
        val isDurationValid = getType() == Type.TIMER && getTargetDuration() > 0
        val isConditionValid = isCountValid || isDurationValid
        counterInput.isErrorEnabled = getType() !== Type.COUNTER || !isCountValid
        durationInput.isErrorEnabled = getType() !== Type.TIMER || !isDurationValid
        val isScheduledDaysSelected = scheduledDayChipGroup.checkedChipIds.size > 0
        scheduleMsg.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isScheduledDaysSelected) R.color.colorOnSurface else R.color.colorError
            )
        )
        return isTitleValid && isConditionValid && isScheduledDaysSelected
    }

    private fun getType(): Type {
        return if (counterButton.isChecked) Type.COUNTER else Type.TIMER
    }

    private fun getTargetCount(): Int {
        val countText = counterInput.editText?.text.toString()
        return if (TextUtils.isEmpty(countText)) 0 else countText.toInt()
    }

    private fun getTargetDuration(): Long {
        val durationText = durationInput.editText?.text.toString()
        return if (TextUtils.isEmpty(durationText)) 0 else TimeUtil.stringHMSToSeconds(durationText)
    }

    private fun getScheduledDays(): ArrayList<Boolean> {
        val scheduledDays:ArrayList<Boolean> = ArrayList(BooleanArray(DayOfWeek.entries.size).toList())
        for (checkedChipId in scheduledDayChipGroup.checkedChipIds) {
            when (checkedChipId) {
                R.id.chip_monday -> {
                    scheduledDays[0] = true
                }
                R.id.chip_tuesday -> {
                    scheduledDays[1] = true
                }
                R.id.chip_wednesday -> {
                    scheduledDays[2] = true
                }
                R.id.chip_thursday -> {
                    scheduledDays[3] = true
                }
                R.id.chip_friday -> {
                    scheduledDays[4] = true
                }
                R.id.chip_saturday -> {
                    scheduledDays[5] = true
                }
                R.id.chip_sunday -> {
                    scheduledDays[6] = true
                }
            }
        }
        return scheduledDays
    }

    private fun getCategory(): Category {
        return when (categoryChipGroup.checkedChipId) {
                R.id.finance_button -> {
                    Category.FINANCE
                }
                R.id.fitness_button -> {
                    Category.FITNESS
                }
                R.id.health_button -> {
                    Category.HEALTH
                }
                R.id.hobby_button -> {
                    Category.HOBBY
                }
                R.id.house_chore_button -> {
                    Category.HOUSE_CHORE
                }
                R.id.learning_button -> {
                    Category.LEARNING
                }
                R.id.work_button -> {
                    Category.WORK
                }
                else -> {
                    Category.NONE
                }
            }
    }

    private fun getPriority(): Priority {
        return when (priorityChipGroup.checkedChipId) {
            R.id.habit_priority_high_button -> {
                Priority.HIGH
            }
            R.id.habit_priority_medium_button -> {
                Priority.MEDIUM
            }
            else -> {
                Priority.LOW
            }
        }
    }

}