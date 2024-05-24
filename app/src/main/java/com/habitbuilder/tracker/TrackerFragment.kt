package com.habitbuilder.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.habitbuilder.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class TrackerFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_tracker, container, false)
        val appBarLayout = requireActivity().findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout.setExpanded(false, false)
        val collapsingToolbarLayout =
            requireActivity().findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbarLayout.visibility = View.GONE
        val recyclerView = view.findViewById<RecyclerView>(R.id.tracker_recycler_view)
        val trackerAdapter = TrackerAdapter()
        recyclerView.adapter = trackerAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        val trackerViewModel:TrackerViewModel by viewModels<TrackerViewModel>()
        val currentMonthText = view.findViewById<TextView>(R.id.tracker_month_text)
        val today = LocalDate.now()
        val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM, y")
        val monthText = dateTimeFormatter.format(today)
        currentMonthText.text = monthText

        trackerViewModel.currentLocalDate.value = LocalDate.now()
        trackerViewModel.monthlyHabitRecordList.observe(viewLifecycleOwner, trackerAdapter::submitList)
        return view
    }
}