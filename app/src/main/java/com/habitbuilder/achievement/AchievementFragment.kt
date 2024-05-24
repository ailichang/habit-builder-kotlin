package com.habitbuilder.achievement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.habitbuilder.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AchievementFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View = inflater.inflate(R.layout.fragment_achievement, container, false)
        val appBarLayout:AppBarLayout = requireActivity().findViewById(R.id.app_bar_layout)
        appBarLayout.setExpanded(false, false)
        val collapsingToolbarLayout:CollapsingToolbarLayout = requireActivity().findViewById(R.id.collapsing_toolbar)
        collapsingToolbarLayout.visibility = View.GONE
        val recyclerView:RecyclerView = view.findViewById(R.id.achievement_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
        val achievementAdapter = com.habitbuilder.achievement.AchievementAdapter()
        recyclerView.adapter = achievementAdapter
        val achievementViewModel: com.habitbuilder.achievement.AchievementViewModel by viewModels<com.habitbuilder.achievement.AchievementViewModel> ()

        achievementViewModel.getAchievementDetails().observe(viewLifecycleOwner){ list ->
            if(list != null){
                achievementAdapter.submitList(list)
            }
        }
        return view
    }
}