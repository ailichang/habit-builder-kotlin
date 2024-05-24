package com.habitbuilder.tracker

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.habitbuilder.R
import com.habitbuilder.util.WidgetFormatter.Companion.getCategoryColor
import com.habitbuilder.util.WidgetFormatter.Companion.getCategoryIcon

class TrackerViewHolder(itemView: View) : ViewHolder(itemView) {
    private var trackerTitle: TextView
    private var trackerStartDate: TextView
    private var habitGraphView: HabitGraphView

    init{
        trackerTitle = itemView.findViewById(R.id.tracker_title)
        trackerStartDate = itemView.findViewById(R.id.tracker_start_date)
        habitGraphView = itemView.findViewById(R.id.tracker_monthly_graph)
    }

    fun onBind(record: MonthlyHabitRecord) {
        trackerTitle.text = record.habit.title
        val backgroundColor = ContextCompat.getColor(trackerTitle.context, getCategoryColor(record.habit.category))
        trackerTitle.setBackgroundColor(backgroundColor)
        val icon = ContextCompat.getDrawable(trackerTitle.context, getCategoryIcon(record.habit.category))
        trackerTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
        trackerStartDate.text = record.habit.createdDate
        val categoryColor = ContextCompat.getColor(trackerTitle.context, getCategoryColor(record.habit.category))
        habitGraphView.setGraphData(record.year, record.month, categoryColor, record.habit.scheduledDays, record.monthlyCompleteTable)
    }
}