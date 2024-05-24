package com.habitbuilder.habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.habitbuilder.R
import com.habitbuilder.habit.editor.HabitEditorFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HabitFragment: Fragment(){
    companion object{
        const val HABIT_KEY = "HABIT"
        private const val TAG = "HabitFragment"
    }

    private var habitAdapter: HabitAdapter = HabitAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_habit, container, false)
        val appBarLayout = requireActivity().findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout.setExpanded(false, false)
        val collapsingToolbarLayout =
            requireActivity().findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbarLayout.visibility = View.GONE
        val bottomNavigationView =
            requireActivity().findViewById<NavigationBarView>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.VISIBLE
        val addButton = view.findViewById<FloatingActionButton>(R.id.add_habit_fab)
        val habitRecyclerView = view.findViewById<RecyclerView>(R.id.habit_recyclerview)
        val habitListViewModel: HabitListViewModel by viewModels <HabitListViewModel>()
        habitRecyclerView.layoutManager = LinearLayoutManager(view.context)
        habitRecyclerView.adapter = habitAdapter
        habitListViewModel.getHabitList().observe(requireActivity()) { habitList ->
            if (habitList != null) {
                habitAdapter.submitList(habitList)
            }
        }
        addButton.setOnClickListener {
            val args = Bundle()
            args.putBoolean(HabitEditorFragment.IS_NEW_HABIT_KEY, true)
            val tag: String = HabitEditorFragment::class.java.name
            val habitEditorFragment = HabitEditorFragment()
            habitEditorFragment.arguments = args
            val fragmentManager =
                requireActivity().supportFragmentManager
            val transaction =
                fragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.enter_anim,
                R.anim.exit_anim,
                R.anim.enter_anim,
                R.anim.exit_anim
            )
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            transaction.replace(R.id.nav_host_fragment, habitEditorFragment, tag)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return view
    }

}