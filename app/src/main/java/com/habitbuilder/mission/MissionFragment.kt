package com.habitbuilder.mission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.habitbuilder.R
import com.habitbuilder.mission.data.MissionDetail
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.habitbuilder.achievement.data.ExperienceUpdateType
import com.habitbuilder.habit.data.Habit
import com.habitbuilder.mission.MissionViewHolder.MissionClickedCallback
import com.habitbuilder.mission.data.Mission
import com.habitbuilder.mission.dialog.MissionDetailDialogFragment
import com.habitbuilder.mission.dialog.MissionDetailDialogFragment.MissionDetailDialogCallback
import java.time.LocalDate


class MissionFragment: Fragment(), MissionClickedCallback, MissionDetailDialogCallback{
    companion object{
        const val MISSION_KEY = "MISSION"
    }

    private lateinit var emptyMissionsMessage: TextView
    private lateinit var emptyCompletedListMessage: TextView
    private lateinit var completedRecyclerView: RecyclerView
    private lateinit var completedAdapter: MissionAdapter
    private lateinit var incompleteRecyclerView: RecyclerView
    private lateinit var incompleteAdapter: MissionAdapter
    private lateinit var missionListViewModel: MissionListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_mission, container, false)
        val appBarLayout = requireActivity().findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout.setExpanded(false, false)
        val collapsingToolbarLayout =
            requireActivity().findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbarLayout.visibility = View.GONE
        emptyMissionsMessage = view.findViewById(R.id.empty_missions_text)
        emptyMissionsMessage.visibility = View.GONE
        emptyCompletedListMessage = view.findViewById(R.id.empty_completed_missions_text)
        completedRecyclerView = view.findViewById(R.id.completed_mission_recyclerview)
        incompleteRecyclerView = view.findViewById(R.id.mission_recyclerview)
        missionListViewModel =
            ViewModelProvider(requireActivity())[MissionListViewModel::class.java]
        completedAdapter = MissionAdapter(isEnabled = false, this)
        val completeItemTouchHelper =
            ItemTouchHelper(MissionItemTouchCallback(false, completedAdapter, missionListViewModel))
        completeItemTouchHelper.attachToRecyclerView(completedRecyclerView)
        completedRecyclerView.layoutManager = LinearLayoutManager(view.context)
        completedRecyclerView.adapter = completedAdapter
        incompleteAdapter = MissionAdapter(isEnabled = true, this)
        val incompleteItemTouchHelper =
            ItemTouchHelper(MissionItemTouchCallback(true, incompleteAdapter, missionListViewModel))
        incompleteItemTouchHelper.attachToRecyclerView(incompleteRecyclerView)
        incompleteRecyclerView.layoutManager = LinearLayoutManager(view.context)
        incompleteRecyclerView.adapter = incompleteAdapter

        missionListViewModel.getCompletedDailyMissions().observe(viewLifecycleOwner) { missions ->
            if (missions != null) {
                completedAdapter.missionList = missions
                setEmptyMessages()
            }
        }

        missionListViewModel.getIncompleteDailyMissions().observe(viewLifecycleOwner) { missions ->
            if (missions != null) {
                incompleteAdapter.missionList = missions
                setEmptyMessages()
            }
        }

        missionListViewModel.currentLocalDate.observe(viewLifecycleOwner){
            missionListViewModel.scheduleNewMission(it)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        missionListViewModel.currentLocalDate.value = LocalDate.now()
    }

    private fun setEmptyMessages() {
        val isIncompleteListEmpty: Boolean = incompleteAdapter.itemCount == 0
        val isCompletedListEmpty: Boolean = completedAdapter.itemCount == 0
        completedRecyclerView.visibility = if (isCompletedListEmpty) View.GONE else View.VISIBLE
        incompleteRecyclerView.visibility = if (isIncompleteListEmpty) View.GONE else View.VISIBLE
        emptyCompletedListMessage.visibility = if (isCompletedListEmpty) View.VISIBLE else View.GONE
        emptyMissionsMessage.visibility =
            if (isCompletedListEmpty && isIncompleteListEmpty) View.VISIBLE else View.GONE
    }

    override fun onIncreaseExperiencePoints(habit: Habit) {
        missionListViewModel.increaseExperiencePoints(habit)
    }

    override fun onUpdateMission(mission: Mission) {
        missionListViewModel.updateMission(mission)
    }

    override fun onMissionClicked(missionDetail: MissionDetail) {
        val missionDetailFragment = MissionDetailDialogFragment(this)
        val args = Bundle()
        args.putParcelable(MISSION_KEY, missionDetail)
        missionDetailFragment.arguments = args
        missionDetailFragment.show(parentFragmentManager, MissionViewHolder.DIALOG_MISSION_DETAIL_TAG)
    }
}

class MissionItemTouchCallback(private var isItemEnabled: Boolean, private var adapter:MissionAdapter, private var viewModel:MissionListViewModel) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (isItemEnabled) makeMovementFlags( (ItemTouchHelper.UP or ItemTouchHelper.DOWN), ItemTouchHelper.END) else makeMovementFlags( 0,0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.END -> {
                val missionDetail:MissionDetail = adapter.missionList[viewHolder.bindingAdapterPosition]
                val isCompleted: Boolean = !missionDetail.mission.isCompleted
                missionDetail.mission.setMissionCompleted(isCompleted)
                viewModel.setMissionComplete(missionDetail, if(isCompleted) ExperienceUpdateType.INCREASE else ExperienceUpdateType.DECREASE)
                adapter.onItemSwiped(viewHolder.bindingAdapterPosition)
            }
            else ->{}
        }
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

}
